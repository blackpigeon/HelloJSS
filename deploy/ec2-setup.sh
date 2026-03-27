#!/bin/bash
# =============================================================
# EC2 Setup Script: Install Java 8 + Tomcat 8.5
# Run once on a fresh EC2 instance (Amazon Linux 2 / Ubuntu)
# =============================================================
set -e

TOMCAT_VERSION="8.5.100"
TOMCAT_DIR="/opt/tomcat"
TOMCAT_USER="tomcat"

# --- Detect OS ---
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
fi

echo ">>> Detected OS: $OS"

# --- Install Java 8 ---
if [[ "$OS" == "amzn" || "$OS" == "rhel" || "$OS" == "centos" ]]; then
    sudo yum update -y
    sudo yum install -y java-1.8.0-amazon-corretto wget
elif [[ "$OS" == "ubuntu" || "$OS" == "debian" ]]; then
    sudo apt-get update -y
    sudo apt-get install -y openjdk-8-jdk wget
fi

java -version

# --- Create Tomcat user ---
sudo useradd -r -m -U -d "$TOMCAT_DIR" -s /bin/false "$TOMCAT_USER" || true

# --- Download Tomcat ---
echo ">>> Downloading Apache Tomcat $TOMCAT_VERSION..."
cd /tmp
wget -q "https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz"
sudo tar xzf "apache-tomcat-${TOMCAT_VERSION}.tar.gz" -C /opt
sudo ln -sfn "/opt/apache-tomcat-${TOMCAT_VERSION}" "$TOMCAT_DIR"

# --- Set permissions ---
sudo chown -R "$TOMCAT_USER":"$TOMCAT_USER" "$TOMCAT_DIR"
sudo chmod +x "$TOMCAT_DIR/bin/"*.sh

# --- Create systemd service ---
sudo tee /etc/systemd/system/tomcat.service > /dev/null <<EOF
[Unit]
Description=Apache Tomcat 8
After=network.target

[Service]
Type=forking
User=$TOMCAT_USER
Group=$TOMCAT_USER
Environment="JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))"
Environment="CATALINA_HOME=$TOMCAT_DIR"
ExecStart=$TOMCAT_DIR/bin/startup.sh
ExecStop=$TOMCAT_DIR/bin/shutdown.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable tomcat
sudo systemctl start tomcat

echo ">>> Tomcat installed and running at http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8080"
echo ">>> TOMCAT_HOME = $TOMCAT_DIR"
echo ">>> Add this as GitHub Secret: TOMCAT_HOME=$TOMCAT_DIR"
