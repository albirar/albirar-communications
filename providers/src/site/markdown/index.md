# Providers

_Providers_ are a concept of this library to design some _agent_ that operate with any service that provide communication capabilities.

The concrete provider selected by service to manage a send operation is made by one parameter: the kind of channel for both, sender and recipient.

By default, two providers are implemented, one for email and another one for sms:

* `email` through [JavaMail API](https://javaee.github.io/javamail) with the `cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider` implementation class.  See [javamail](javamail/index.html)
* `SMS` through [Click & Send SMS service provider](https://clicksend.com) with `cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider` implementation class. See [ClickAndSend](clickandsend/index.html)

You can use these or any other that you want.

## Implementing providers

Any provider implementation class should implement any of the two following interfaces:

* `cat.albirar.communications.providers.email.IEmailProvider` for email provider
* `cat.albirar.communications.providers.sms.ISmsSenderProvider` for SMS provider

The communications should to be made asynchronously. Any problem should to be notified throwing a `cat.albirar.communications.providers.ProviderException`.

The "registration" of new providers is made by *spring scanning components* and will be autowired to the list of the service providers.
