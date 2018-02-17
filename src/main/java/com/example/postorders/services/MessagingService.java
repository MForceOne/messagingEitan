package com.example.postorders.services;

import javax.jms.JMSException;

public interface MessagingService {

    void sendOrder(Order order,DestinationType destinationType,String brokerAddress,String destinationAddress) throws
            JMSException;
}
