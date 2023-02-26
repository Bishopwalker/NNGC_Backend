package com.northernneckgarbage.nngc.email;

import com.sendgrid.Content;
import com.sendgrid.Email;

public interface EmailSender {
    void send(String to, String subject);
   void sendWithSendGrid(String to, String subject, Content content) throws java.io.IOException;
}
