package com.brian.sensortest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class Sensors implements SensorEventListener
{
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor vector;
    private float alpha = 0.8f;
    float[] accel = new float[3];
    float[] angles = new float[3];

    public Sensors(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void resume()
    {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, vector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void pause()
    {
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    float mGravity[];
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = event.values;
            accel[0] = event.values[0] - alpha * accel[0] - (1 - alpha) * event.values[0];
            accel[1] = event.values[1] - alpha * accel[1] - (1 - alpha) * event.values[1];
            accel[2] = event.values[2] - alpha * accel[2] - (1 - alpha) * event.values[2];
        }
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            float mag[] = event.values;
            if (mGravity != null)
            {
                float R[] = new float[9];
                float I[] = new float[9];;
                SensorManager.getRotationMatrix(R, I, mGravity, mag);
                SensorManager.getOrientation(R, angles);
            }
        }
    }

    public String getBumpText()
    {
        return "bump: x=" + String.format("%.2f", accel[0]) + " y=" + String.format("%.2f", accel[1]) + " z=" + String.format("%.2f", accel[2]);
    }
    public String getRotationText()
    {
        return "rotation: x=" + String.format("%.2f", angles[0]) + " y=" + String.format("%.2f", angles[1]) + " z=" + String.format("%.2f", angles[2]);
    }

    public boolean significantMotion()
    {
        return (accel[0] > 1);
    }

    public void setAlpha(float num)
    {
        alpha = num;
    }
}