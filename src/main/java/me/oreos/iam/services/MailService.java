package me.oreos.iam.services;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    MailService to(String... to);
    MailService subject(String subject);
    MailService body(String body);
    MailService attachmentPath(String attachmentPath);
    MailService cc(String... cc);
    MailService fromName(String fromName);
    MailService fromEmail(String fromEmail);
    CompletableFuture<Void> send() throws Exception;
} 
