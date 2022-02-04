package nl.rnsd.jms.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

@Component
@Slf4j
public class ConsumerBasedQueueService {

    private ActiveMQConnection connection;
    private ActiveMQSession session;
    private ActiveMQMessageConsumer consumer;
    private ActiveMQMessageProducer producer;

    public ConsumerBasedQueueService(
    ) throws JMSException {
        init(
            "test.queue",
            "tcp://localhost:61616",
            "admin",
            "admin");
    }

    public Message poll() throws JMSException {
        return consumer.receiveNoWait();
    }

    public void send(String payload) throws JMSException {
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(payload);
        producer.send(message);
    }

    public void recover() throws JMSException {
        session.recover();
    }

    private void init(String queueName,
                      String brokerUrl,
                      String brokerUsername,
                      String brokerPassword) throws JMSException {
//        var connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
//        connectionFactory.setTrustedPackages(List.of("nl.rnsd.jms.demo"));
//
//        this.connection = (ActiveMQConnection) connectionFactory.createConnection(brokerUsername, brokerPassword);
//        RedeliveryPolicy policy = connection.getRedeliveryPolicy();
//        policy.setInitialRedeliveryDelay(500);
//        policy.setBackOffMultiplier(2);
//        policy.setUseExponentialBackOff(true);
//        policy.setMaximumRedeliveries(2);
//        connection.start();
//
//        this.session = (ActiveMQSession) connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//        Destination destinationFrom = session.createQueue(queueName);
//        this.consumer = (ActiveMQMessageConsumer) session.createConsumer(destinationFrom);
//        this.producer = (ActiveMQMessageProducer) session.createProducer(destinationFrom);
    }

    @PreDestroy
    private void close() throws JMSException {
        session.close();
        connection.close();
    }

}
