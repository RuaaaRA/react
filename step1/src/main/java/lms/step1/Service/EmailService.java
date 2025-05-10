package lms.step1.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            log.info("📧 Preparing to send email to: {}", to);
            log.debug("📨 Subject: {}", subject);
            log.debug("📝 Content: {}", text);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            log.info("✅ Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send email to: {}", to, e);
        }
    }
}
