package com.sage.sage.microservices.config.azure

import com.azure.communication.email.EmailClient
import com.azure.communication.email.EmailClientBuilder
import com.azure.cosmos.*
import com.azure.cosmos.models.CosmosContainerProperties
import com.azure.cosmos.models.CosmosContainerResponse
import com.sage.sage.microservices.exception.KoshaExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.springframework.stereotype.Component


@Component("cosmosInitializer")
class AzureInitializer {

    private val logger: Logger = LoggerFactory.getLogger(AzureInitializer::class.java)

    private lateinit var client: CosmosClient

    private var database: CosmosDatabase? = null
    var userContainer: CosmosContainer? = null
    var albumContainer: CosmosContainer? = null
    var emailClient: EmailClient? = null

    private val usersContainerName = "users"
    private val albumContainerName = "albums"

    private val userDatabaseName = "koshadb"

    init{
        try {
            getStartedDemo()
            logger.info("Demo complete, please hold while resources are released")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            logger.error(String.format("Cosmos getStarted failed with %s", e))
        } finally {
            logger.error("Closing the client")
        }
        initialiseEmailComms()
    }

    //  </Main>
    @Throws(Exception::class)
    private fun getStartedDemo() {
        logger.info("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST)
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
        logger.info("Create database ${userDatabaseName}databaseName if not exists.")

        //  Create database if not exists
        val databaseResponse = client.createDatabaseIfNotExists(userDatabaseName)
        database = client.getDatabase(databaseResponse.properties.id)
        logger.info("Checking database " + database?.id + " completed!\n")
    }

    @Throws(java.lang.Exception::class)
    private fun createContainerIfNotExists() {
        logger.info("Create container $usersContainerName if not exists.")
        logger.info("Create container $albumContainerName if not exists.")

        //  Create container if not exists
        val userContainerProperties = CosmosContainerProperties(usersContainerName, "/userKey")
        val albumContainerProperties = CosmosContainerProperties(albumContainerName, "/albumKey")
        val userContainerResponse: CosmosContainerResponse = database!!.createContainerIfNotExists(userContainerProperties)
        val albumContainerResponse: CosmosContainerResponse = database!!.createContainerIfNotExists(albumContainerProperties)
        userContainer = database!!.getContainer(userContainerResponse.properties.id)
        albumContainer = database!!.getContainer(albumContainerResponse.properties.id)
        logger.info("Checking container completed!\n")
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