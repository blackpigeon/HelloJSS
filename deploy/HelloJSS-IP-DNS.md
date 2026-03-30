HelloJSS

Public IP4
54.251.137.161

Public DNS
ec2-54-251-137-161.ap-southeast-1.compute.amazonaws.com



postgress
user: postgres
password: postgres.123



database-1.cluster-chu0uc6ca4ie.ap-southeast-1.rds.amazonaws.com

psql -h database-1.cluster-chu0uc6ca4ie.ap-southeast-1.rds.amazonaws.com   
-U postgres   
-d postgres

CREATE USER jsshello\_user WITH LOGIN;

GRANT rds\_iam TO jsshello\_user;

