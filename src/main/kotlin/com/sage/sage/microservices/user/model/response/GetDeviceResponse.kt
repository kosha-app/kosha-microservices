package com.sage.sage.microservices.user.model.response

import com.azure.core.annotation.Get

class GetDeviceResponse (
    val userId: String? = null,
    val message: String? = null
){
    companion object{
        fun DeviceModel.toResponse(): GetDeviceResponse{
            return GetDeviceResponse(
                userId
            )
        }
    }
}
