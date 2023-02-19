package com.northernneckgarbage.nngc.registration;
;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationService {

    public String register(RegistrationRequest request) {
        log.info("New customer object created"+request);
        return "works";
    }
}
