package nl.rnsd.jms.demo.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TemplateBasedQueueService {

    private final JmsTemplate customJmsTemplate;


    public Message poll() {
        return customJmsTemplate.receive();
    }

    public void send(String payload) {
        customJmsTemplate.convertAndSend(payload);
    }

}
