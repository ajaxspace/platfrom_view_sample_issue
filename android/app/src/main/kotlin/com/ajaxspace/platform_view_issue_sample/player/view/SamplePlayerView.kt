package com.ajaxspace.platform_view_issue_sample.player.view

import android.content.Context
import android.view.View
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import io.flutter.plugin.platform.PlatformView

class SamplePlayerView(context: Context, player: SamplePlayer) : PlatformView {

    private var layout: View

    init {
        val playerView = PlayerView(context)
        playerView.useController = false
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        playerView.setShowShuffleButton(false)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        layout = playerView
        player.setPlayerView(playerView)
    }

    override fun getView(): View {
        return layout
    }

    override fun dispose() {
    }
}