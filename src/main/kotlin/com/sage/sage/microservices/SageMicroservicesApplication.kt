package com.sage.sage.microservices

import com.azure.cosmos.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.FileInputStream
import java.util.*


@SpringBootApplication
class SageMicroservicesApplication

    fun main(args: Array<String>) {
        runApplication<SageMicroservicesApplication>(*args)
    }



