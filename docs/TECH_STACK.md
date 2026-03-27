# Tech Stack Reference — AEON Financing System (MM AFIS Web App)

> เอกสารนี้สรุป tech stack ทั้งหมดของ project เพื่อใช้อ้างอิงในการสร้าง project ใหม่

---

## 1. Overview

| Category | Technology |
|---|---|
| Language | Java 1.7 (source/target), JDK 1.8 runtime |
| Web Framework | Spring MVC 3.1.0.RELEASE |
| Persistence | MyBatis 3.0.5 |
| Database | PostgreSQL 42.2.5 (primary), MS SQL Server (secondary) |
| View Layer | JSP + JSPF Fragments |
| Build Tool | Apache Ant |
| App Server | Apache Tomcat 8.5 |
| IDE | Eclipse (Dynamic Web Project) |

---

## 2. Backend

### 2.1 Core Framework — Spring MVC

| Library | Version |
|---|---|
| spring-core | 3.1.0.RELEASE |
| spring-beans | 3.1.0.RELEASE |
| spring-context | 3.1.0.RELEASE |
| spring-web | 3.1.0.RELEASE |
| spring-webmvc | 3.1.0.RELEASE |
| spring-jdbc | 3.1.0.RELEASE |
| spring-tx | 3.1.0.RELEASE |
| spring-aop | 3.1.0.RELEASE |
| spring-expression | 3.1.0.RELEASE |
| spring-asm | 3.1.0.RELEASE |
| spring-test | 3.1.2.RELEASE |
| aopalliance | 1.0 |
| cglib | 2.2 |
| cglib-nodep | 2.2.2 |
| asm | 3.2 |

**Configuration style**: XML-based (`dispatcher-servlet.xml`, `database-config.xml`, `mapper-config.xml`)
**No Spring Boot** — ใช้ traditional Spring MVC

### 2.2 Persistence — MyBatis

| Library | Version |
|---|---|
| mybatis | 3.0.5 |
| mybatis-spring | 1.0.1 |

**Pattern**: Mapper Interface + XML Mapper (`*.xml`)
**Result type**: ส่วนใหญ่ใช้ `Map<String, Object>` (loose typing)
**Dynamic SQL**: `<if>`, `<where>`, `<choose>` tags

### 2.3 Database & Connection Pool

| Library | Version |
|---|---|
| postgresql | 42.2.5 |
| sqljdbc4 | (MS SQL Server) |
| commons-dbcp | 1.3 |
| commons-pool | 1.5.6 |

**Connection Pool Settings**: maxActive = 50

### 2.4 Transaction Management

`org.springframework.jdbc.datasource.DataSourceTransactionManager`

Annotation used on service layer:
```java
@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED)
```

### 2.5 Logging

| Library | Version |
|---|---|
| slf4j-api | 1.6.4 |
| logback-classic | 1.0.3 |
| logback-core | 1.0.3 |
| commons-logging | 1.1.1 |

### 2.6 Utility Libraries (Apache Commons)

| Library | Version |
|---|---|
| commons-beanutils | 1.8.3 |
| commons-codec | 1.3 |
| commons-collections | 3.2.1 |
| commons-digester | 2.1 |
| commons-fileupload | 1.2.1 |
| commons-io | 1.4 |

### 2.7 XML Processing

| Library | Version |
|---|---|
| dom4j | 1.6.1 |
| jdom | (bundled) |
| xml-apis | 1.3.04 |
| jakarta-oro | 2.0.7 |

### 2.8 JSON

| Library | Version |
|---|---|
| gson | 2.3 |

### 2.9 Email

| Library | Version |
|---|---|
| javax-mail | (bundled) |
| jta | 1.1 |

### 2.10 Image Processing

| Library | Version |
|---|---|
| im4java | 1.4.0 |

---

## 3. Reporting & Document Generation

