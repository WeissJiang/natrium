open module nano.bot.common {
    exports nano.support;
    exports nano.support.mail;
    exports nano.support.configuration;
    exports nano.support.validation;
    exports nano.support.templating;
    exports nano.support.jdbc;

    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires spring.aop;
    requires spring.beans;
    requires spring.core;
    requires org.slf4j;
    requires spring.context.support;
    requires jakarta.mail;
    requires spring.context;
    requires java.desktop;

    requires static org.apache.tomcat.embed.core;
    requires static spring.jdbc;
    requires static spring.web;
    requires static spring.webmvc;
}
