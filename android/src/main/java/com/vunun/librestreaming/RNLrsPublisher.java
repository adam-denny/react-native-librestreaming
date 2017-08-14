package com.vunun.librestreaming;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.content.res.Configuration;

import com.facebook.react.bridge.ReadableMap;

import java.util.concurrent.TimeUnit;

import me.lake.librestreaming.client.RESClient;
import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.softaudiofilter.BaseSoftAudioFilter;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.model.Size;
import me.lake.librestreaming.sample.audiofilter.SetVolumeAudioFilter;
import me.lake.librestreaming.sample.hardfilter.ColorMixHardFilter;
import me.lake.librestreaming.sample.hardfilter.FishEyeFilterHard;
import me.lake.librestreaming.sample.hardfilter.SeaScapeFilter;
import me.lake.librestreaming.sample.hardfilter.SkinBlurHardVideoFilter;
import me.lake.librestreaming.sample.hardfilter.SobelEdgeDetectionHardVideoFilter;
import me.lake.librestreaming.sample.hardfilter.WhiteningHardVideoFilter;

/**
 * Created by damly on 16/9/7.
 */
public class RNLrsPublisher {

    private static final RNLrsPublisher ourInstance = new RNLrsPublisher();

    private RESClient resClient = null;
    private RESConfig resConfig = RESConfig.obtain();
    private String streamUrl = "";
    private String streamKey = "";
    private boolean ready = false;

    private String camera = "front";
    private String quality = "d1";
    private String orientation = "landscape";

    public static RNLrsPublisher getInstance() {
        return ourInstance;
    }

    private RNLrsPublisher() {
        resConfig.setFilterMode(RESConfig.FilterMode.HARD);
        resConfig.setRenderingMode(RESConfig.RenderingMode.OpenGLES);
    }

