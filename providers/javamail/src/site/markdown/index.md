# Javamail Usage

This provider enable the channel email backed by [SpringFramework Javamail](https://docs.spring.io/spring-framework/docs/5.2.12.RELEASE/spring-framework-reference/integration.html#mail).

## Dependency

To use this provider should to add dependency:

```xml
<dependency>
   <groupId>cat.albirar.lib</groupId>
   <artifactId>albirar-communications-provider-clickandsend</artifactId>
   <version>${project.version}</version>
</dependency>
```

## Configuration

Some properties can be used to configure working of provider:

The properties are:

| Property name    | Description                    | Default Value                                                   |
|--------------|--------------------------------|-----------------------------------------------------------------|
| `albirar.communications.mail.javamail.host`     | Mail server host name | None |
| `albirar.communications.mail.javamail.port`     | Mail server host port | None |
| `albirar.communications.mail.javamail.username` | Credential's username to access to mail server | None |
| `albirar.communications.mail.javamail.password` | Credential's password to access to mail server | None |


## Usage

This provider are "discovered" by default if SpringBoot *autoconfiguration* is enabled.

Also, if *autoconfiguration* is dissabled, you should to use the annotation `cat.albirar.communications.core.core.providers.email.javamail.configuration.AutoconfigureJavamailEmailProvider` in your configuration class:

```java
@Configuration
@AutoconfigureJavamailEmailProvider
public AppConfig {
// ...
```

This annotation configure the provider for use.

**!!Remember to configure email server properties**


This provider, like any other, is not used directly but used by [core](/core/index.html) service.

The provider JavaMail is selected when [CommunicationChannelBean](/apidocs/cat/albirar/communications/channels/models/CommunicationChannelBean.html) **of both**, sender and recipient, is [EMAIL](/apidocs/cat/albirar/communications/channels/models/ECommunicationChannelType.html#EMAIL)


