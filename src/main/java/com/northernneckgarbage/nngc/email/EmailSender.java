package com.northernneckgarbage.nngc.email;

import com.sendgrid.Content;

public interface EmailSender {

   void sendWithSendGrid(String to, String subject, Content content) throws java.io.IOException;
}
