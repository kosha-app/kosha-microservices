package com.sage.sage.microservices.user.email

import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


@Bean
fun getJavaMailSender(): JavaMailSender {
    val mailSender = JavaMailSenderImpl()
    mailSender.host = "smtp.gmail.com"
    mailSender.port = 587
    mailSender.username = "clifortramaramela@gmail.com"
    mailSender.password = "dhxx myhx ujzx gzbk"
    val props: Properties = mailSender.javaMailProperties
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.debug", "true")
    return mailSender
}
