package me.oreos.iam.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import me.oreos.iam.services.MailService;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.CompletableFuture;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.from:${spring.mail.username}}")
    private String defaultFromEmail;

    @Value("${mail.from.name}")
    private String defaultFromName;

    private final JavaMailSender mailSender;

    private String[] to;
    private String subject;
    private String body;
    private String attachmentPath;
    private String[] cc;
    private String fromEmail;
    private String fromName;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public static MailServiceImpl builder(JavaMailSender sender) {
        return new MailServiceImpl(sender);
    }

    @Override
    public MailService to(String... to) {
        this.to = to;
        return this;
    }

    @Override
    public MailService subject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public MailService body(String body) {
        this.body = body;
        return this;
    }

    @Override
    public MailService attachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
        return this;
    }

    @Override
    public MailService cc(String... cc) {
        this.cc = cc;
        return this;
    }

    @Override
    public MailService fromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
        return this;
    }

    @Override
    public MailService fromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    @Override
    @Async
    public CompletableFuture<Void> send() throws Exception {

        try {
            if (to == null || subject == null || body == null)
                throw new IllegalArgumentException("To, Subject, and Body are required fields.");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, attachmentPath != null);

            helper.setTo(to);
            if (cc != null) {
                helper.setCc(cc);
            }

            helper.setSubject(subject);
            helper.setText(body, false);

            helper.setFrom(fromEmail != null ? fromEmail : defaultFromEmail, fromName != null ? fromName : defaultFromName);

            if (attachmentPath != null) {
                FileSystemResource file = new FileSystemResource(new File(attachmentPath));
                helper.addAttachment(file.getFilename(), file);
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new Exception("Failed to send email: " + e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
