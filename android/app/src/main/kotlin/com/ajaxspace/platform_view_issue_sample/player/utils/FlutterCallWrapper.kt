package com.ajaxspace.platform_view_issue_sample.player.utils

import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.CountDownLatch

class FlutterCallWrapper {
    private val handler = Handler(Looper.getMainLooper())

    fun <T> execute(methodChannel: MethodChannel, method: String, arguments: Any?): T? {
        var result: T? = null
        val countDownLatch = CountDownLatch(1)

        handler.post {
            methodChannel.invokeMethod(
                    method,
                    arguments,
                    CallResult(
                            countDownLatch = countDownLatch,
                            onSuccess = { result = it as? T }
                    )
            )
        }

        runCatching {
            countDownLatch.await()
        }

        return result
    }

    class CallResult(
            private val countDownLatch: CountDownLatch,
            private val onSuccess: (Any?) -> Unit,
            private val onError: ((String?, String?, Any?) -> Unit)? = null
    ) : MethodChannel.Result {
        override fun notImplemented() {
            countDownLatch.countDown()
        }

        override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
            onError?.invoke(errorCode, errorMessage, errorDetails)
            countDownLatch.countDown()
        }

        override fun success(result: Any?) {
            onSuccess.invoke(result)
            countDownLatch.countDown()
        }
    }
}