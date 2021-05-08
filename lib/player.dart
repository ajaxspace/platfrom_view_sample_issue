import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/services.dart';

typedef VideoPlayerGetLicense = Future<Uint8List> Function(String drmSchemeId, Object? payload);

const _kChannel = 'PlayerChannel';

class AndroidPlayer {
  final VideoPlayerGetLicense _getLicense;
  late MethodChannel _channel;

  AndroidPlayer({
    required VideoPlayerGetLicense getLicense,
  }) : _getLicense = getLicense {
    _channel = const MethodChannel(_kChannel)..setMethodCallHandler(handleMethodCall);
  }

  // see: https://dashif.org/identifiers/content_protection/
  String get drmSchemeId => 'edef8ba9-79d6-4ace-a3c8-27dcd51d21ed';

  Future<dynamic> handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'executeLicenseRequest':
        return executeLicenseRequest(call.arguments);
    }
  }

  void load(String url) {
    _channel.invokeMethod('load', {'url': url});
  }

  void stop() {
    _channel.invokeMethod('stop');
  }

  Future<Object> executeLicenseRequest(dynamic arguments) {
    final decodedArguments = base64Decode(arguments);
    return _getLicense(drmSchemeId, decodedArguments);
  }
}
