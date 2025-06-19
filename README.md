# JBoss EAP Quickstarts

> A collection of small, focused example applications that showcase the features and best-practices of **Red Hat JBoss Enterprise Application Platform (EAP)** and the upstream **WildFly** project.

These quickstarts serve as _living documentation_: they demonstrate how to use Jakarta EE / MicroProfile technologies on JBoss EAP and provide a convenient starting point for your own applications.

---

## Why Quickstarts?

* **Learn by example** – see complete, working code instead of snippets.  
* **Explore specific features** – each project targets one or two technologies (CDI, JPA, JMS, WebSockets, MicroProfile, etc.).  
* **Kick-start new projects** – copy a quickstart skeleton, then extend it.  
* **Verify your environment** – confirm that JBoss EAP and your tool-chain are installed correctly.

---

## Repository Structure

```
jboss-eap-quickstarts/
├── helloworld/       # simple servlet
├── ejb-remote/       # remote EJB invocation
├── jpa/              # database access with JPA
├── websocket/        # real-time messaging
└── …                 # many more
```

Each directory is a **stand-alone Maven project** containing:

* `src/` – Java source, web resources, config (`persistence.xml`, `beans.xml`, …)  
* `pom.xml` – build & dependency management  
* `README.adoc` – in-depth guide for that quickstart

---

## System Requirements

| Component        | Minimum Version |
|------------------|-----------------|
| Java (JDK)       | 11 or newer |
| Maven            | 3.6 or newer |
| Application Server | Red Hat JBoss EAP 8.0 (or WildFly 28) |

Optional:

* Docker / OpenShift + Helm (for cloud deployments)

---

## Getting Started

1. **Clone the repo**

   ```bash
   git clone https://github.com/Factory-Crucible/jboss-eap-quickstarts.git
   cd jboss-eap-quickstarts
   ```

2. **Choose a quickstart**

   ```bash
   cd helloworld
   ```

3. **Start JBoss EAP**

   ```bash
   $EAP_HOME/bin/standalone.sh           # *.bat on Windows
   ```

4. **Build & deploy**

   ```bash
   mvn clean package
   mvn wildfly:deploy
   ```

5. **Access the app**

   ```
   http://localhost:8080/helloworld/
   ```

6. **Run integration tests (optional)**

   ```bash
   mvn verify -Parq-remote
   ```

---

## Running on OpenShift with Helm (Optional)

```bash
helm repo add jboss-eap https://jbossas.github.io/eap-charts/
helm install helloworld -f charts/helm.yaml jboss-eap/eap8
```

The chart builds the image via S2I and deploys it with one command.

---

## Contributing

Issues and pull requests are welcome! If you add a new quickstart, follow the existing directory layout and include a brief `README.adoc` explaining:

* Technologies demonstrated  
* Prerequisites  
* Build & run steps  
* Expected output

---

## More Information

* Full documentation lives in the per-quickstart `README.adoc` files.  
* Official product docs: <https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/>  

Happy hacking!
