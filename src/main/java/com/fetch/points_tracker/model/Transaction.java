package com.fetch.points_tracker.model;

import java.time.ZonedDateTime;

public class Transaction {
    private String payer;
    private int points;
    private ZonedDateTime timestamp; // Use ZonedDateTime here

    public Transaction(String payer, int points, ZonedDateTime timestamp) {
        this.payer = payer;
        this.points = points;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getPayer() { return payer; }
    public void setPayer(String payer) { this.payer = payer; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public ZonedDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString(){
        return "PAYER: " + payer + ", POINTS: " + points + ", TIMESTAMP: " + timestamp;
    }
}
