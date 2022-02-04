package nl.rnsd.jms.demo.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.Queue;

import static javax.jms.Session.CLIENT_ACKNOWLEDGE;

@Configuration
@EnableJms
public class JmsConfiguration {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String DESTINATION = "test.queue";

    @Bean
    public Queue createQueue() {
        return new ActiveMQQueue(DESTINATION);
    }

    @Bean
    public ActiveMQConnectionFactory senderConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(BROKER_URL);
        activeMQConnectionFactory.setTransactedIndividualAck(true);
        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory =
            new CachingConnectionFactory(senderConnectionFactory());
        cachingConnectionFactory.setSessionCacheSize(10);
        cachingConnectionFactory.setCacheConsumers(true);
        return cachingConnectionFactory;
    }

    @Bean
    public Destination destination() {
        return new ActiveMQQueue(DESTINATION);
    }

    @Bean
    public JmsTemplate customJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setDefaultDestination(destination());
        jmsTemplate.setSessionAcknowledgeMode(CLIENT_ACKNOWLEDGE);
        return jmsTemplate;
    }
}
