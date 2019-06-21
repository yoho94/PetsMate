package com.example.petsmate;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;



public class mypage02 extends BaseActivity {
    BottomNavigationView bottomNavigationView;
    private Spinner spinner2;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage02);
        seekFunction();
    }

    public void seekFunction() {
        SeekBar sb = (SeekBar) findViewById(R.id.sb);
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolumn = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);

        sb.setMax(nMax);
        sb.setProgress(nCurrentVolumn);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });

        {

            arrayList = new ArrayList<>();
            arrayList.add("알람음1");
            arrayList.add("알람음2");
            arrayList.add("알람음3");
            arrayList.add("알람음4");
            arrayList.add("알람음5");
            arrayList.add("알람음6");


            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayList);

            spinner2 = (Spinner) findViewById(R.id.alarmMelodyspinner);
            spinner2.setAdapter(arrayAdapter);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), arrayList.get(i) + "가 선택되었습니다.",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
            configBottomNavigation(this, bottomNavigationView);
        }
    }
}
