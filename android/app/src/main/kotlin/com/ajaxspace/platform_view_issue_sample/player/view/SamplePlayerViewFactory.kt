package com.ajaxspace.platform_view_issue_sample.player.view

import android.content.Context
import io.flutter.plugin.common.JSONMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class SamplePlayerViewFactory(private val player: SamplePlayer) : PlatformViewFactory(JSONMessageCodec.INSTANCE) {
    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        return SamplePlayerView(
                context = context!!,
                player = player
        )
    }
}