package com.example.mvisualizer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.example.mvisualizer.AudioVisuals.AudioInputReader;
import com.example.mvisualizer.AudioVisuals.VisualizerView;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVisualizerView = findViewById(R.id.activity_visualizer);

        setupSharePreferences();
        setupPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.visualizer_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings :
                Intent startSettingsActivityIntent=new Intent(this, Settings.class);
                startActivity(startSettingsActivityIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSharePreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showOrHideBass =sharedPreferences.getBoolean(getString(R.string.bass_key),
                getResources().getBoolean(R.bool.check_box_bass_default_value));

        boolean showOrHideSquare =sharedPreferences.getBoolean(getString(R.string.square_key),
                getResources().getBoolean(R.bool.check_box_square_default_value));

        boolean showOrHideTriangle =sharedPreferences.getBoolean(getString(R.string.triangle_key),
                getResources().getBoolean(R.bool.check_box_triangle_default_value));
        String defineColor=sharedPreferences.getString(getString(R.string.colors_option_key), getString(R.string.value_label_red_option));
        String size= sharedPreferences.getString(getString(R.string.size_key), getString(R.string.size_default));


        mVisualizerView.setShowBass(showOrHideBass);
        mVisualizerView.setShowMid(showOrHideSquare);
        mVisualizerView.setShowTreble(showOrHideTriangle);
        mVisualizerView.setMinSizeScale(Float.parseFloat(size));
        mVisualizerView.setColor(defineColor);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.bass_key))){
            mVisualizerView.setShowBass((sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.check_box_bass_default_value))));
        }
        else if (key.equals(getString(R.string.square_key))){

            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.check_box_square_default_value)));
        }

        else if (key.equals((getString(R.string.triangle_key)))){

            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.check_box_triangle_default_value)));
        }
        else if (key.equals(getString(R.string.colors_option_key))){
            mVisualizerView.setColor(sharedPreferences.getString(key, getString(R.string.value_label_red_option)));
        }
        else if (key.equals(getString(R.string.size_key))){
            String size= sharedPreferences.getString(getString(R.string.size_key), getString(R.string.size_default));

            mVisualizerView.setMinSizeScale(Float.parseFloat(size));
        }

    }


}
