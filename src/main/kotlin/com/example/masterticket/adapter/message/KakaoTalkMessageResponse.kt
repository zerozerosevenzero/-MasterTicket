package com.example.masterticket.adapter.message

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoTalkMessageResponse(
    @JsonProperty("successful_receiver_uuids")
    var successfulReceiverUuids: List<String>? = null
)