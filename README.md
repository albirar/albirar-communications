# albirar-communications

A library to enable email and SMS communications with third party, like users.

The messages are send in an asynchronous way, decoupling application from network latency.


## Library structure

This library is divided in two parts:

1. Core
2. Providers

### Core

The `Core` part include the main service: `cat.albirar.communications.services.ICommunicationService` that is the interface to all the system.

### Providers

The `Providers` are providers for communication via `email` or `SMS`. There is two implemented providers:

* `email` through `javamail` with the `cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider` implementation class
* `SMS` through [Click & Send SMS service provider](https://clicksend.com/ "Click & Send SMS service provider") with `cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider` implementation class

You can use these or any other that you want.

#### Implementing providers

Any provider implementation class should to implement any of the two following interfaces:

* `cat.albirar.communications.providers.email.IEmailProvider` for email provider
* `cat.albirar.communications.providers.sms.ISmsSenderProvider` for SMS provider

The communications should to be made asynchronously. Any problem should to be notified throwing a `cat.albirar.communications.providers.ProviderException`.

## Usage

You should to put 