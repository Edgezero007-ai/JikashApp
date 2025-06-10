package com.gcash.model;

import java.util.Date;

public class Transaction {
    private String type;
    private double amount;
    private String relatedUsername;
    private Date timestamp;

    public Transaction(String type, double amount, String relatedUsername, Date timestamp) {
        this.type = type;
        this.amount = amount;
        this.relatedUsername = relatedUsername;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getRelatedUsername() { return relatedUsername; }
    public Date getTimestamp() { return timestamp; }
}