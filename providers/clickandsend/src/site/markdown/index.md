# Click And Send Usage

## Dependency

First of all should to add the dependency to this provider:

```xml
<dependency>
   <groupId>cat.albirar.lib</groupId>
   <artifactId>albirar-communications-provider-clickandsend</artifactId>
   <version>${project.version}</version>
</dependency>
```
## Configuration

In order to use, you need to indicate the credentials to access to Rest API of Click&Send service (see [ClickAndSend API Authentication](https://github.com/ClickSend/clicksend-java#getting-started)).
Also other properties

The properties are:

| Property name    | Description                    | Default Value                                                   |
|--------------|--------------------------------|-----------------------------------------------------------------|
| `albirar.communications.sms.cs.api.username`     | Credentials user name | "`TEST_USERNAME`", see [![Subaccounts](images/credentials.png)](https://dashboard.clicksend.com/account/subaccounts) |
| `albirar.communications.sms.cs.api.key`     | Credentials API Key | "`TEST_KEY`", see [![Subaccounts](images/credentials.png)](https://dashboard.clicksend.com/account/subaccounts) |

## Usage

This provider are "discovered" by default if SpringBoot *autoconfiguration* is enabled.

Also, if *autoconfiguration* is dissabled, you should to use the annotation `cat.albirar.communications.core.core.providers.sms.clickandsend.configuration.AutoconfigureClickAndSendSmsProvider` in your configuration class:

```java
@Configuration
@AutoconfigureClickAndSendSmsProvider
public AppConfig {
// ...
```

This annotation configure the provider for use.

**!!Remember to configure Click&Send service properties**


This provider, like any other, is not used directly but used by [core](/core/index.html) service that select it by sender and recipient channel type criteria.

The provider ClickAndSend is selected when [CommunicationChannelBean](/apidocs/cat/albirar/communications/channels/models/CommunicationChannelBean.html] _of both_, sender and recipient, are [SMS](/apidocs/cat/albirar/communications/channels/models/ECommunicationChannelType.html#SMS)

