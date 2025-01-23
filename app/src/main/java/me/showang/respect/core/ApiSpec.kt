package com.shopback.respect.core

interface ApiSpec {
    val url: String
    val httpMethod: HttpMethod
    val contentType: String

    val headers: Map<String, String>
    val urlQueries: Map<String, String>
    val urlArrayQueries: Map<String, List<String>>

    val body: ByteArray

    val priority: Priority get() = Priority.NORMAL
    val timeout: Long? get() = null
}
