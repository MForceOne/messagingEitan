package com.example.postorders.services;

import java.io.ByteArrayOutputStream;

public interface UploadService {

    UploadResult uploadOrders(String user,String password,String brokerAddress,String destinationName,DestinationType
            destinationType,
            ByteArrayOutputStream
            file);

}
