package com.vunun.librestreaming;

import android.util.Log;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * Created by damly on 16/9/6.
 */
public class RNLrsViewManager  extends SimpleViewManager<RNLrsView> {
    public static final String REACT_CLASS = "RNLibReStreamingView";
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected RNLrsView createViewInstance(ThemedReactContext reactContext) {
        return new RNLrsView(reactContext);
    }

    @ReactProp(name="streamUrl")
    public void setStreamUrl(RNLrsView view, String streamUrl){
        Log.e(">>>>>>>>>>>>>>>>>", streamUrl);
        RNLrsPublisher.getInstance().setStreamUrl(streamUrl);
    }

    @ReactProp(name="streamKey")
    public void setStreamKey(RNLrsView view, String streamKey){
        Log.e(">>>>>>>>>>>>>>>>>", streamKey);
        RNLrsPublisher.getInstance().setStreamKey(streamKey);
    }

    @ReactProp(name="orientation")
    public void setOrientation(RNLrsView view, String orientation){
        Log.e(">>>>>>>>>>>>>>>>>", orientation);
        RNLrsPublisher.getInstance().setOrientation(orientation);
    }

    @ReactProp(name="camera")
    public void setCamera(RNLrsView view, String camera){
        Log.e(">>>>>>>>>>>>>>>>>", camera);
        RNLrsPublisher.getInstance().setCamera(camera);
    }

    @ReactProp(name="quality")
    public void setQuality(RNLrsView view, String quality){
        Log.e(">>>>>>>>>>>>>>>>>", quality);
        RNLrsPublisher.getInstance().setQuality(quality);
    }

    @ReactProp(name="color")
    public void setColor(RNLrsView view, ReadableMap colors){
        Log.e(">>>>>>>>>>>>>>>>>", "(" + colors.getInt("r") + "," + colors.getInt("g") +"," + colors.getInt("b") + "," + colors.getInt("a") + ")");
        RNLrsPublisher.getInstance().setColors(colors);
    }

    @ReactProp(name="zoom")
    public void setZoom(RNLrsView view, float zoom){
        Log.e(">>>>>>>>>>>>>>>>", "setting zoom to " + zoom);
        RNLrsPublisher.getInstance().setZoomByPercent((int)zoom);
    }

    @ReactProp(name="stabilize", defaultBoolean = true)
    public void setStable(RNLrsView view, boolean stable){
        RNLrsPublisher.getInstance().setStable(stable);
    }


}
