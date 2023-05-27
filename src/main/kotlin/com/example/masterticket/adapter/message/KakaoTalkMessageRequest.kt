package com.example.masterticket.adapter.message

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoTalkMessageRequest(

    @JsonProperty("template_object")
    var templateObject: TemplateObject,

    @JsonProperty("receiver_uuids")
    var receiverUuids: List<String>
) {
    data class TemplateObject(
        @JsonProperty("object_type")
        var objectType: String,
        var text: String,
        var link: Link
    ) {
        data class Link(
            @JsonProperty("web_url")
            var webUrl: String? = null
        )
    }

    constructor(uuid: String, text: String) : this(
        TemplateObject("text", text, TemplateObject.Link()),
        listOf(uuid)
    )
}