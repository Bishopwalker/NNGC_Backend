package com.northernneckgarbage.nngc.entity;

import com.northernneckgarbage.nngc.entity.dto.AddressDTO;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.roles.AppUserRoles;
import com.northernneckgarbage.nngc.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@Builder
public class Customer  implements UserDetails {

    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "customer_seq")

    private long id;
    @Column(name = "first_name",  length = 50)
    private String firstName;
    @Column(name = "last_name",  length = 50)
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
    @Column(name = "county", length = 50)
    private String county;
    @Column(name = "geo_location", length = 10000,unique = true)
    private String  geoLocation;


    @Column(name = "latitude", length = 150)

    private Double latitude ;
    @Column(name = "longitude", length = 150)
    private Double longitude;


  @Column(name = "stripe_customer_id", length = 50)
private String stripeCustomerId;

    @OneToMany(mappedBy = "transactionId", cascade = CascadeType.ALL)
    private List<StripeTransactions> stripeTransactions;
    @Column(name = "enabled", nullable = false)
private boolean enabled = false;




    @Enumerated(EnumType.STRING)
    private AppUserRoles appUserRoles;
    @OneToMany(mappedBy = "token", cascade = CascadeType.ALL)
    private List<Token> tokens;


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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled ;
    }


    public CustomerDTO toCustomerDTO() {
        return CustomerDTO.builder()
                .id(id)
                .fullName(firstName + " " + lastName)
                .email(email)
                .phoneNumber(phone)

                .address(AddressDTO.builder()
                        .line1(houseNumber + " " + streetName)
                        .city(city)
                        .state(state)
                        .zipCode(zipCode)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build())

                .role(appUserRoles)
                .enabled(enabled)
                .stripeCustomerId(stripeCustomerId)
                .geoLocation(geoLocation)
                .build();
    }
}
