package nl.rnsd.jms.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class TemplateBasedJmsController {

    private final JmsTemplate customJmsTemplate;

    @GetMapping("/template/publish/{payload}")
    public ResponseEntity<Void> publishJmsMessage(@PathVariable("payload") String payload) throws JMSException {
        log.info("Start publish");
        for (int i = 0; i < 100; i++) {
            customJmsTemplate.convertAndSend(payload);
        }
        log.info("End publish");

        log.info("Published message : {}", payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/template/poll/{ack}")
    public ResponseEntity<Void> pollJmsMessage(@PathVariable("ack") boolean ackKnowledge) throws JMSException, InterruptedException {
        log.info("Start poll");
        for (int i = 0; i < 100; i++) {
            try {
                poll(ackKnowledge);
            } catch (Exception e) {
                log.info("Conn error : " + e.getMessage());
            }
            Thread.sleep(1000);
        }
        log.info("End poll");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void poll(boolean ackKnowledge) {
        String message = this.customJmsTemplate.execute(session -> {
            MessageConsumer consumer = session.createConsumer(
                this.customJmsTemplate.getDestinationResolver().resolveDestinationName(session, "test.queue", false));

            String res = null;
            try {
                Message received = consumer.receiveNoWait();
                if (received != null) {
                    res = (String) requireNonNull(this.customJmsTemplate.getMessageConverter()).fromMessage(received);
                    if (ackKnowledge) {
                        received.acknowledge();
                    } else {
                        session.recover();
                    }
                }
            } catch (Exception e) {
                res = null;
            } finally {
                consumer.close();
            }
            return res;
        }, true);

        //log
        if (message != null) {
            if (ackKnowledge) {
                log.info("Polled message : {}", message);
            } else {
                log.info("Polled message, no acknowledge. {}", message);
            }
        } else {
            log.info("Polled message, message is null");
        }
    }
}
