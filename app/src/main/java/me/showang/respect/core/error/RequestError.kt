package com.shopback.respect.core.error

class RequestError(
    throwable: Throwable? = null,
    val responseCode: Int = -1,
    val bodyBytes: ByteArray? = null
) : Exception(throwable) {

    lateinit var rawBodyResponseString: String

    init {
        bodyBytes?.let { rawBodyResponseString = String(it) } ?: kotlin.run {
            rawBodyResponseString = ""
        }
    }

    override fun toString(): String {
        return "/******* RequestError *******/ (on ${Thread.currentThread()})\n" +
            "code:$responseCode message: ${cause?.message}\n" +
            "body: ${rawBodyResponseString ?: ""}\n" +
            "cause: $cause"
    }
}
