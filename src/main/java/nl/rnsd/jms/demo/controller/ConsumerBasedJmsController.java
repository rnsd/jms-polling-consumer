package nl.rnsd.jms.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rnsd.jms.demo.consumer.ConsumerBasedQueueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ConsumerBasedJmsController {

    private final ConsumerBasedQueueService consumerBasedQueueService;

    @GetMapping("/publish/{payload}")
    public ResponseEntity<Void> publishJmsMessage(@PathVariable("payload") String payload) throws JMSException {
        consumerBasedQueueService.send(payload);
        log.info("Published message : {}", payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/poll/{ack}")
    public ResponseEntity<Void> pollJmsMessage(@PathVariable("ack") boolean ackKnowledge) throws JMSException {
        Message message = consumerBasedQueueService.poll();

        if (message != null) {
            if (ackKnowledge) {
                message.acknowledge();
                log.info("Polled message : {}", ((TextMessage) message).getText());
            } else {
                consumerBasedQueueService.recover(); //needed for redelivery : https://activemq.apache.org/message-redelivery-and-dlq-handling
                log.info("Polled message, no acknowledge. {}", ((TextMessage) message).getText());
            }
        } else {
            log.info("Polled message, message is null");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
