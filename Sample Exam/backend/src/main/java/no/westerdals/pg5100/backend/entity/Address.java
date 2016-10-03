package no.westerdals.pg5100.backend.entity;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Address {
    private String street;
    private int zipcode;
    private String country;
}
