package com.sage.sage.microservices.device.repository

import com.azure.cosmos.models.PartitionKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.sage.sage.microservices.azure.AzureInitializer
import com.sage.sage.microservices.user.model.response.DeviceModel
import org.springframework.stereotype.Repository


@Repository
class DeviceRepository(
    private val azureInitializer: AzureInitializer
): IDeviceRepository {

    @JsonProperty("userKey") val deviceKey = "device"

    override fun getDevice(deviceId: String): DeviceModel? {
        val response = azureInitializer.userContainer?.readItem(
            deviceId,
            PartitionKey(deviceKey),
            DeviceModel::class.java
        )
        return response?.item
    }
}