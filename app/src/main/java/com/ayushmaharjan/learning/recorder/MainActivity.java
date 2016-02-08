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

    @Bind(R.id.start)
    Button start;

    @Bind(R.id.stop)
    Button stop;

    private ScreenRecorder recorder;

    private MediaProjectionManager projectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);

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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                Intent permissionIntent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_MEDIA_PROJECTION);
                break;
            case R.id.stop:
                if(recorder!=null) {
                    recorder.stopRecording();
                } else {
                    Timber.d("Not Recording");
                }
                break;
        }
    }
}
