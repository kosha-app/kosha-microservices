package com.sage.sage.microservices.featuretoggles

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "features")
class KoshaProdFeatureToggles {
    var toggleFeatureA: Boolean = false
    var toggleFeatureB: Boolean = false
}