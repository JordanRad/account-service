package com.example.accountservice.models;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String countryCode;

    @NotNull
    private String city;

    @NotNull
    private String street;

    private int houseNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Address(long id, String countryCode, String city, String street, int houseNumber) {
        this.id = id;
        this.countryCode = countryCode;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }
}
