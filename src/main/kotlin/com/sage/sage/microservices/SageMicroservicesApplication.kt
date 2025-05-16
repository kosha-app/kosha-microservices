package com.sage.sage.microservices

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SageMicroservicesApplication

var dbUsername: String = System.getenv("CUSTOMCONNSTR_MY_SQL_DB_URL")
var dbUsername2: String = System.getenv("MY_SQL_DB_URL")

fun main(args: Array<String>) {
    runApplication<SageMicroservicesApplication>(*args)
    println("SageTheMan --------------------------- 1 ------------------ $dbUsername")
    println("SageTheMan --------------------------- 2 ------------------ $dbUsername2")
}



