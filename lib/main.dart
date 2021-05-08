import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'package:platform_view_issue_sample/player.dart';

const _kPlayerViewType = 'Player';

const String dashManifestUrl =
    'https://storage.googleapis.com/shaka-demo-assets/angel-one-widevine/dash.mpd';

const dashLicenseServerUrl = 'https://cwip-shaka-proxy.appspot.com/no_auth';

late AndroidPlayer videoPlayer;

String transformUrl(String url) => url;

Future<Uint8List> getLicense(String id, Object? payload) {
  return http
      .post(
        Uri.parse(dashLicenseServerUrl),
        body: payload,
      )
      .then((response) => response.bodyBytes);
}

void main() {
  runApp(MaterialApp(
    title: 'Platform View Android Tv issue sample',
    // Start the app with the "/" named route. In this case, the app starts
    // on the FirstScreen widget.
    initialRoute: '/',
    routes: {
      // When navigating to the "/" route, build the FirstScreen widget.
      '/': (context) => PlayerPage(),
      // When navigating to the "/second" route, build the SecondScreen widget.
      '/second': (context) => SecondScreen(),
    },
  ));

  videoPlayer = AndroidPlayer(
    getLicense: getLicense,
  );
}

class PlayerPage extends StatefulWidget {
  PlayerPage({Key? key}) : super(key: key);

  @override
  _PlayerPageState createState() => _PlayerPageState();
}

class _PlayerPageState extends State<PlayerPage> {
  @override
  Widget build(BuildContext context) {
    return RawKeyboardListener(
      focusNode: FocusNode(),
      child: GestureDetector(
        behavior: HitTestBehavior.translucent,
        onTap: () => Navigator.pushNamed(context, '/second'),
        child: _PlatformPlayer(),
      ),
      onKey: (event) {
        print(event);
        if (event.logicalKey == LogicalKeyboardKey.select) {
          Navigator.pushNamed(context, '/second');
        }
      },
    );
  }

  @override
  void initState() {
    super.initState();

    videoPlayer.load(dashManifestUrl);
  }

  @override
  void deactivate() {
    videoPlayer.stop();

    super.deactivate();
  }
}

class SecondScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blue,
      body: SizedBox(),
    );
  }
}

class _PlatformPlayer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return PlatformViewLink(
      viewType: _kPlayerViewType,
      surfaceFactory: (context, controller) {
        return AndroidViewSurface(
          controller: controller as AndroidViewController,
          gestureRecognizers: const {},
          hitTestBehavior: PlatformViewHitTestBehavior.transparent,
        );
      },
      onCreatePlatformView: (params) {
        return PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: params.viewType,
          layoutDirection: TextDirection.ltr,
        )
          ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
          ..create();
      },
    );
  }
}
