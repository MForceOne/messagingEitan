package com.example.postorders.services;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UploadServiceImpl implements UploadService {

    @Autowired
    private UsersService usersService;
    @Autowired
    private MessagingService messagingService;
    private XStream orderParser = initOrderParser();

    @Override
    public UploadResult uploadOrders(
            String user, String password, String brokerAddress, String destinationName, DestinationType destinationType,
            ByteArrayOutputStream file) {
        if (user.isEmpty() || password.isEmpty() || brokerAddress.isEmpty() || destinationName.isEmpty() ||
                destinationType == null || file ==
                null) {
            return getFailedResult("All fields are mandatory");
        }
        if (!usersService.isAuthorisedToUploadOrders(user, password)) {
            return getFailedResult(MessageFormat.format("User: {0} is not authorised to upload orders", user));
        }

        List<Order> orders;
        try {
            orders = parseOrders(file);
        } catch (IOException e) {
            return getFailedResult("Uploaded file  is not a valid orders file");
        }
        if(orders.isEmpty()){
            return getFailedResult("Uploaded file  does not contain any orders");
        }

        try {
            for(Order order: orders){
                messagingService.sendOrder(order,destinationType,brokerAddress,destinationName);

            }
        } catch (Exception e) {
            return  getFailedResult(MessageFormat.format("Failed to send order:{0}, error:{1}",orders,e.getMessage()));
        }

        return new UploadResult(UploadResult.uploadStatus.SUCCESS, null);
    }

    private List<Order> parseOrders(ByteArrayOutputStream file) throws IOException {
        File tempFile = File.createTempFile("order",".txt");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(file.toByteArray());

        return Files.lines(Paths.get(tempFile.getAbsolutePath()))
                .filter(x -> x.matches("<Order>.*</Order>"))
                .map(x ->  orderParser.fromXML(x))
                .map(x -> (Order)x)
                .collect(Collectors.toList());

    }

    private XStream initOrderParser() {
        XStream xStream = new XStream();
        xStream.alias("Order",Order.class);
        return xStream;
    }

    private UploadResult getFailedResult(String errorMessage) {
        return new UploadResult(UploadResult.uploadStatus.FAILURE, errorMessage);
    }

}
