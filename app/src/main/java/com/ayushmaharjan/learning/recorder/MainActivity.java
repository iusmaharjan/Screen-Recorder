package com.ayushmaharjan.learning.recorder;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private static final int REQUEST_MEDIA_PROJECTION_SERVICE = 2;


    @Bind(R.id.start)
    Button start;

    @Bind(R.id.stop)
    Button stop;

    @Bind(R.id.start_service)
    Button startService;

    @Bind(R.id.stop_service)
    Button stopService;

    private ScreenRecorder recorder;

    private MediaProjectionManager projectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);

        projectionManager = (MediaProjectionManager) getSystemService(
                android.content.Context.MEDIA_PROJECTION_SERVICE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_MEDIA_PROJECTION == requestCode) {
            if(resultCode == RESULT_OK) {
                recorder = new ScreenRecorder(this, resultCode, data);
                recorder.startRecording();
            } else {
                Timber.d("Permission denied");
            }
        } else if (REQUEST_MEDIA_PROJECTION_SERVICE == requestCode) {
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent(this, RecordingService.class);
                intent.putExtra(RecordingService.EXTRA_DATA, data);
                intent.putExtra(RecordingService.EXTRA_RESULT_CODE, resultCode);
                startService(intent);
            } else {
                Timber.d("Permission denied");
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.start:
                Intent permissionIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_MEDIA_PROJECTION);
                break;
            case R.id.stop:
                if (recorder != null) {
                    recorder.stopRecording();
                } else {
                    Timber.d("Not Recording");
                }
                break;
            case R.id.start_service:
                intent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(intent, REQUEST_MEDIA_PROJECTION_SERVICE);
                break;
            case R.id.stop_service:
                intent = new Intent(this, RecordingService.class);
                stopService(intent);
                break;
        }
    }
}
