package com.brian.detectbeacon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by brian
 */
public class Visualize extends SurfaceView
{
    Paint paint;
    float xInput, yInput;
    boolean reset = false;
    Bitmap mBitmap;

    public Visualize(Context context)
    {
        super(context);
        init();
    }
    public Visualize(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public Visualize(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init()
    {
        setWillNotDraw(false);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mBitmap != null)
        {
            canvas.drawBitmap(mBitmap, 0, 0, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            xInput = event.getX() / getRight();
            yInput = event.getY() / getBottom();
            Log.w("x" , String.valueOf(xInput));
            Log.w("y" , String.valueOf(yInput));
            reset = true;
        }
        return true; //processed
    }
}