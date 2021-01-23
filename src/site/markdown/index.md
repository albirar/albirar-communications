# Albirar Communications

*Albirar communications* is a library to get some mid-level functionalities around *eMail* and *SMS* communications channels.

Enable email and SMS communications with third party, like users. The library use external services to communicate to, like gmail or Click&Send.

Is implemented with [SpringFramework library](https://spring.io/projects/spring-framework "SpringFramework library") and [Spring Boot library](https://spring.io/projects/spring-boot "Spring Boot library") and usable only with this environment.

The messages are send in an asynchronous way, decoupling application from network latency and involved services.

Messages to only one receipt is implemented today. Batch messages processing and sending are not implemented yet.

The access to external service for email or SMS is implemented through a kind of *agents* named **providers**. Providers are responsible of connecting, authenticate (if needed), send the message to the service end-point and retrieve the resulting status code.

Two base providers are implemented now: one for email and another one for sms.

# Library structure

This library is divided in two parts:

1. Core
2. Providers

## Core

The `Core` part include some interface contract for operations and the main service: `cat.albirar.communications.services.ICommunicationService` that is the interface to all the system.

Also contains the classes to use a 

## Providers

The `Providers` are a kind of agents for communication via `email` or `SMS`. There are two implemented providers:

* `email` through [JavaMail API](https://javaee.github.io/javamail/ "JavaMail API") with the `cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider` implementation class
* `SMS` through [Click & Send SMS service provider](https://clicksend.com/ "Click & Send SMS service provider") with `cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider` implementation class

You can use these or any other that you want.

# Implementing providers

Any provider implementation class should implement any of the two following interfaces:

* `cat.albirar.communications.providers.email.IEmailProvider` for email provider
* `cat.albirar.communications.providers.sms.ISmsSenderProvider` for SMS provider

The communications should to be made asynchronously. Any problem should to be notified throwing a `cat.albirar.communications.providers.ProviderException`.

# Requirements

* Java 1.8 or greater
* [SpringFramework *5.2.x*](https://docs.spring.io/spring-framework/docs/5.2.x/spring-framework-reference/ "SpringFramework *5.2.x*")
* [Spring Boot *2.3.x*](https://docs.spring.io/spring-boot/docs/2.3.x/reference/html/index.html "Spring Boot *2.3.x*")
* [Maven 3.6](https://maven.apache.org/ "Maven 3.6") or any other tool that uses maven repository and dependency resolution

# Usage

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

# Source code

The source code is available at github:

https://github.com/albirar/albirar-communications.git

The *master* branch holds the current production release.

The *develop* branch holds the last SNASHOT developing code.

# License

This library is distributed under the GNU GENERAL PUBLIC LICENSE Version 3 or later (GPLv3)

Full text of the license is available at: [GPLv3](https://www.gnu.org/licenses/gpl-3.0-standalone.html "GPLv3")

Short summary is available at: [TL;DR;Legal LGPL-3](https://tldrlegal.com/license/gnu-lesser-general-public-license-v3-(lgpl-3) "TL;DR;Legal LGPL-3")
