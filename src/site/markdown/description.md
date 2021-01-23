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

The `Core` part include the main service: `cat.albirar.communications.services.ICommunicationService` that is the interface to all the system.

Also contains the classes to use a 

## Providers

The `Providers` are a kind of agents for communication via `email` or `SMS`. 

See [Providers](providers.html "Providers")
