package com.example.demo.Mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.eclipse.angus.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

@Component
public class GmailOAuth2Sender {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ✅ Updated: send HTML mail
    public void send(String to, String subject, String body, String accessToken) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);

        // ✅ Send HTML content instead of plain text
        msg.setContent(body, "text/html; charset=utf-8");

        SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
        transport.connect(host, fromEmail, accessToken);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    // ✅ Send with attachment (can also be HTML body)
    public void sendWithAttachment(String to, String subject, String body,
                                   Resource attachment, String filename, String accessToken) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        Session session = Session.getInstance(props);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);

        // Body part (HTML)
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(body, "text/html; charset=utf-8");

        // Attachment part
        MimeBodyPart attachmentPart = new MimeBodyPart();
        try (InputStream is = attachment.getInputStream()) {
            attachmentPart.setFileName(filename);
            attachmentPart.setContent(is.readAllBytes(), "application/octet-stream");
        }

        // Combine parts
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        msg.setContent(multipart);

        SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
        transport.connect(host, fromEmail, accessToken);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
}
