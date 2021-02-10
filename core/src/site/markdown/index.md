# Core usage

## Configuration

Although default properties values are enough to run in most of environments, in some cases another values should to be indicated.

Here is a configuration table for `core` operations:

### RabbitMQ configuration

The connection of RabbitMQ client used by *albirar communications* is configurable by properties (see https://www.rabbitmq.com/api-guide.html#connecting)

| Property name    | Description                    | Default Value                                                   |
|--------------|--------------------------------|-----------------------------------------------------------------|
| `albirar.communications.connection.username`     | Credentials user name | "guest" |
| `albirar.communications.connection.password`     | Credentials passowrd | "guest" |
| `albirar.communications.connection.virtualhost` | The virtual host to connect to | "/" |
| `albirar.communications.connection.host`     | The server's host | "localhost" |
| `albirar.communications.connection.port`         | The port to connect to. 5672 for regular connections and 5671 for TLS connections | 5672 (regular connections) |

### Exchange and queues

The following are the exchange and queue names for *albirar communications* operations. This names are not configurable.

| Item | Name     | Description                    |
|------|----------|--------------------------------|
| Exchange | `albirar-communications` | The exchange name for queues |
| Send email queue | `emailSendQueue` | The queue for sending email |
| Send sms queue | `smsSendQueue` | The queue for sending SMS |
| Report email queue | `emailReportQueue` | The queue for reporting status and result of every email send |
| Report sms queue | `smsReportQueue` | The queue for reporting status and result of every sms send |

## Usage

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
