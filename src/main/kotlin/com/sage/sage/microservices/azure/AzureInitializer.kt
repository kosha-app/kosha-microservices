package com.sage.sage.microservices.azure

import com.azure.communication.email.EmailClient
import com.azure.communication.email.EmailClientBuilder
import com.azure.cosmos.*
import com.azure.cosmos.models.CosmosContainerProperties
import com.azure.cosmos.models.CosmosContainerResponse
import org.springframework.stereotype.Component


@Component("cosmosInitializer")
class AzureInitializer {

    private lateinit var client: CosmosClient

    var database: CosmosDatabase? = null
    var container: CosmosContainer? = null
    var emailClient: EmailClient? = null

    private val containerName = "Items"

    private val userDatabaseName = "users"

    init{
        try {
            getStartedDemo()
            println("Demo complete, please hold while resources are released")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            System.err.println(String.format("Cosmos getStarted failed with %s", e))
        } finally {
            println("Closing the client")
        }
        initialiseEmailComms()
    }

    //  </Main>
    @Throws(Exception::class)
    private fun getStartedDemo() {
        System.out.println("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST)
        val preferredRegions = ArrayList<String>()
        preferredRegions.add("West US")

        //  Create sync client
        client = CosmosClientBuilder()
            .endpoint(AccountSettings.HOST)
            .key(AccountSettings.MASTER_KEY)
            .preferredRegions(preferredRegions)
            .userAgentSuffix("CosmosDBJavaQuickstart")
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient()
        createDatabaseIfNotExists()
        createContainerIfNotExists()
    }


    @Throws(java.lang.Exception::class)
    private fun createDatabaseIfNotExists() {
        println("Create database ${userDatabaseName}databaseName if not exists.")

        //  Create database if not exists
        val databaseResponse = client.createDatabaseIfNotExists(userDatabaseName)
        database = client.getDatabase(databaseResponse.properties.id)
        println("Checking database " + database?.id + " completed!\n")
    }

    @Throws(java.lang.Exception::class)
    private fun createContainerIfNotExists() {
        println("Create container $containerName if not exists.")

        //  Create container if not exists
        val containerProperties = CosmosContainerProperties(containerName, "/partitionKey")
        val containerResponse: CosmosContainerResponse =
            database!!.createContainerIfNotExists(containerProperties)
        container = database!!.getContainer(containerResponse.properties.id)
        println("Checking container completed!\n")
    }

    private fun close() {
        client.close()
    }

    private  fun initialiseEmailComms(){
        val connectionString = "endpoint=https://kosha-comms.unitedstates.communication.azure.com/;accesskey=+DadqhRyrUxr2Gp5ITii7Kz7acTPtXkgCr9X4MNfsWeKZA9L4y1hybRzdRSNac9urrX6U5cmjAhr4vU8U+hjBg=="

        emailClient = EmailClientBuilder()
            .connectionString(connectionString)
            .buildClient()

    }

}