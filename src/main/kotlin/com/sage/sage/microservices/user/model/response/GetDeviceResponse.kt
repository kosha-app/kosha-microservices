package com.sage.sage.microservices.user.model.response

import com.fasterxml.jackson.annotation.JsonProperty


class GetDeviceResponse ( @JsonProperty("userId") val userId: String)
