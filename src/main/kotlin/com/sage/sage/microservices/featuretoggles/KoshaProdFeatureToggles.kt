package com.sage.sage.microservices.featuretoggles

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "features")
class KoshaProdFeatureToggles {
    var otpBypass: Boolean = true
}