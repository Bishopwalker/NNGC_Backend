package com.northernneckgarbage.nngc.registration;

import lombok.*;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class RegistrationRequest {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
}
