## Providers

The `Providers` are a kind of agents for communication via `email` or `SMS`. There are two implemented providers:

* `email` through [JavaMail API](https://javaee.github.io/javamail/ "JavaMail API") with the `cat.albirar.communications.providers.email.javamail.impl.JavaMailSenderEmailProvider` implementation class.  (see [javamail](albirar-communications-providers/albirar-communications-provider-javamail/index.html))
* `SMS` through [Click & Send SMS service provider](https://clicksend.com/ "Click & Send SMS service provider") with `cat.albirar.communications.providers.sms.clickandsend.ClickAndSendSmsSenderProvider` implementation class. (see [ClickAndSend](albirar-communications-providers/albirar-communications-provider-clickandsend/index.html))

You can use these or any other that you want.

## Implementing providers

Any provider implementation class should implement any of the two following interfaces:

* `cat.albirar.communications.providers.email.IEmailProvider` for email provider
* `cat.albirar.communications.providers.sms.ISmsSenderProvider` for SMS provider

The communications should to be made asynchronously. Any problem should to be notified throwing a `cat.albirar.communications.providers.ProviderException`.

The "registration" of new providers is made by *spring scanning components* and will be autowired to the list of the service providers.
