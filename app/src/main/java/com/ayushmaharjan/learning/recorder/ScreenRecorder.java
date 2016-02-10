package com.ayushmaharjan.learning.recorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;


public class ScreenRecorder {

    private final int resultCode;
    private final Intent data;

    private MediaProjectionManager mMediaProjectionManager;

    private MediaProjection projection;

    private MediaRecorder mMediaRecorder;

    private VirtualDisplay mVirtualDisplay;

    private File screenRecordingDir;

    private boolean recording;

    private int screenWidth;
    private int screenHeight;
    private int pixelDensity;

    private final DateFormat fileFormat =
            new SimpleDateFormat("'Recording_'yyyy-MM-dd-HH-mm-ss'.mp4'", Locale.US);

    ScreenRecorder(Context context, int resultCode, Intent data) {

        Timber.d("ScreenRecorder created");

        this.resultCode = resultCode;
        this.data = data;

        //Specifying the output file as "ScreenRecordings" inside "Movies" dir
        File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        screenRecordingDir = new File(moviesDir, "ScreenRecordings");

        mMediaProjectionManager = (MediaProjectionManager)context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        pixelDensity = metrics.densityDpi;
    }

    public void startRecording() {

        if(!screenRecordingDir.mkdirs()) {
            Timber.d("Failed to created directory: %s", screenRecordingDir.getAbsolutePath());
        }

        mMediaRecorder = new MediaRecorder();

        initRecorder();

        prepareRecorder();

        //TODO: Learn about resultCode and data
        projection = mMediaProjectionManager.getMediaProjection(resultCode, data);

        setUpVirtualDisplay();

        mMediaRecorder.start();
        recording = true;
    }

    public void stopRecording() {
        if(!recording) {
            Timber.d("Not Recording");
            return;
        }
        recording = false;

        mMediaRecorder.stop();
        Timber.d("Media Recorder stopped.");

        projection.stop();
        Timber.d("Media Projection stopped.");

        // Reset to solve issue: W/MediaRecorder: mediarecorder went away with unhandled events
        mMediaRecorder.reset();
        Timber.d("Media Recorder reset.");

        mMediaRecorder.release();
        Timber.d("Media Recorder released.");

        mVirtualDisplay.release();
        Timber.d("Virtual Display released.");

    }

    private void initRecorder() {

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(8000 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(screenWidth, screenHeight);

        String outputFileName = fileFormat.format(new Date());
        File outputFile = new File(screenRecordingDir, outputFileName);
        mMediaRecorder.setOutputFile(outputFile.getAbsolutePath());
    }

    private void prepareRecorder() {
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e(e.toString());
        }
    }

    private void setUpVirtualDisplay() {
        //TODO: Learn about virtual display flags
        mVirtualDisplay = projection.createVirtualDisplay("ScreenRecording",
                screenWidth, screenHeight, pixelDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
                mMediaRecorder.getSurface(), null, null);
    }
}
