package com.vunun.librestreaming;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
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

import static java.security.AccessController.getContext;

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
    public int mWidth;
    public int mHeight;
    public SurfaceTexture mSurface;
    private boolean readyToPreview = false;
    private boolean hasStarted = false;
    private boolean stopped = false;

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
        Log.d("VIDEO", orientation);
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
        try {
            int frontDirection, backDirection;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
            frontDirection = cameraInfo.orientation;
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, cameraInfo);
            backDirection = cameraInfo.orientation;
            Log.d("RNLrsPublisher", "front" + frontDirection + " back" + backDirection);

            if (orientation.equalsIgnoreCase("PORTRAIT")) {
                Log.d("RNLrsPublisher", "Portrait Mode");
                resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
                resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_90 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_270));
            } else if (orientation.equalsIgnoreCase("LANDSCAPE-LEFT")) {
                Log.d("RNLrsPublisher", "Landscape left");
                resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
                resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0));
            } else {
                Log.d("RNLrsPublisher", "Landscape Right");
                resConfig.setBackCameraDirectionMode((backDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_0));
                resConfig.setFrontCameraDirectionMode((frontDirection == 90 ? RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180 : RESConfig.DirectionMode.FLAG_DIRECTION_ROATATION_180));
            }

            readyToPreview = true;
            Log.d("VIDEO", "Ready to Preview");
        } catch(Exception e){
            e.printStackTrace();
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

    public void end(){
        Log.d("VIDEO", "Stopping Stream");
        if(hasStarted && !stopped){
            resClient.stopStreaming();
        }

        if (resClient == null) {
            return;
        }
        resClient.stopPreview();
        resClient.destroy();
        resClient = null;
        ready = false;
        hasStarted = false;
        stopped = false;
        readyToPreview = false;
    }

    public void stopStreaming() {
        Log.d("VIDEO", "Stopping Stream");
        if (resClient == null) {
            return;
        }
        resClient.stopStreaming();
        stopped = true;
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

    public void refreshPreview(){
        setCameraDirection();
        updatePreview(mWidth, mHeight);
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
        try {
            Log.d("VIDEO", "Starting Stream");
            if(!ready){
                setupConnection();
            }
            stopped = false;
            hasStarted = true;
            resClient.startStreaming();
            return true;
        } catch (Exception e){
            Log.d("VIDEO", "Error loading: ", e);
            return false;
        }

    }


    public void startPreview(SurfaceTexture surface, int width, int height){
        mSurface = surface;
        mWidth = width;
        mHeight = height;
        Log.d("VIDEO", "Trying Preview");
        if(!ready){
            setupConnection();
        }
        if(readyToPreview && resClient.prepare(resConfig)) {
            Log.d("VIDEO", "Starting Preview");
            Log.d("VIDEO", "PUblisher thinks the preview should be " + resConfig.getFrontCameraDirectionMode());

            resClient.startPreview(surface, width, height);
        } else {
            Log.d("VIDEO", "Preview not ready");
            new CountDownTimer(100,100) {
                public void onTick(long millisecondsUntilFinished){

                }

                public void onFinish() {
                    Log.d("VIDEO", "Not Ready yet, retrying");
                    startPreview(mSurface, mWidth, mHeight);
                }
            }.start();
            return;
        }

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

    public void setFocusArea(int x, int y, int w, int h){

        float widthPerc = 0 / w;
        float heightPerc = 0 / h;

        float xPerc = x * widthPerc;
        float yPerc = y * heightPerc;

        int xInterp = ((int)xPerc * 2000) - 1000;
        int yInterp = ((int)yPerc * 2000) - 1000;

        Rect rect = new Rect(xInterp, yInterp, xInterp + 10, yInterp + 10);
        resClient.setFocusArea(rect);
    }

    public void setStable(boolean stable){
        if(resClient != null){
            resClient.setStable(stable);
        }
    }
}
