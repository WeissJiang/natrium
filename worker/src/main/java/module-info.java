open module nano.bot.worker {
    requires org.jetbrains.annotations;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.rabbit;
    requires spring.beans;
    requires org.slf4j;
    requires jakarta.mail;
    requires spring.amqp;
    requires spring.context;
    requires spring.context.support;
    requires nano.bot.common;
}
