package com.ajaxspace.platform_view_issue_sample.player

import androidx.annotation.NonNull
import com.ajaxspace.platform_view_issue_sample.player.utils.ArgumentsParser
import com.ajaxspace.platform_view_issue_sample.player.view.SamplePlayer
import com.ajaxspace.platform_view_issue_sample.player.view.SamplePlayerViewFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private const val CHANNEL_NAME = "PlayerChannel"
private const val PLAYER_VIEW_TYPE = "Player"

class PlayerPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {

    private lateinit var player: SamplePlayer
    private lateinit var channel: MethodChannel

    private val argumentsParser: ArgumentsParser = ArgumentsParser()

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, CHANNEL_NAME)
        channel.setMethodCallHandler(this)

        val context = binding.applicationContext

        player = SamplePlayer(
                context = context,
                methodChannel = channel
        )

        binding.platformViewRegistry.registerViewFactory(PLAYER_VIEW_TYPE, SamplePlayerViewFactory(player = player))
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "stop" -> {
                player.stopPlayer()
            }
            "load" -> {
                player.load(getManifestUrl(call.arguments))
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    private fun getManifestUrl(arguments: Any?): String {
        return argumentsParser.getManifestUrl(arguments)
    }
}