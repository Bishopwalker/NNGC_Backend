package com.nngc.controller;


import com.nngc.dto.EmailRequest;
import com.nngc.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.nngc.email.EmailService.LOGGER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Slf4j
public class EmailController {

        private final EmailService emailService;

        @PostMapping("/send")
        public void sendEmail(@RequestBody EmailRequest emailRequest) throws IOException {
            emailService.sendDirectMessageToSales(emailRequest);
        }

//    @PostMapping("/sendToSales")
//    public ResponseEntity<?> sendDirectMessageToSales(@RequestBody UserMessageDTO userMessageDTO) {
//        try {
//            emailService.sendDirectMessageToSales(
//                    userMessageDTO.getUserEmail(),
//                    userMessageDTO.getUserPhone(),
//                    userMessageDTO.getUserName(),
//                    userMessageDTO.getMessage()
//            );
//            return ResponseEntity.ok().body("Message sent successfully to Sales.");
//        } catch (IOException e) {
//            // Handle the exception appropriately
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message.");
//        }
//    }

        @PostMapping("/sendToSales")
    public ResponseEntity<?> sendDirectMessageToSales(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendDirectMessageToSales(emailRequest);
            return ResponseEntity.ok().body("Message sent successfully to Sales.");
        } catch (IOException e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message.");
        }
    }
}



