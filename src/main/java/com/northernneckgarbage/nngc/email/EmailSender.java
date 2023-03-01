package com.northernneckgarbage.nngc.email;

import com.sendgrid.Content;
import com.sendgrid.Email;

public interface EmailSender {

   void sendWithSendGrid(String to, String subject, Content content) throws java.io.IOException;
}
