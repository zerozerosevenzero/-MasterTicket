package com.example.masterticket.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "kakaotalk")
data class KakaoTalkMessageConfig(
    var host: String = "",
    var token: String = ""
)
