package com.northernneckgarbage.nngc.controller;


import com.northernneckgarbage.nngc.email.EmailService;
import com.northernneckgarbage.nngc.entity.dto.UserMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("api/nngc/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // Constructor injection is recommended for the EmailService

    @PostMapping("/sendToSales")
    public ResponseEntity<?> sendDirectMessageToSales(@RequestBody UserMessageDTO userMessageDTO) {
        try {
            emailService.sendDirectMessageToSales(
                    userMessageDTO.getUserEmail(),
                    userMessageDTO.getUserPhone(),
                    userMessageDTO.getUserName(),
                    userMessageDTO.getMessage()
            );
            return ResponseEntity.ok().body("Message sent successfully to Sales.");
        } catch (IOException e) {
            // Handle the exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message.");
        }
    }
}
