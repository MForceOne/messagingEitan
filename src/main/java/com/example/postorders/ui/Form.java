package com.example.postorders.ui;

import com.example.postorders.services.DestinationType;
import com.example.postorders.services.UploadResult;
import com.example.postorders.services.UploadService;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;

@SpringUI
@Theme("valo")
public class Form extends UI {

    private TextField url = new TextField("Connection string to the broker");
    private TextField user = new TextField("Username");
    private TextField password = new TextField("Password");
    private TextField destinationName = new TextField("Destination name");
    private RadioButtonGroup<DestinationType> destinationType= getDestinationType();
    private Upload upload = getUpload();
    private ByteArrayOutputStream ordersFile = new ByteArrayOutputStream();
    private Button submitButton = getSubmitButton();
    @Autowired
    private UploadService uploadService;

    private Button getSubmitButton() {
        Button result =new Button("Submit orders");
        result.addClickListener(this::SubmitForm);
        result.setEnabled(false);
        return  result;
    }

    private Upload getUpload() {
        Upload upload = new Upload(null, (x,y) -> ordersFile);
        upload.setButtonCaption("Upload orders");
        upload.addSucceededListener(x -> {
            upload.setButtonCaption(x.getFilename());
            submitButton.setEnabled(true);
        });
        return upload;
    }

    private RadioButtonGroup<DestinationType> getDestinationType() {
        RadioButtonGroup<DestinationType> destinationType = new RadioButtonGroup<>();
        destinationType.setItems(DestinationType.values());
        return  destinationType;
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout view = new VerticalLayout();
        view.addComponent(url);
        view.addComponent(user);
        view.addComponent(password);
        view.addComponent(destinationName);
        view.addComponent(destinationType);
        view.addComponent(upload);
        view.addComponent(submitButton);
        setContent(view);
    }

    private void SubmitForm(Button.ClickEvent event) {
        UploadResult result = uploadService.uploadOrders(user.getValue(), password.getValue(), url.getValue(),
                destinationName
                        .getValue(),
                destinationType.getValue(),
                ordersFile);
        if (result.getStatus() == UploadResult.uploadStatus.FAILURE) {
           displayError(result.getErrorMessage());
        }
        if(result.getStatus() == UploadResult.uploadStatus.SUCCESS ){
            Notification.show("Success", "Orders where sent successfully",
                    Notification.Type.HUMANIZED_MESSAGE);
        }

    }

    private void displayError(String errorMessage){
        Notification.show("Failure", errorMessage,
                Notification.Type.ERROR_MESSAGE);
    }


}