| Library | Version | Purpose |
|---|---|---|
| jasperreports | 5.0.0 | Report engine (.jrxml → .jasper) |
| jasperreports-fonts | 5.0.0 | Font support |
| poi | 3.9-20121203 | Excel (.xls) |
| poi-ooxml | 3.9-20121203 | Excel (.xlsx) |
| poi-ooxml-schemas | 3.9-20121203 | OOXML schemas |
| itext | 2.1.7 | PDF generation |
| jxl | 2.6 | Legacy Excel |

---

## 4. Frontend / View Layer

### 4.1 View Technology

- **JSP** (JavaServer Pages) — server-side rendering
- **JSPF** (JSP Fragments) — reusable header/footer/shared components
- **HTML 4.01 Transitional** Doctype
- **IE 8 Emulation** mode (`X-UA-Compatible: IE=EmulateIE8`)
- **Encoding**: UTF-8

### 4.2 JSTL Tag Libraries

| Tag Library | URI | Prefix |
|---|---|---|
| JSTL Core | `http://java.sun.com/jsp/jstl/core` | `c` |
| JSTL Format | `http://java.sun.com/jstl/fmt` | `fmt` |
| JSTL Functions | `http://java.sun.com/jsp/jstl/functions` | `fn` |
| Spring Tags | `http://www.springframework.org/tags` | `spring` |

### 4.3 JavaScript Libraries

| Library | Version | Purpose |
|---|---|---|
| jQuery | 1.8.2 | Core JS framework |
| jQuery UI | 1.9.0.custom | UI widgets & interactions |
| jquery.maskedinput | 1.2.2 | Input masking (dates, numbers) |
| jquery.treeview | (bundled) | Hierarchical side menu |
| jquery.cookie | (bundled) | Cookie management |
| jquery.placeholder | (bundled) | Placeholder polyfill |
| dhtmlgoodies_calendar | (bundled) | Date picker widget |

**Custom Scripts:**
- `common.js` — Global utilities, session management, breadcrumbs
- `validator.js` — Client-side form validation
- `date.js` — Date manipulation utilities

### 4.4 CSS

**No external CSS framework** (Bootstrap, Tailwind ไม่ได้ใช้)
ใช้ custom CSS ทั้งหมด:

| File | Purpose |
|---|---|
| `common.css` | Base styles, tables, typography |
| `default.css` | Inputs, buttons, utilities |
| `header.css` | Fixed top navigation bar |
| `sidemenu.css` | Collapsible side menu |
| `login.css` | Login page styles |
| `home.css` | Dashboard styles |
| `sub.css` | Subpage styles |

