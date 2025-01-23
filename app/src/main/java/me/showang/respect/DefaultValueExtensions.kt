package me.showang.respect

import com.shopback.respect.core.RequestExecutor
import com.shopback.respect.okhttp.OkhttpRequestExecutor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient

private val defaultExecutor: RequestExecutor by lazy {
    OkhttpRequestExecutor(OkHttpClient())
}

object Respect {
    var specifiedExecutor: RequestExecutor? = null
}

fun <Result> RestfulApi<Result>.flow(
    requestExecutor: RequestExecutor = Respect.specifiedExecutor
        ?: defaultExecutor
): Flow<Result> =
    kotlinx.coroutines.flow.flow {
        emit(request(requestExecutor))
    }

suspend fun <Result> RestfulApi<Result>.request(): Result = request(
    Respect.specifiedExecutor ?: defaultExecutor
)

suspend fun <Result> RestfulApi<Result>.request(errorDelegate: (Throwable) -> Unit): Result? = runCatching {
    request()
}.run {
    getOrNull() ?: exceptionOrNull()?.let { e ->
        e.takeIf { it !is CancellationException }?.let(errorDelegate)
        null
    }
}
