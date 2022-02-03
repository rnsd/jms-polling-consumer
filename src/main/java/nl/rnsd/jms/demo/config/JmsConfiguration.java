package nl.rnsd.jms.demo.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@Configuration
@EnableJms
public class JmsConfiguration {
    @Bean
    public Queue createQueue(){
        return new ActiveMQQueue("test.queue");
    }
}
