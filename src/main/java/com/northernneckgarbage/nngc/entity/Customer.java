package com.northernneckgarbage.nngc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "phone", nullable = false, length = 13, unique = true)
    private String phone;
    @Column(name = "house_number", nullable = false, length = 8)
    private String houseNumber;
    @Column(name = "street_name", nullable = false, length = 50)
    private String streetName;
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    @Column(name = "state", nullable = false, length = 2)
    private String state;
    @Column(name = "zip_code", nullable = false, length = 5)
    private String zipCode;
    @Column(name = "county",nullable=true, length = 50)
    private String county;
    @Column(name = "notes", length = 500)
    private String notes;
}
