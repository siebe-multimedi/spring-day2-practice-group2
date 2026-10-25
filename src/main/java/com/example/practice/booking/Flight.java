package com.example.practice.booking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue
    private Long id;

    // TODO step 2: add a version field, so that two bookings at the same moment are detected

    private String code;

    private int seatsLeft;

    protected Flight() {
    }

    public Flight(String code, int seatsLeft) {
        this.code = code;
        this.seatsLeft = seatsLeft;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }
}
