package com.example.postorders.services;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable{

    private String accont;

    private long SubmittedAt;

    private long ReceivedAt;

    private String market;

    private String action;

    private int size;


}
