package com.sage.sage.microservices.device.repository

import com.sage.sage.microservices.device.model.request.UpdateLogInRequest
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse
import com.sage.sage.microservices.device.model.response.CheckDeviceResponse.Companion.toCheckDeviceResponse
import com.sage.sage.microservices.device.repository.DevicesDatabaseConstants.DATABASE_DEVICES_COLLECTION
import com.sage.sage.microservices.device.repository.DevicesDatabaseConstants.DATABASE_DEVICE_LOGIN
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.WriteResult
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository


@Repository
class DeviceRepository: IDeviceRepository {
    override fun checkDevice(deviceId: String): CheckDeviceResponse? {
        val database = FirestoreClient.getFirestore()
        val document: ApiFuture<DocumentSnapshot> = database.collection(DATABASE_DEVICES_COLLECTION).document(deviceId).get()

        val documentSnapshot = document.get()

        return if (documentSnapshot.exists()) documentSnapshot.toCheckDeviceResponse("Device check Successful") else null
    }

    override fun updateDeviceLogIn(deviceId: String, updateLogInRequest: UpdateLogInRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_DEVICES_COLLECTION).document(deviceId)

        val future = docRef.update(DATABASE_DEVICE_LOGIN, updateLogInRequest.newLoggedIn)
        val result: WriteResult = future.get()
        return result.updateTime.toString()
    }

}