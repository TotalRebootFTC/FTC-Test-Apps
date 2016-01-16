package com.treboot.brian.swervedrivecalaculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float X, Y, radius;
    private boolean drawing = false;
    private static boolean reset = false;

    @Override
    protected void onDraw(Canvas canvas)
    {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    }

    public MySurfaceView(Context context)
    {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public static void reset()
    {
        reset = true;
    }
}

