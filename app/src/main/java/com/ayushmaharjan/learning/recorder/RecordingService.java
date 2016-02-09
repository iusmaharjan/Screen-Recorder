package com.ayushmaharjan.learning.recorder;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class RecordingService extends Service {

    private ScreenRecorder recorder;

    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_DATA = "extra_data";

    private static boolean running;

    public RecordingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(running) {
            Timber.d("Service already running");
            return START_NOT_STICKY;
        }

        Timber.d("Service started.");

        running = true;
        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
        Intent data = intent.getParcelableExtra(EXTRA_DATA);

        recorder = new ScreenRecorder(this, resultCode, data);
        recorder.startRecording();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        recorder.stopRecording();
        Timber.d("Service destroyed.");
        super.onDestroy();
    }
}
