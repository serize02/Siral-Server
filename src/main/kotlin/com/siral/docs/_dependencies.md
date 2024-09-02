# Dependency Description

This document provides a description of the dependencies used in the project. Dependencies are organized by their general purpose and function in the project.

## Ktor Dependencies

Ktor is a web application framework for Kotlin. The following describes the dependencies related to Ktor:

- **`io.ktor:ktor-server-core-jvm`**: Provides the core components for building a Ktor web server.
- **`io.ktor:ktor-serialization-kotlinx-json-jvm`**: Support for JSON serialization and deserialization using Kotlinx.
- **`io.ktor:ktor-server-content-negotiation-jvm`**: Manages content negotiation on the server, allowing the server to determine the content type returned based on the client's request.
- **`io.ktor:ktor-server-call-logging-jvm`**: Enables logging of server calls, useful for debugging and monitoring.
- **`io.ktor:ktor-server-host-common-jvm`**: Provides common utilities for server configuration.
- **`io.ktor:ktor-server-status-pages-jvm`**: Allows customization of HTTP status pages for handling errors and responses.
- **`io.ktor:ktor-server-auth-jvm`**: Provides support for authentication in Ktor applications.
- **`io.ktor:ktor-server-auth-jwt-jvm`**: Implementation of authentication based on JWT (JSON Web Tokens).
- **`io.ktor:ktor-server-netty-jvm`**: Support for the Netty server engine, one of the recommended network servers for Ktor.
- **`io.ktor:ktor-server-tests-jvm`**: Provides tools for testing Ktor servers.
- **`io.ktor:ktor-server-test-host-jvm`**: Support for server testing using a test host in Ktor.

## Exposed Dependencies

Exposed is an ORM (Object-Relational Mapping) library for Kotlin:

- **`org.jetbrains.exposed:exposed-core:0.41.1`**: The core of the Exposed library, providing basic functionalities for working with databases.
- **`org.jetbrains.exposed:exposed-jdbc:0.41.1`**: Exposed extension that allows integration with JDBC for database interaction.
- **`org.jetbrains.exposed:exposed-java-time:0.41.1`**: Support for Java Time types in Exposed.

## Database Dependencies

- **`com.h2database:h2:2.1.214`**: H2 database engine for in-memory and disk-based databases, useful for testing and development.
- **`org.postgresql:postgresql:42.4.2`**: JDBC driver for PostgreSQL databases, necessary for interaction with PostgreSQL databases.

## Logging Dependencies

- **`ch.qos.logback:logback-classic:$logback_version`**: Logging implementation for Java, used to log events in the application.

## Kotlin Dependencies

- **`org.jetbrains.kotlinx:kotlinx-datetime:0.6.0`**: Provides support for date and time manipulation in Kotlin.

## Testing Dependencies

- **`org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version`**: Testing library for Kotlin, compatible with JUnit.
