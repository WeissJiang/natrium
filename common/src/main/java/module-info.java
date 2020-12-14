open module nano.bot.common {
    exports nano.support;
    exports nano.support.mail;
    exports nano.support.configuration;
    exports nano.support.validation;
    exports nano.support.templating;
    exports nano.support.jdbc;

    requires org.jetbrains.annotations;

    requires static java.desktop;
    requires static jakarta.mail;
    requires static org.apache.tomcat.embed.core;
    requires static com.fasterxml.jackson.core;
    requires static com.fasterxml.jackson.databind;
    requires static com.fasterxml.jackson.datatype.jsr310;
    requires static org.slf4j;
    requires static spring.core;
    requires static spring.beans;
    requires static spring.context;
    requires static spring.context.support;
    requires static spring.aop;
    requires static spring.jdbc;
    requires static spring.web;
    requires static spring.webmvc;
}
