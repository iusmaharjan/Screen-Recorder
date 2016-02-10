package com.ayushmaharjan.learning.recorder;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        if (REQUEST_MEDIA_PROJECTION == requestCode) {
            if(resultCode == RESULT_OK) {
                Timber.d("Permission granted");
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
                Timber.d("Requesting permission to record the screen");
                intent = projectionManager.createScreenCaptureIntent();
                startActivityForResult(intent, REQUEST_MEDIA_PROJECTION);
                break;
            case R.id.stop:
                intent = new Intent(this, RecordingService.class);
                if(stopService(intent)) {
                    Timber.d("Recording Service stopped");
                } else {
                    Timber.d("Recording Service not running");
                }
                break;
        }
    }
}
