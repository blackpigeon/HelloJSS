# Run and Stop Commands

เอกสารนี้สรุปคำสั่งที่ใช้ Run และ Stop สำหรับโปรเจกต์ HelloJSS

## Run (Start Web Server)

รันจากโฟลเดอร์โปรเจกต์:

```powershell
Set-Location runtime/apache-tomcat-8.5.100/bin
.\catalina.bat run
```

เมื่อรันสำเร็จ เข้าใช้งานได้ที่:

- http://localhost:8080/HelloJSS/

## Stop (Stop Web Server)

เปิด PowerShell อีกหน้าต่าง แล้วรัน:

```powershell
Set-Location runtime/apache-tomcat-8.5.100/bin
.\catalina.bat stop
```

ถ้าเปิดแบบ foreground ในหน้าต่างเดียวกัน สามารถกด `Ctrl + C` เพื่อหยุดได้เช่นกัน
