package com.ajaxspace.platform_view_issue_sample.player.utils

private const val STREAM_KEY = "url"

class ArgumentsParser {

    fun getManifestUrl(arguments: Any?): String {
        return (arguments as? Map<*, *>)?.get(STREAM_KEY) as? String ?: ""
    }
}