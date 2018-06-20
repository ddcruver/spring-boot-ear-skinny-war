# Spring Boot EAR with Skinny WARs

An example project showing the problems deploying Spring Boot applications to Wildfly in an EAR deployment using skinny WARs.

This project consist of several deployment scenarios:

## Scenario 1: Single Enterprise Application Archive

A single EAR containing two Spring Boot Web Applications, `web1` and `web2`.

The EAR contains all (with one exception) the dependencies that both `web1` and `web2` require.
This avoids the duplicated dependencies.  The wars are packaged as skinny WARs.

## Scenario 2: Dependency Platform (for JBoss/WildFly)

A EAR containing all dependencies, `platform`.

A EAR, `skinny-ear` containing two Spring Boot Web Applications, `web1` and `web2`.
Both the EAR and the WAR are skinny and do not include any dependencies provided by `platform`.

A standalone WAR, `skinny-war`, providing the context `/skinny-web`

# Problems

1. Wildfly does not find `SpringServletContainerInitializer` if it is in the EAR `lib` directory. This is 
discussed on https://issues.jboss.org/browse/WFLY-4205 and https://jira.spring.io/browse/SPR-12555. A fix should be in 
9.0.0.Beta1 upwards, but it did not work for me.
So I had to create  the `servlet-container-initializer-meta-inf` module and force it to be included in each `WEB-INF/lib` directory.
2. When `SpringServletContainerInitializer` is called it does not find Spring Boot `WebApplicationInitializer` classes in
`WEB-INF/classes`. When it interrogates the class loader, it only finds classes from the EAR `lib` folder. However, the 
WAR classes are on the class path and are found by Spring once it starts up. As a temporary work-around, I created a common Spring Boot
application class and included it as a common JAR.

# Workarounds

Since the Servlet 3.0 spec make externalizing dependencies out of the war troublesome
we will instead use a method known to allow Spring Boot 1.x run within a Servlet 3.0 container.

Using the master branch of [dyser/spring-boot-legacy](https://github.com/dsyer/spring-boot-legacy) that has been updated to Spring Boot 2.0.3
we show that you can use Spring Boot 2 in an EAR with Skinny WARs in WildFly.

Tested Versions:
- WildFly 8.2.1.Final
- WildFly 9.0.2.Final
- WildFly 10.1.0.Final
- WildFly 11.0.0.Final
- WildFly 12.0.0.Final

## Further work

Spring loads a completely separate context for each WAR. I would like it to share a root context to speed up loading and allow
for shared components. There is an unanswered question on Stack Overflow http://stackoverflow.com/questions/31667392/spring-boot-ear-packaging and
I have not yet found a solution.

## Install

1. Download WildFly 8.2.1.Final+
2. Build the project using `mvn clean package`
3. Deploy the spring-boot-ear-2.0.0-SNAPSHOT.ear file to WildFly.

You should be able to hit http://localhost:8080/web1 and http://localhost:8080/web2

Username: user
Password: password

```$log
14:09:11,335 INFO  [org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration] (ServerService Thread Pool -- 79)

Using generated security password: 28f03f81-83fa-482f-9121-23950b634ead

```