    public void setDefault() {
        streamUrl = "";
        streamKey = "";
        camera = "front";
        quality = "d1";
        orientation = "landscape";
        ready = false;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setOrientation(String orientation) {
        Log.d("RNLrsPublisher", orientation);
        //this.orientation = orientation;
        //setCameraDirection();
    }

    public void setCameraPositionBack() {
        resConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    public void setCameraPositionFront() {
        resConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public void setInitialOrientation(int direction){
        if(direction == 0){
            this.orientation = "PORTRAIT";
        }
        if(direction == 1){
            this.orientation = "LANDSCAPE-LEFT";
        }
        if(direction == 3){
            this.orientation = "LANDSCAPE-RIGHT";
        }
        setCameraDirection();
    }

    public void setCameraDirection() {
        int frontDirection, backDirection;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
        frontDirection = cameraInfo.orientation;
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
        backDirection = cameraInfo.orientation;

        if (orientation.equalsIgnoreCase("PORTRAIT")) {
            resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
            resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
        } else if(orientation.equalsIgnoreCase("LANDSCAPE-LEFT")) {
            resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
            resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0));
        } else {
            resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0));
            resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
        }

    }


    public void setTargetVideoSize(int width, int height) {
        resConfig.setTargetVideoSize(new Size(width, height));
    }

    public void setBitRate(int bitRate) {
        resConfig.setBitRate(bitRate);
    }

    public void setVideoFPS(int fps) {
        resConfig.setVideoFPS(fps);
    }

    public void setVolume(int step) {

        if (resClient == null) {
            return;
        }

        BaseSoftAudioFilter audioFilter = resClient.acquireSoftAudioFilter();
        if (audioFilter != null) {
            if (audioFilter instanceof SetVolumeAudioFilter) {
                SetVolumeAudioFilter blackWhiteFilter = (SetVolumeAudioFilter) audioFilter;
                blackWhiteFilter.setVolumeScale((float) (step / 10.0));
            }
        }
        resClient.releaseSoftAudioFilter();
    }


    public void stopStreaming() {
        Log.d("RNLrsPublisher", "Stopping Stream");
        if (resClient == null) {
            return;
        }
        resClient.stopPreview();
        resClient.stopStreaming();
        resClient.destroy();
        resClient = null;
        ready = false;
        //setDefault();
    }

    private boolean prepare() {

        if (resClient == null || !resClient.prepare(resConfig)) {
            return false;
        }

        resClient.setSoftAudioFilter(new SetVolumeAudioFilter());
        resClient.setConnectionListener(new RESConnectionListener() {
            @Override
            public void onOpenConnectionResult(int i) {
            }

            @Override
            public void onWriteError(int errno) {
                if (errno == 9) {
                    resClient.stopStreaming();
                    resClient.startStreaming();
                }
            }

            @Override
            public void onCloseConnectionResult(int i) {
            }
        });

        return true;
    }

    public void setupConnection(){
        resClient = new RESClient();

        if (streamUrl.isEmpty() || streamKey.isEmpty())
            return;


        if (camera.equalsIgnoreCase("back")) {
            setCameraPositionBack();
        } else {
            setCameraPositionFront();
        }

        setCameraDirection();

        if (quality.equalsIgnoreCase("D1")) {
            setBitRate(800 * 1000);
            setTargetVideoSize(960, 16 * 34);
        } else if (quality.equalsIgnoreCase("720P")) {
            setBitRate(1500 * 1000);
            setTargetVideoSize(1280, 16 * 45);
        } else {
            setBitRate(600 * 1000);
            setTargetVideoSize(680, 16 * 23);
        }

        setVideoFPS(25);

        resConfig.setRtmpAddr(streamUrl + '/' + streamKey);

        if (!prepare()) {
            resClient = null;
            return;
        }

        ready = true;
    }

    public boolean startStreaming() {
        Log.d("RNLrsPublisher", "Starting Stream");
        if(!ready){
            setupConnection();
        }
        resClient.startStreaming();
        return true;
    }


    public void startPreview(SurfaceTexture surface, int width, int height){
        Log.d("RNLrsPublisher", "Starting Preview");
//        if (resClient == null) {
//            Log.d("RNLrsPublisher", "resClient == null so NO PREVIEW");

//            return;
//        }
        if(!ready){
            setupConnection();
        }
        resClient.startPreview(surface, width, height);
    }

    public void updatePreview(int width, int height) {
        if (resClient == null) {
            return;
        }
        resClient.updatePreview(width, height);
    }

    public void swapCamera() {
        if (resClient == null) {
            return;
        }
        resClient.swapCamera();
    }

    public void setSkinBlurFilter(int stepScale) {
        if (resClient == null) {
            return;
        }
        resClient.setHardVideoFilter(new SkinBlurHardVideoFilter(stepScale));
    }

    public void setWhiteningFilter() {
        if (resClient == null) {
            return;
        }
        resClient.setHardVideoFilter(new WhiteningHardVideoFilter());
    }

    public void setFishEye(){
        if (resClient == null) {
            return;
        }
        resClient.setHardVideoFilter(new FishEyeFilterHard());
    }

    public void setSeaScape(){
        if (resClient == null) {
            return;
        }
        resClient.setHardVideoFilter(new SeaScapeFilter());
    }

    public void setEdgeDetection(){
        if (resClient == null) {
            return;
        }
        resClient.setHardVideoFilter(new SobelEdgeDetectionHardVideoFilter());
    }

    public void setZoomByPercent(int percent) {
        if (resClient == null) {
            return;
        }
        resClient.setZoomByPercent(percent / 100.0f);
    }

    public void releaseFilters(){
        if(resClient == null){
            return;
        }
        resClient.releaseHardVideoFilter();
    }

    public void setColors(ReadableMap colors){
        if(resClient == null){
            return;
        }

        float red = (float) 1 / (256 / colors.getInt("r"));
        float green = (float) 1 / (256 / colors.getInt("g"));
        float blue = (float) 1 / (256 / colors.getInt("b"));
        float alpha = (float) 1 / (100 / colors.getInt("a"));

        resClient.setHardVideoFilter(new ColorMixHardFilter(red, green, blue, alpha));
    }
}
