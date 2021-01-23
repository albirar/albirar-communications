## Usage

Include in your maven project, the dependencies for providers, the core library is a transitive dependency.

If use the default providers:

```xml
<dependency>
   <groupId>cat.albirar.lib</groupId>
   <artifactId>albirar-communications-provider-javamail</artifactId>
   <version>2.0.0</version>
</dependency>
<dependency>
   <groupId>cat.albirar.lib</groupId>
   <artifactId>albirar-communications-provider-clickandsend</artifactId>
   <version>2.0.0</version>
</dependency>
```



Should to add the discovery of components for providers.
Second, configure some aspects of providers, like host and port, credentials, etc.

Add the component dependency for the core service: `cat.albirar.communications.services.ICommunicationService` on your classes.

Use the service:

```java

@Autowired
private ICommunicationService communicationService;

   // ...

   String idMsg = communicationService.pushMessage(message);
   // ...
   oMsg = communicationService.popStatusMessage(idMsg);
   if(oMsg.isPresent()) {
       if(oMsg.get().getStatus() == EStatusMessage.SEND) {
           // ...
       }
       if(oMsg.get().getStatus() == EStatusMessage.ERROR) {
           // ...
       }
   }
   // ...
```