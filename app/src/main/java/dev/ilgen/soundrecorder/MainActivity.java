package dev.ilgen.soundrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView maxAmplitudeText;
    private final Handler handler = new Handler();

    private AudioRecord record;
    private int minSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maxAmplitudeText = findViewById(R.id.max_amplitude);

        if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initializeSoundRecorder();
        } else {
            this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSoundRecorder();
        } else {
            finish();
        }
    }

    private void initializeSoundRecorder() {
        //minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        minSize = 8000;
        record = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
        record.startRecording();

        handler.postDelayed(new TimeSlotRecordHandler(), 1000);

    }

    public final class TimeSlotRecordHandler implements Runnable {
        final short[] buffer = new short[minSize];

        @Override
        public void run() {
            record.read(buffer, 0, minSize);

            short maxAmplitude = 0;
            for (short value : buffer) {
                if (abs(value) > maxAmplitude) {
                    maxAmplitude = abs(value);
                    maxAmplitudeText.setText(getString(R.string.max_amplitude, maxAmplitude));
                }
            }

            handler.postDelayed(this, 1000);
        }

        private short abs(short value) {
            return (short) Math.abs((long) value);
        }
    }

}
