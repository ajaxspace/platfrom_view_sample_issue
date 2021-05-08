package com.ajaxspace.platform_view_issue_sample.player.view

import android.content.Context
import com.ajaxspace.platform_view_issue_sample.player.drm.DrmCallback
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import io.flutter.plugin.common.MethodChannel

class SamplePlayer(
        private val context: Context,
        private val methodChannel: MethodChannel
) {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    private fun initPlayer() {
        stopPlayer()
        val loadControl = getLoadControl()
        val localTrackSelector = getTrackSelector()

        runCatching {
            player = SimpleExoPlayer.Builder(
                    context,
                    DefaultRenderersFactory(context)
            ).apply {
                setTrackSelector(localTrackSelector)
                setLoadControl(loadControl)
            }.build()

            player?.apply {
                playerView?.let {
                    it.player = player
                }
            }
        }
    }

    fun setPlayerView(playerView: PlayerView) {
        this.playerView = playerView

        player?.let {
            playerView.player = it
        }
    }

    fun load(url: String) {
        runCatching {
            initPlayer()
            getMediaSource(url).let {
                player?.apply {
                    setMediaSource(it, false)
                    prepare()
                    playWhenReady = true
                }
            }
        }
    }

    fun stopPlayer() {
        player?.apply {
            stop()
            clearMediaItems()
            release()
        }

        player = null
    }


    private fun getTrackSelector(): DefaultTrackSelector {
        return DefaultTrackSelector(context, AdaptiveTrackSelection.Factory()).apply {
            buildUponParameters()
                    .build()
        }
    }

    private fun getLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
                .build()
    }

    private fun getDrmSessionManager(): DefaultDrmSessionManager {
        return DefaultDrmSessionManager.Builder().apply {
            setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            setMultiSession(false)
            setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy())
        }.build(DrmCallback(methodChannel))
    }

    private fun getMediaSource(url: String): MediaSource {
        val dataSource = DefaultHttpDataSource.Factory()
        val drmSessionManager = getDrmSessionManager()

        return DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(
                        dataSource
                ), dataSource)
                .setDrmSessionManagerProvider { drmSessionManager }
                .createMediaSource(MediaItem.fromUri(url))
    }


}