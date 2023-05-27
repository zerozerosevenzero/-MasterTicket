package com.example.masterticket.adapter.message

import com.example.masterticket.config.KakaoTalkMessageConfig
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@org.springframework.stereotype.Service
class KakaoTalkMessageAdapter(config: KakaoTalkMessageConfig){
    private  val  webClient: WebClient
    init {
        webClient = WebClient.builder()
            .baseUrl(config.host)
            .defaultHeaders { h ->
                h.setBearerAuth(config.token)
                h.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
            }.build()
    }
    fun sendKakaoTalkMessage(uuid: String, text: String): Boolean{
        val response: KakaoTalkMessageResponse? = webClient.post().uri("/v1/api/talk/friends/message/default/send")
            .body(BodyInserters.fromValue(KakaoTalkMessageRequest(uuid, text)))
            .retrieve()
            .bodyToMono(KakaoTalkMessageResponse::class.java)
            .block()
        if (response?.successfulReceiverUuids == null){
            return false
        }
        return response.successfulReceiverUuids!!.size > 0
    }
}