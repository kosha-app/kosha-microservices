package com.sage.sage.microservices.user.email

import jakarta.mail.MessagingException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service


@Service
class EmailService (
    private val javaMailSender: JavaMailSender
) {
    @Throws(MessagingException::class)
    fun sendOtpEmail(toEmail: String, otp: String, expiryMinutes: Int) {
        val message = javaMailSender.createMimeMessage()

        val helper = MimeMessageHelper(message, true, "UTF-8")
        helper.setTo(toEmail)
        helper.setSubject(EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT)

        // Replace placeholders with actual values
        val formattedBody = String.format(
            EmailTemplateConstants.VERIFICATION_EMAIL_BODY,
            otp
        ).replace("{EXPIRY_DURATION}", expiryMinutes.toString())

        helper.setText(formattedBody, true)


        javaMailSender.send(message)
    }

}
