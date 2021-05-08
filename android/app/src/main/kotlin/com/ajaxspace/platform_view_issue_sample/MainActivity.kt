package com.ajaxspace.platform_view_issue_sample

import com.ajaxspace.platform_view_issue_sample.player.PlayerPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        flutterEngine.plugins.add(PlayerPlugin())
    }
}