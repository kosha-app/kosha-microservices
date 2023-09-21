package com.sage.sage.microservices.device.repository

import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.CosmosPatchOperations
import com.azure.cosmos.models.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.response.DeviceModel
import org.springframework.stereotype.Repository


@Repository
class DeviceRepository(
    private val azureInitializer: AzureInitializer
): IDeviceRepository {

    @JsonProperty("userKey") val deviceKey = "device"
    val profileUserKey = "profile"

    override fun getDevice(deviceId: String): DeviceModel? {
        val response = azureInitializer.userContainer?.readItem(
            deviceId,
            PartitionKey(deviceKey),
            DeviceModel::class.java
        )
        return response?.item
    }

    override fun deleteDevice(deviceId: String) {
        azureInitializer.userContainer?.deleteItem(
            deviceId,
            PartitionKey(deviceKey),
            CosmosItemRequestOptions()
        )
    }

    override fun deleteDeviceFromUser(userId: String, devices: List<DeviceModel>){
        azureInitializer.userContainer?.patchItem(
            userId,
            PartitionKey(profileUserKey),
            CosmosPatchOperations.create()
                .replace("/devices", devices), User::class.java
        )
    }
}