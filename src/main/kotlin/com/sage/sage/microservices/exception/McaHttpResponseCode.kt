package com.sage.sage.microservices.exception

import org.springframework.http.HttpStatus


enum class McaHttpResponseCode(val httpStatus: HttpStatus, val mcaCode: String) {

    ERROR_UNAUTHORISED(HttpStatus.UNAUTHORIZED, "1001"),
    ERROR_ITEM_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "1002"),
    ERROR_IDENTITY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "1003"),
    ERROR_FEATURE_TOGGLED_OFF(HttpStatus.FORBIDDEN, "1004"),
    ERROR_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "1005"),
    ERROR_CODE_CATCH_ALL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "9999");

    override fun toString(): String {
        return "$name - $mcaCode"
    }
}
