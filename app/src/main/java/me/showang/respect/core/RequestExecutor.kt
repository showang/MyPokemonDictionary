package com.shopback.respect.core

import com.shopback.respect.core.error.RequestError
import java.io.InputStream

interface RequestExecutor {

    @Throws(RequestError::class)
    suspend fun submit(api: ApiSpec): InputStream

    fun cancel(api: ApiSpec)
    fun cancelAll()
}
