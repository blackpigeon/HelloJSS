# Local Run Checklist

## 1) Verify PostgreSQL connectivity

PowerShell:

```powershell
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
$env:PGPASSWORD = "P@ssw0rd"
& $psql -h localhost -p 5432 -U postgres -d postgres -c "SELECT version();"
& $psql -h localhost -p 5432 -U postgres -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='hello_jss';"
$env:PGPASSWORD = $null
```

Expected result:

- Database service responds.
- Last command returns `1`.

## 2) Build WAR with Ant wrapper

From workspace root:

```powershell
deploy\antw.bat war
```

Expected result:

- `BUILD SUCCESSFUL`
- WAR generated at `dist/HelloJSS.war`
- JDBC driver present at `lib/postgresql-42.7.5.jar`

## 3) Deploy to Tomcat

Copy `dist/HelloJSS.war` to Tomcat `webapps` directory and start/restart Tomcat.

When opening `/HelloJSS/index.jsp`, the app will:

1. Check if schema is already migrated.
2. Auto-migrate if needed.
3. Load the guestbook page and data.