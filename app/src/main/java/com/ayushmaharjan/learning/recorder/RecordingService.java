package com.ayushmaharjan.learning.recorder;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class RecordingService extends Service {

    private ScreenRecorder recorder;

    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_DATA = "extra_data";

    private static final int NOTIFICATION_ID = 1001;

    private static boolean running;

    public RecordingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(running) {
            Timber.d("Service already running");
            return START_NOT_STICKY;
        }

        Context context = getApplicationContext();
        Notification notification = new Notification.Builder(context)
                .setPriority(Notification.PRIORITY_MIN)
                .build();


        startForeground(NOTIFICATION_ID, notification);

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
