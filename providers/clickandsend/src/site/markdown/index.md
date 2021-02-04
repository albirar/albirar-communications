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

This provider, like any other, is not used directly but used by [core](core/index.html) service.

The provider C&S is selected when CommunicationChannelBean of both, sender and recipient, are SMS.


