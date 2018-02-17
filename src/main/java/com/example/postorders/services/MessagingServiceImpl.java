package com.example.postorders.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
@Slf4j
@Component
public class MessagingServiceImpl implements MessagingService {

    @Override
    public void sendOrder(
            Order order, DestinationType destinationType, String brokerAddress, String destinationAddress)
            throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerAddress);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = getDestination(session,destinationAddress,destinationType);

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        ObjectMessage orderMessage = session.createObjectMessage(order);

        // Tell the producer to send the message
        producer.send(orderMessage);

        log.info("Order:{} was sent successfully to:{}",order,destinationAddress);

        // Clean up
        session.close();
        connection.close();
    }

    private Destination getDestination(Session session ,String address, DestinationType type) throws JMSException {
        switch (type) {
            case TOPIC:
                return session.createTopic(address);
            case QUEUE:
                return  session.createQueue(address);
        }
        return  null;
    }
}
