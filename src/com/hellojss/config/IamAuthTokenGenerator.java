package com.hellojss.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generates a temporary IAM authentication token for Amazon Aurora/RDS
 * using AWS Signature Version 4. Credentials are fetched automatically
 * from the EC2 Instance Metadata Service (IMDSv2). No external libraries needed.
 */
public class IamAuthTokenGenerator {

    private static final String ALGORITHM        = "AWS4-HMAC-SHA256";
    private static final String SERVICE          = "rds-db";
    private static final String AWS4_REQUEST     = "aws4_request";
    private static final int    TOKEN_EXPIRY_SEC = 900;
    private static final String IMDS_BASE        = "http://169.254.169.254";
    private static final int    IMDS_TIMEOUT_MS  = 2000;

    private final String region;

    public IamAuthTokenGenerator(String region) {
        this.region = region;
    }

    public String generate(String host, int port, String dbUser) throws Exception {
        ImdsCredentials creds = fetchCredentialsFromImds();
        return buildAuthToken(host, port, dbUser, creds);
    }

    private String buildAuthToken(String host, int port, String dbUser,
                                  ImdsCredentials creds) throws Exception {
        String dateTime        = utcDateTime();
        String date            = dateTime.substring(0, 8);
        String credentialScope = date + "/" + region + "/" + SERVICE + "/" + AWS4_REQUEST;
        String canonicalQuery  = buildCanonicalQueryString(dbUser, creds, credentialScope, dateTime);
        String canonicalHost   = host + ":" + port;
        String canonicalRequest =
                "GET\n/\n" + canonicalQuery + "\n"
                + "host:" + canonicalHost + "\n\n"
                + "host\n"
                + sha256Hex("");
        String stringToSign =
                ALGORITHM + "\n"
                + dateTime + "\n"
                + credentialScope + "\n"
                + sha256Hex(canonicalRequest);
        byte[] signingKey = deriveSigningKey(creds.secretAccessKey, date);
        String signature  = toHex(hmacSha256(signingKey, stringToSign));
        return host + ":" + port + "/?" + canonicalQuery + "&X-Amz-Signature=" + signature;
    }

    private String buildCanonicalQueryString(String dbUser, ImdsCredentials creds,
                                             String credentialScope, String dateTime)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Action=connect");
        sb.append("&DBUser=").append(sigV4Encode(dbUser));
        sb.append("&X-Amz-Algorithm=").append(ALGORITHM);
        sb.append("&X-Amz-Credential=")
          .append(sigV4Encode(creds.accessKeyId + "/" + credentialScope));
        sb.append("&X-Amz-Date=").append(dateTime);
        sb.append("&X-Amz-Expires=").append(TOKEN_EXPIRY_SEC);
        if (creds.sessionToken != null && !creds.sessionToken.isEmpty()) {
            sb.append("&X-Amz-Security-Token=").append(sigV4Encode(creds.sessionToken));
        }
        sb.append("&X-Amz-SignedHeaders=host");
        return sb.toString();
    }

    private byte[] deriveSigningKey(String secretKey, String dateStamp) throws Exception {
        byte[] kDate    = hmacSha256(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), dateStamp);
        byte[] kRegion  = hmacSha256(kDate, region);
        byte[] kService = hmacSha256(kRegion, SERVICE);
        return hmacSha256(kService, AWS4_REQUEST);
    }

    private ImdsCredentials fetchCredentialsFromImds() throws Exception {
        String imdsToken = getImdsSessionToken();
        String roleName  = httpGet(IMDS_BASE + "/latest/meta-data/iam/security-credentials/",
                                   imdsToken).trim();
        if (roleName.isEmpty()) {
            throw new RuntimeException("No IAM role attached to this EC2 instance.");
        }
        int newline = roleName.indexOf('\n');
        if (newline > 0) roleName = roleName.substring(0, newline).trim();
        String credsJson = httpGet(
                IMDS_BASE + "/latest/meta-data/iam/security-credentials/" + roleName, imdsToken);
        return parseCredentialsJson(credsJson);
    }

    private String getImdsSessionToken() throws Exception {
        URL url = new URL(IMDS_BASE + "/latest/api/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(IMDS_TIMEOUT_MS);
            conn.setReadTimeout(IMDS_TIMEOUT_MS);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", "21600");
            conn.setDoOutput(true);
            conn.getOutputStream().close();
            requireOk(conn, "IMDSv2 token");
            return readFully(conn);
        } finally {
            conn.disconnect();
        }
    }

    private String httpGet(String urlStr, String imdsToken) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(IMDS_TIMEOUT_MS);
            conn.setReadTimeout(IMDS_TIMEOUT_MS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-aws-ec2-metadata-token", imdsToken);
            requireOk(conn, urlStr);
            return readFully(conn);
        } finally {
            conn.disconnect();
        }
    }

    private void requireOk(HttpURLConnection conn, String label) throws Exception {
        int code = conn.getResponseCode();
        if (code != 200) throw new RuntimeException("HTTP " + code + " from: " + label);
    }

    private String readFully(HttpURLConnection conn) throws Exception {
        BufferedReader r = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
            return sb.toString().trim();
        } finally {
            r.close();
        }
    }

    private ImdsCredentials parseCredentialsJson(String json) {
        ImdsCredentials c = new ImdsCredentials();
        c.accessKeyId     = jsonField(json, "AccessKeyId");
        c.secretAccessKey = jsonField(json, "SecretAccessKey");
        c.sessionToken    = jsonField(json, "Token");
        if (c.accessKeyId == null || c.secretAccessKey == null) {
            throw new RuntimeException("Failed to parse IAM credentials from IMDS.");
        }
        return c;
    }

    private String jsonField(String json, String fieldName) {
        String needle = "\"" + fieldName + "\"";
        int ki = json.indexOf(needle);
        if (ki < 0) return null;
        int ci = json.indexOf(':', ki + needle.length());
        if (ci < 0) return null;
        int oq = json.indexOf('"', ci + 1);
        if (oq < 0) return null;
        int cq = oq + 1;
        while (cq < json.length()) {
            char ch = json.charAt(cq);
            if (ch == '"' && json.charAt(cq - 1) != '\\') break;
            cq++;
        }
        return (cq < json.length()) ? json.substring(oq + 1, cq) : null;
    }

    private String sha256Hex(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return toHex(md.digest(data.getBytes(StandardCharsets.UTF_8)));
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String sigV4Encode(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
            int i = b & 0xFF;
            if ((i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z') || (i >= '0' && i <= '9')
                    || i == '-' || i == '_' || i == '.' || i == '~') {
                sb.append((char) i);
            } else {
                sb.append(String.format("%%%02X", i));
            }
        }
        return sb.toString();
    }

    private String utcDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private static class ImdsCredentials {
        String accessKeyId;
        String secretAccessKey;
        String sessionToken;
    }
}
