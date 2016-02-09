package com.ayushmaharjan.learning.recorder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class RecordingService extends Service {
    public RecordingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("Service started.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Timber.d("Service destroyed.");
        super.onDestroy();
    }
}