**Theme System**: afsTheme, laoTheme (country-specific themes)
**Color scheme**: Blue (#4775D1 headers), White/Gray backgrounds, Orange (#FF6600 accents)
**Font**: Tahoma, Arial, sans-serif

### 4.5 Layout Pattern (JSP Fragment System)

```
[_headerNew.jspf]   ← DOCTYPE, CSS, JS includes, top nav, side menu
[page-specific JSP] ← Main content area
[_footerNew.jspf]   ← Footer, version info, language switcher
```

---

## 5. Architecture

### 5.1 Application Architecture

```
HTTP Request
    ↓
web.xml (DispatcherServlet — Spring MVC entry point)
    ↓
SignonInterceptor (Session authentication check)
    ↓
Controller Layer (131 controllers)
    ↓
Service Layer (206 services: interface + impl)
    ↓
MyBatis Mapper Layer (47 interfaces + 37 XML mappers)
    ↓
Database (PostgreSQL / MS SQL Server)
    ↓
ModelAndView → JSP View → HTTP Response
```

### 5.2 Package Structure

```
src/
└── com/acss/
    ├── common/              # Shared utilities, exceptions, taglib
    │   ├── exception/
    │   ├── taglib/          # Custom JSP tag libraries
    │   ├── util/
    │   └── web/             # Pagination utilities
    └── hps/                 # Main application domain
        ├── domain/          # Beans / Entities / DTOs
        ├── persistence/     # MyBatis mapper interfaces
        │   └── xmlmaps/     # MyBatis XML mapper files
        ├── services/        # Business logic
        │   └── impl/        # Service implementations
        ├── util/            # App-specific utilities
        └── web/
            └── controller/  # Spring MVC controllers
```

### 5.3 Key Design Patterns

| Pattern | Implementation |
|---|---|
| MVC | Spring MVC |
| Layered Architecture | Controller → Service → Mapper |
| Template Method | `AbstractACSController`, `AbstractACSService` |
| Dependency Injection | `@Autowired` (field injection) |
| Factory | `MapperFactoryBean` (MyBatis) |
| Interceptor | `SignonInterceptor` (authentication) |
| DTO | `ACSSession`, `ACSAccount`, domain beans |

### 5.4 Security / Authentication

- **No Spring Security** — Custom implementation
- `SignonInterceptor` extends `HandlerInterceptorAdapter`
- Session stored in `HttpSession` (key: `"hpSession"`)
- Session timeout: 30 minutes
- `ACSSessionManager` — Static helper for get/set/remove session
- `ACSSession` object contains: user account, country code, menu permissions

---

## 6. Configuration Files Structure

```
WebContent/
└── WEB-INF/
    ├── web.xml                        # Servlet config, filter, session timeout
    ├── mybatis-config.xml             # MyBatis global settings
    ├── classes/
    │   └── {country}/                 # e.g., mm/
    │       ├── dispatcher-servlet.xml # Spring MVC beans (controllers, services, interceptors)
    │       ├── database-config.xml    # DataSource, SqlSessionFactory, TxManager
    │       ├── mapper-config.xml      # MyBatis mapper bean definitions
    │       ├── logback.xml            # Logging config
    │       └── resources_{env}.properties  # JDBC URL, credentials (dev/test/demo/prod)
    ├── config/
    │   └── {country}/{lang}/          # Business XML configs
    │       ├── AFS.xml
    │       ├── code.xml
    │       ├── validation.xml
    │       ├── emailConfig.xml
    │       ├── smsConfig.xml
    │       └── reportConfiguration.xml
    ├── jsp/
    │   └── {country}/                 # JSP views
    ├── lib/                           # All JAR dependencies
    └── tld/                           # Custom tag library descriptors
```

---

## 7. Multi-Tenancy & Environments

### Countries Supported
| Code | Country |
|---|---|
| `mm` | Myanmar |
| `la` | Laos |
| `kh` | Cambodia |

### Environments
| Env | Properties File |
|---|---|
| Development | `resources_dev.properties` |
| Testing | `resources_test.properties` |
| Demo | `resources_demo.properties` |
| Production | `resources_prod.properties` |

### Internationalization (i18n)
- Messages stored in **database table** `M_MESSAGES` (not property files)
- Custom `MessageSource` bean loads messages from DB
- `AFSSessionLocaleResolver` — Custom locale resolver
- `SessionThemeResolver` — Theme switching per session
- Language links in footer to switch locale at runtime

---

## 8. Servlet Configuration (web.xml)

| Servlet | URL Pattern | Purpose |
|---|---|---|
| AFSPlatform (DispatcherServlet) | `*.htm` | Main Spring MVC dispatcher |
| ImageShowServlet | `/ImageShowServlet/*` | Serve stored images |
| ImageSaveServlet | (internal) | Save uploaded images |
| JasperReportServlet | `/JasperReportServlet/*`, `/reportJasper/*` | Generate reports |

**Filters:**
- `trimFilter` — Strips whitespace from HTML responses

**Listeners:**
- `com.acss.hps.web.SessionListener` — Tracks active sessions

**Multipart Upload:**
- Max file size: 500 MB
- Resolver: `CommonsMultipartResolver`

---

## 9. Build System

**Tool**: Apache Ant (`deploy/build.xml`)

**Output WAR files (per country):**
- `AFSMyanmarProjectProdFix.war` — Myanmar
- `AFSLaos.war` — Laos
- `AFSCambodia.war` — Cambodia

**Java compile settings:**
- Source: 1.7
- Target: 1.7
- Runtime: JDK 1.8

**JasperReports**: Compiled during build using `JRAntCompileTask` (`.jrxml` → `.jasper`)

---

## 10. Testing

| Library | Version |
|---|---|
| junit | 4.9 |
| spring-test | 3.1.2.RELEASE |

---

## 11. Scale of Codebase

| Component | Count |
|---|---|
| Total Java files | ~484 |
| Controllers | 131 |
| Services (interface + impl) | 206 |
| MyBatis Mapper interfaces | 47 |
| MyBatis XML mapper files | 37 |
| Domain/Bean classes | 20 |
| Common utility files | 52 |
| JSP/JSPF view files | 300+ |
| Report templates (.jrxml) | 50+ |

---

## 12. Recommended Project Structure for New Project

หากจะสร้าง project ใหม่ด้วย stack เดียวกัน แนะนำโครงสร้างดังนี้:

```
my-project/
├── src/
│   └── com/company/
│       ├── common/
│       │   ├── exception/
│       │   ├── taglib/
│       │   └── util/
│       └── app/
│           ├── domain/          # Beans, DTOs
│           ├── persistence/     # MyBatis mapper interfaces
│           │   └── xmlmaps/     # MyBatis XML files
│           ├── services/
│           │   └── impl/
│           └── web/
│               └── controller/
├── WebContent/
│   ├── js/                      # jQuery, custom scripts
│   ├── themes/                  # CSS files, images
│   └── WEB-INF/
│       ├── web.xml
│       ├── mybatis-config.xml
│       ├── classes/
│       │   ├── dispatcher-servlet.xml
│       │   ├── database-config.xml
│       │   ├── mapper-config.xml
│       │   ├── logback.xml
│       │   └── resources_dev.properties
│       ├── jsp/
│       │   ├── _headerNew.jspf
│       │   ├── _footerNew.jspf
│       │   └── [feature pages]
│       ├── lib/                 # All JAR files
│       └── tld/
├── deploy/
│   └── build.xml                # Ant build script
└── .classpath / .project        # Eclipse project files
```

### Minimum Required JARs for New Project

**Spring MVC:**
- `org.springframework.core-3.1.0.RELEASE.jar`
- `org.springframework.beans-3.1.0.RELEASE.jar`
- `org.springframework.context-3.1.0.RELEASE.jar`
- `org.springframework.web-3.1.0.RELEASE.jar`
- `org.springframework.web.servlet-3.1.0.RELEASE.jar`
- `org.springframework.jdbc-3.1.0.RELEASE.jar`
- `org.springframework.transaction-3.1.0.RELEASE.jar`
- `org.springframework.aop-3.1.0.RELEASE.jar`

**MyBatis:**
- `mybatis-3.0.5.jar`
- `mybatis-spring-1.0.1.jar`

**Database:**
- `postgresql-42.2.5.jar`
- `commons-dbcp-1.3.jar`
- `commons-pool-1.5.6.jar`

**Logging:**
- `slf4j-api-1.6.4.jar`
- `logback-classic-1.0.3.jar`
- `logback-core-1.0.3.jar`

**Utilities:**
- `commons-fileupload-1.2.1.jar`
- `commons-io-1.4.jar`
- `commons-collections-3.2.1.jar`
- `gson-2.3.jar`
- `cglib-2.2.jar`
- `aopalliance-1.0.jar`

**JSTL:**
- `jstl-1.2.jar`
- `standard-1.1.2.jar`

---

*Generated from codebase analysis of mm-afis-web-app — March 2026*
