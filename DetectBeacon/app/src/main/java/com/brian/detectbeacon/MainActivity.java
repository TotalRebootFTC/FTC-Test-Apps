package com.brian.detectbeacon;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
    Visualize view;
    CameraSensor cameraSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (Visualize) findViewById(R.id.view);
        cameraSensor = new CameraSensor(0, view);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        cameraSensor.mResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        cameraSensor.pause();
    }
}