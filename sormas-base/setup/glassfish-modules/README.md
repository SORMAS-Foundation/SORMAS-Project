# Patched glassfish-modules

- Purpose: To avoid certain problems, we are patching specific jars of Payara (`payara/glassfish/modules`).
- Current version: `payara-5.2021.10`

## Patches

### weld-integration.jar/org/glassfish/weld/services/InjectionServicesImpl.java, line 143

```java
    // XXX Avoid err message on invocation of AuditLog (Auditor, TransactionId)
    // System.err.println("No valid EE environment for injection of " + targetClass);
```
