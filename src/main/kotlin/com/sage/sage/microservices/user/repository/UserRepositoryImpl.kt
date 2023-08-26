package com.sage.sage.microservices.user.repository

import com.sage.sage.microservices.device.repository.DevicesDatabaseConstants.DATABASE_DEVICES_COLLECTION
import com.sage.sage.microservices.user.model.User
import com.sage.sage.microservices.user.model.User.Companion.toUser
import com.sage.sage.microservices.user.model.response.DeviceModel
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.DATABASE_COLLECTION
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_EMAIL
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_NAME
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_OTP
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_PASSWORD
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_SURNAME
import com.sage.sage.microservices.user.repository.UserDatabaseConstant.USER_DATABASE_VERIFICATION
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.WriteResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import com.sage.sage.microservices.user.model.request.*
import org.apache.http.client.HttpResponseException
import org.springframework.stereotype.Repository
import kotlin.random.Random


@Repository
class UserRepositoryImpl : UserRepository {

    override fun create(userRegistrationRequest: UserRegistrationRequest): String {
        val database = FirestoreClient.getFirestore()
        val firebaseAuth = FirebaseAuth.getInstance()
        val otp = generateSixDigitOTP()
        val mail = hashMapOf(
            "to" to listOf(userRegistrationRequest.email), "message" to hashMapOf(
                "subject" to EmailTemplateConstants.VERIFICATION_EMAIL_SUBJECT,
                "text" to EmailTemplateConstants.VERIFICATION_EMAIL_BODY.format(otp),
            )
        )

        try {
            firebaseAuth.createUser(
                UserRecord.CreateRequest().setUid(userRegistrationRequest.username)
                    .setEmail(userRegistrationRequest.email).setPassword(userRegistrationRequest.password)
            )

            val docRef = database.collection(DATABASE_COLLECTION).document(userRegistrationRequest.username)
            val data: MutableMap<String, Any> = HashMap()


            data["name"] = userRegistrationRequest.name
            data["surname"] = userRegistrationRequest.surname
            data["email"] = userRegistrationRequest.email
            data["cellNumber"] = userRegistrationRequest.cellNumber
            data["devices"] = userRegistrationRequest.devices
            data["otp"] = otp
            data["isVerified"] = false

            createDevice(userRegistrationRequest.devices[0])
            database.collection("user_mail").add(mail)

            val result: ApiFuture<WriteResult> = docRef.set(data)
            return result.get().updateTime.toString()
        } catch (e: HttpResponseException) {
            return e.localizedMessage
        }
    }

    override fun createDevice(deviceModel: DeviceModel): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_DEVICES_COLLECTION).document(deviceModel.deviceId)
        val data: MutableMap<String, Any> = HashMap()

        data["isLoggedIn"] = deviceModel.isLoggedIn
        data["userUsername"] = deviceModel.userUsername

        val result: ApiFuture<WriteResult> = docRef.set(data)
        return result.get().updateTime.toString()
    }

    override fun otpVerification(username: String, request: UserVerificationRequest): Boolean {
        val user = getByUsername(username)
        val result: Boolean
        if (user != null) {
            if (user.otp == request.otp) {
                result = true
                updateVerification(username, UserUpdateVerificationRequest(true))
            } else {
                result = false
            }
        } else {
            result = false
        }

        return result
    }

    private fun generateSixDigitOTP(): String {
        val otpLength = 6
        val otpBuilder = StringBuilder()

        repeat(otpLength) {
            val randomDigit = Random.nextInt(0, 10)
            otpBuilder.append(randomDigit)
        }

        return otpBuilder.toString()
    }

    private fun updateOtp(username: String) {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        docRef.update(USER_DATABASE_OTP, "null")
    }

    override fun getByUsername(username: String): User? {
        val database = FirestoreClient.getFirestore()
        val document: ApiFuture<DocumentSnapshot> = database.collection(DATABASE_COLLECTION).document(username).get()
        val documentSnapshot = document.get()

        return if (documentSnapshot.exists()) documentSnapshot.toUser() else null
    }

    override fun deleteByUsername(username: String): String {
        val database = FirestoreClient.getFirestore()
        val writeResult: ApiFuture<WriteResult> = database.collection(DATABASE_COLLECTION).document(username).delete()
        return writeResult.get().updateTime.toString()
    }

    override fun updateName(username: String, userUpdateNameRequest: UserUpdateNameRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_NAME, userUpdateNameRequest.newName)
        val result: WriteResult = future.get()
        return result.toString()
    }

    private fun updateVerification(
        username: String, userUpdateVerificationRequest: UserUpdateVerificationRequest
    ): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_VERIFICATION, userUpdateVerificationRequest.newVerification)
        updateOtp(username)
        val result: WriteResult = future.get()
        return result.toString()
    }

    override fun updateSurname(username: String, userUpdateNameRequest: UserUpdateSurnameRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_SURNAME, userUpdateNameRequest.newSurname)
        val result: WriteResult = future.get()
        return result.toString()
    }

    override fun updateEmail(username: String, userUpdateNameRequest: UserUpdateEmailRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_EMAIL, userUpdateNameRequest.newEmail)
        val result: WriteResult = future.get()
        return result.toString()
    }

    override fun updatePassword(username: String, userUpdateNameRequest: UserUpdatePasswordRequest): String {
        val database = FirestoreClient.getFirestore()
        val docRef: DocumentReference = database.collection(DATABASE_COLLECTION).document(username)
        val future = docRef.update(USER_DATABASE_PASSWORD, userUpdateNameRequest.newPassword)
        val result: WriteResult = future.get()
        return result.toString()
    }


}

