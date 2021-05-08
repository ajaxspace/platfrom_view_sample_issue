package com.ajaxspace.platform_view_issue_sample.player.drm

import android.util.Base64
import androidx.annotation.WorkerThread
import com.ajaxspace.platform_view_issue_sample.player.utils.FlutterCallWrapper
import com.google.android.exoplayer2.drm.ExoMediaDrm
import com.google.android.exoplayer2.drm.MediaDrmCallback
import io.flutter.plugin.common.MethodChannel
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class DrmCallback(private val channel: MethodChannel) : MediaDrmCallback {

    private val callWrapper: FlutterCallWrapper = FlutterCallWrapper()

    override fun executeKeyRequest(uuid: UUID, request: ExoMediaDrm.KeyRequest): ByteArray {
        return callWrapper.execute<ByteArray>(channel, "executeLicenseRequest", Base64.encodeToString(request.data, Base64.NO_WRAP)) ?: ByteArray(0)
    }

    override fun executeProvisionRequest(uuid: UUID, request: ExoMediaDrm.ProvisionRequest): ByteArray {
        return WidevineDeviceProvision().getProvision(requestUrl = request.defaultUrl, requestData = request.data)
    }
}

class WidevineDeviceProvision {

    @WorkerThread
    fun getProvision(
            requestUrl: String?,
            requestData: ByteArray?
    ): ByteArray {
        if (!requestUrl.isNullOrEmpty() && requestData?.isNotEmpty() == true) {
            val url = URL("${requestUrl}&signedRequest=${String(requestData)}")

            val con: HttpsURLConnection = url.openConnection() as HttpsURLConnection
            con.requestMethod = "POST"
            con.doOutput = true

            con.outputStream.use { os ->
                os.write(byteArrayOf(), 0, 0)
            }

            return runCatching {
                con.responseCode
                con.inputStream.use {
                    return it.readBytes()
                }
            }.getOrElse {
                //Error handling send some error
                ByteArray(0)
            }
        }

        return ByteArray(0)
    }
}