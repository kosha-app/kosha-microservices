package com.sage.sage.microservices

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream
import java.util.*

@SpringBootApplication
class SageMicroservicesApplication

fun main(args: Array<String>) {
    val classLoader: ClassLoader = SageMicroservicesApplication::class.java.classLoader
    val serviceAccountPath = Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).file
    val serviceAccountStream = FileInputStream(serviceAccountPath)

    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
        .build()

    FirebaseApp.initializeApp(options)
    runApplication<SageMicroservicesApplication>(*args)

}
