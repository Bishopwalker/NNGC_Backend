package com.northernneckgarbage.nngc.entity;

import com.northernneckgarbage.nngc.roles.AppUserRoles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@Table(name = "customer")
@NoArgsConstructor
public class Customer  implements UserDetails {
    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customer_seq")
    private long id;
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    @Column(name = "phone", length = 13, unique = true)
    private String phone;
    @Column(name = "house_number", length = 8)
    private String houseNumber;
    @Column(name = "street_name", length = 50)
    private String streetName;
    @Column(name = "city", length = 50)
    private String city;
    @Column(name = "state", length = 2)
    private String state;
    @Column(name = "zip_code", length = 5)
    private String zipCode;
    @Column(name = "county",nullable=true, length = 50)
    private String county;
    @Column(name = "notes", length = 500)
    private String notes;




    private Boolean locked = false;
    private Boolean enabled = false;
    @Enumerated(EnumType.STRING)
    private AppUserRoles appUserRoles;

    public Customer(String firstName, String lastName, String email, String password, String phone, String houseNumber, String streetName, String city, String state, String zipCode, String county, String notes, AppUserRoles appUserRoles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.county = county;
        this.notes = notes;
        this.appUserRoles = appUserRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRoles.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
