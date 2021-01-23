## Providers

The `Providers` are a kind of agents for communication via `email` or `SMS`. There are two implemented providers:

* `email` through [JavaMail API](https://javaee.github.io/javamail/ "JavaMail API") with the `cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider` implementation class
* `SMS` through [Click & Send SMS service provider](https://clicksend.com/ "Click & Send SMS service provider") with `cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider` implementation class

You can use these or any other that you want.

## Implementing providers

Any provider implementation class should implement any of the two following interfaces:

* `cat.albirar.communications.providers.email.IEmailProvider` for email provider
* `cat.albirar.communications.providers.sms.ISmsSenderProvider` for SMS provider

The communications should to be made asynchronously. Any problem should to be notified throwing a `cat.albirar.communications.providers.ProviderException`.
