package com.vunun.librestreaming;

import android.util.Log;

import com.facebook.react.bridge.*;
import com.vunun.librestreaming.RCTSensorOrientationChecker;

public class RNLrsModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext mReactContext;
    private RCTSensorOrientationChecker _sensorOrientationChecker;
    public RNLrsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        _sensorOrientationChecker = new RCTSensorOrientationChecker(mReactContext);
        Log.d("RNLrsModule", "Hello, I'm awake");
        init();
    }

    @Override
    public String getName() {
        return "RNLibReStreamingModule";
    }

    @ReactMethod
    public void configureSetZoomByPercent(int percent) {
        RNLrsPublisher.getInstance().setZoomByPercent(percent);
    }

    @ReactMethod
    public void init(){
        _sensorOrientationChecker.onResume();
        _sensorOrientationChecker.registerOrientationListener(new RCTSensorOrientationListener() {
            @Override
            public void orientationEvent() {
                int deviceOrientation = _sensorOrientationChecker.getOrientation();
                _sensorOrientationChecker.unregisterOrientationListener();
                _sensorOrientationChecker.onPause();
                Log.d("RNLrsModule", deviceOrientation + "deg");
                RNLrsPublisher.getInstance().setInitialOrientation(deviceOrientation);
            }
        });
    }

    @ReactMethod
    public void swapCamera() {
        RNLrsPublisher.getInstance().swapCamera();
    }

    @ReactMethod
    public void setSkinBlur(int stepScale) {
        RNLrsPublisher.getInstance().setSkinBlurFilter(stepScale);
    }

    @ReactMethod
    public void setVolume(int step) {
        RNLrsPublisher.getInstance().setVolume(step);
    }

    @ReactMethod
    public void setWhitening() {
        RNLrsPublisher.getInstance().setWhiteningFilter();
    }

    @ReactMethod
    public void setFishEye() {
        RNLrsPublisher.getInstance().setFishEye();
    }

    @ReactMethod
    public void setEdgeDetection() {
        RNLrsPublisher.getInstance().setEdgeDetection();
    }

    @ReactMethod
    public void setSeaScape(){
        RNLrsPublisher.getInstance().setSeaScape();
    }

    @ReactMethod
    public void startPublish(Callback errorCallback, Callback successCallback) {
        if(RNLrsPublisher.getInstance().startStreaming()){
            successCallback.invoke("Started");
        } else {
            errorCallback.invoke("Failed");
        };
    }

    @ReactMethod
    public void refreshPreview(){
        RNLrsPublisher.getInstance().refreshPreview();
    }

    @ReactMethod
    public void stopPublish() {
        RNLrsPublisher.getInstance().stopStreaming();
    }

    @ReactMethod
    public void clearFilters() { RNLrsPublisher.getInstance().releaseFilters(); }

    @ReactMethod
    public void end() {RNLrsPublisher.getInstance().end();}
}