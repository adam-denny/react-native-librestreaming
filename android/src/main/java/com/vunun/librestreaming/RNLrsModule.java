package com.vunun.librestreaming;

import com.facebook.react.bridge.*;

public class RNLrsModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext mReactContext;
    public RNLrsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
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
            successCallback.invoke("started");
        } else {
            errorCallback.invoke("error");
        }
        //RNLrsPublisher.getInstance().setWhiteningFilter();
    }

    @ReactMethod
    public void stopPublish() {
        RNLrsPublisher.getInstance().stopStreaming();
    }

    @ReactMethod
    public void clearFilters() { RNLrsPublisher.getInstance().releaseFilters(); }


}