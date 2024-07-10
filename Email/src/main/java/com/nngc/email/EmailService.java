package com.nngc.email;

import com.sendgrid.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailService  implements EmailSender {

   Dotenv dotenv = Dotenv.load();




    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    @Override
    public void sendWithSendGrid(String to, String subject, Content content) throws IOException {
        Email from = new Email("bishop@northernneckgarbage.com");
Email emailTo = new Email(to);
      Mail mail = new Mail(from, subject, emailTo, content);
        SendGrid sendGrid = new SendGrid(dotenv.get("SENDGRID_API_KEY"));

        Request request = new Request();
LOGGER.info("Sending email to " + to);
        try {
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sendGrid.api(request);
            LOGGER.warn("Sending email to " +response.getStatusCode());
            LOGGER.info("Response Body " +response.getBody());
            LOGGER.info("Response Headers " +response.getHeaders());
        } catch (IOException ex) {
          throw ex;
        }
    }

    public void sendDirectMessageToSales(String userEmail, String userPhone, String userName, String message) throws IOException {
        String recipientEmail = "bishop@northernneckgarbage.com";
        String subject = "New Message from " + userName;
        String formattedMessage = buildEmailContent(userEmail, userPhone, userName, message);
        Content content = new Content("text/plain", formattedMessage);

        Email from = new Email("noreply@northernneckgarbage.com");
        Email to = new Email(recipientEmail);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sendGrid = new SendGrid(dotenv.get("SENDGRID_API_KEY"));

        Request request = new Request();
        LOGGER.info("Sending direct message to Sales from: " + userEmail);
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            LOGGER.info("Email sent with status code: " + response.getStatusCode());
        } catch (IOException ex) {
            LOGGER.error("Error in sending email: " + ex.getMessage());
            throw ex;
        }
    }

    private String buildEmailContent(String userEmail, String userPhone, String userName, String message) {
        return "User Email: " + userEmail + "\nUser Phone: " + userPhone + "\nUser Name: " + userName + "\n\nMessage:\n" + message;
    }


}
