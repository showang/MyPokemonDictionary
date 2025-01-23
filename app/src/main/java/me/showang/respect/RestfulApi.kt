package me.showang.respect

import com.shopback.respect.core.ApiSpec
import com.shopback.respect.core.ContentType
import com.shopback.respect.core.RequestExecutor
import com.shopback.respect.core.error.ParseError
import com.shopback.respect.core.error.RequestError
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

abstract class RestfulApi<Result> : ApiSpec {

    override val contentType: String
        get() = ContentType.NONE
    override val headers: Map<String, String>
        get() = emptyMap()
    override val urlQueries: Map<String, String>
        get() = emptyMap()
    override val urlArrayQueries: Map<String, List<String>>
        get() = emptyMap()
    override val body: ByteArray
        get() = ByteArray(0)

    @Throws(Throwable::class, ParseError::class, RequestError::class)
    open suspend fun request(executor: RequestExecutor): Result = withContext(IO) {
        executor.submit(this@RestfulApi).let { inputStream ->
            runCatching {
                parse(inputStream.readBytes()).apply {
                    inputStream.close()
                }
            }.run {
                getOrNull() ?: throw ParseError(
                    runCatching { inputStream.close() }.exceptionOrNull()
                        ?: exceptionOrNull()
                )
            }
        }
    }

    @Throws(Throwable::class)
    protected abstract fun parse(bytes: ByteArray): Result
}
