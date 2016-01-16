package com.brian.fixkinematic;

import android.content.*;
import android.graphics.*;
import android.os.SystemClock;
import android.util.*;
import android.view.*;
import java.util.*;
import android.os.Handler;

import org.jbox2d.common.Vec2;

public class Graphs extends SurfaceView
{
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float x, y;
    Module[] modules = new Module[4];
    public Graphs(Context context)
    {
        super(context);
        init();
    }

    public Graphs(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public Graphs(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public Module[] getModules()
    {
        for(int i = 0; i < 4; i++)
        {
            modules[i].setXY(new Vec2(modules[i].x / 4, -modules[i].y / 4));
        }
        return modules;
    }

    private void init()
    {
        paint.setStrokeWidth(2);
        setWillNotDraw(false);
        for(int i = 0; i < 4; i++)
        {
            modules[i] = new Module();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (x > getWidth() / 2)
        {
            if(y > getHeight() / 2)
            {
                modules[3].setXY(x - getWidth() * .75f, y - getHeight() * .75f);
            }else
            {
                modules[0].setXY(x - getWidth() * .75f, y - getHeight() / 4);
            }
        }else
        {
            if(y > getHeight() / 2)
            {
                modules[2].setXY(x - getWidth() / 4, y - getHeight() * .75f);
            }else
            {
                modules[1].setXY(x - getWidth() / 4, y - getHeight() / 4);
            }
        }
        canvas.drawARGB(250, 250, 250, 250);
        paint.setColor(Color.BLACK);
        canvas.drawLine(getWidth() / 4, 20, getWidth() / 4, getHeight() / 2 - 20, paint);
        canvas.drawLine(20, getHeight() / 4, getWidth() / 2 - 20, getHeight() / 4, paint);
        canvas.drawLine(getWidth() * .75f, 20, getWidth() * .75f, getHeight() / 2 - 20, paint);
        canvas.drawLine(getWidth() / 2 + 20, getHeight() / 4, getWidth() - 20, getHeight() / 4, paint);
        canvas.drawLine(20, getHeight() * .75f, getWidth() / 2 - 20, getHeight() * .75f, paint);
        canvas.drawLine(getWidth() / 4, getHeight() / 2 + 20, getWidth() / 4, getHeight() - 20, paint);
        canvas.drawLine(getWidth() / 2 + 20, getHeight() * .75f, getWidth() - 20, getHeight() * .75f, paint);
        canvas.drawLine(getWidth() * .75f, getHeight() / 2 + 20, getWidth() * .75f, getHeight() - 20, paint);
        paint.setColor(Color.RED);
        canvas.drawLine(getWidth() * .75f, getHeight() / 4, getWidth() * .75f + modules[0].x, getHeight() / 4 + modules[0].y, paint);
        canvas.drawLine(getWidth() / 4, getHeight() / 4, getWidth() / 4 + modules[1].x, getHeight() / 4 + modules[1].y, paint);
        canvas.drawLine(getWidth() / 4, getHeight() * .75f, getWidth() / 4 + modules[2].x, getHeight() * .75f + modules[2].y, paint);
        canvas.drawLine(getWidth() * .75f, getHeight() * .75f, getWidth() * .75f + modules[3].x, getHeight() * .75f + modules[3].y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        x = event.getX();
        y = event.getY();
        invalidate();
        return true; //processed
    }
}