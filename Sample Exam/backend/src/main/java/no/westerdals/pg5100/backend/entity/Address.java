package no.westerdals.pg5100.backend.entity;

import no.westerdals.pg5100.backend.validation.Country;

import javax.persistence.Embeddable;

@Embeddable
public class Address {

    private String street;
    private int postcode;

    @Country
    private String country;


    public Address() {}


    public String getStreet() { return street; }

    public void setStreet(String street) { this.street = street; }

    public int getPostcode() { return postcode; }

    public void setPostcode(int postcode) { this.postcode = postcode; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }
}
