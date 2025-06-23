# kitchensink Quickstart (JBoss EAP 8)

Welcome to **kitchensink**, the flagship quickstart in the JBoss EAP Quickstarts repository.  
This guide distills the essentials you need to explore the application, build it locally, and understand where the project is headed next.  
For step-by-step, production-grade instructions see the original [`README.adoc`](./README.adoc) or its rendered [`README.html`](./README.html).

---

## What is kitchensink?

kitchensink is a minimal yet fully-functioning Jakarta EE 10 web application that shows how the standard Java EE stack fits together on **JBoss Enterprise Application Platform 8**:

* JSF front-end (`index.xhtml`)
* CDI-managed backing beans
* Stateless EJBs & JPA for business / persistence logic
* JAX-RS REST endpoints
* Bean Validation
* Arquillian integration tests

The result is a CRUD app for member registration that you can deploy in minutes.

---

## Quick Start

|                | Command |
|----------------|---------|
| **Build**      | `mvn clean package` |
| **Deploy** (server running) | `mvn wildfly:deploy` |
| **Run server** | `${EAP_HOME}/bin/standalone.sh` |
| **App URL**    | <http://localhost:8080/kitchensink/> |

> **Note**  
> The quickstart uses an in-memory **H2** datasource defined via `*-ds.xml`; great for demos, *not* for production.

---

## Requirements

* JDK 11+ (tested with Java 17)
* Maven 3.6+
* JBoss EAP 8 (or WildFly 28 for community users)

---

## Project Layout (high-level)

```
kitchensink
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org/jboss/as/quickstarts/kitchensink
│   │   │       ├── controller   (JSF backing bean)
│   │   │       ├── data         (repository + producer)
│   │   │       ├── model        (JPA entity)
│   │   │       ├── rest         (JAX-RS resources)
│   │   │       └── service      (business/EJB layer)
│   │   ├── resources/META-INF/persistence.xml
│   │   └── webapp              (JSF pages, templates, WEB-INF)
└── pom.xml
```

---

## Key Technologies Demonstrated

* **Jakarta EE 10** APIs (CDI, EJB, JPA, JSF, JAX-RS, Bean Validation)
* **Arquillian** for in-container testing
* **Helm chart** for OpenShift S2I deployment (`charts/helm.yaml`)
* **Maven** build with WildFly / EAP plugins

---

## Upcoming: Spring Boot Migration (PRO-337)

The Factory engineering team is actively migrating kitchensink to **Spring Boot 3 / Java 21** under ticket **PRO-337**.  
Goals include:

1. One-for-one feature parity using Spring MVC, Spring Data JPA, and Validation.
2. Preservation of the current package structure to ease comparison.
3. Container-ready build (Dockerfile + GitHub Actions CI).
4. Optional phase to move persistence from H2 → MongoDB.

Follow progress in `droid_migration/` for the full migration plan and setup guide.

---

## Learn More

* Full documentation: [`README.adoc`](./README.adoc)
* Migration plan: [`droid_migration/PRO-337_migration_plan.md`](./droid_migration/PRO-337_migration_plan.md)
* JBoss EAP docs: <https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/>
* WildFly community docs: <https://docs.wildfly.org/>

Enjoy experimenting with kitchensink! Feedback and contributions are welcome via pull requests.
