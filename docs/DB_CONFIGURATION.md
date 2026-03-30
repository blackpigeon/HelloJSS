# DB Configuration

The application follows a JNDI-first design and falls back to direct JDBC settings when no container-managed datasource is available.

## Lookup order

1. JNDI datasource: `java:comp/env/jdbc/HelloJSS`
2. Java system properties
3. Environment variables
4. Local development defaults

## Development defaults

- URL: `jdbc:postgresql://localhost:5432/hello_jss`
- User: `postgres`
- Password: `P@ssw0rd`

## Supported overrides

- System properties
  - `hellojss.db.jndiName`
  - `hellojss.db.url`
  - `hellojss.db.user`
  - `hellojss.db.password`
- Environment variables
  - `HELLOJSS_DB_JNDI_NAME`
  - `HELLOJSS_DB_URL`
  - `HELLOJSS_DB_USER`
  - `HELLOJSS_DB_PASSWORD`

## AWS recommendation

For AWS deployment, define a Tomcat JNDI datasource that points to Aurora PostgreSQL-compatible or RDS Proxy and keep credentials outside the WAR.

Example resource name:

- `jdbc/HelloJSS`

The application checks schema readiness on `index.jsp`. If the baseline schema is missing, it creates the required tables before rendering the main screen.