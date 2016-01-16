package com.brian.fixkinematic;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

public class RoboView extends View implements Handler.Callback
{
    private Simulator model;
    private Paint paint;
    public static final float VIEWPORT_SIZE = 20.0f; // meters
    public static final int MESSAGE_ID = 776;
    private Handler handler;
    private long elapsed;
    List<Float> previous= new ArrayList<Float>();

    public RoboView(Context context)
    {
        super(context);
        init();
    }

    public RoboView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RoboView(Context context, AttributeSet attrs, int defstyle)
    {
        super(context, attrs, defstyle);
        init();
    }

    private void init()
    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(0.05f);
        handler = new Handler(this);
        model = new Simulator();
    }

    @Override
    public boolean handleMessage(Message message)
    {
        if(message.what == MESSAGE_ID)
        {
            model.update(SystemClock.uptimeMillis() - elapsed);
            elapsed = SystemClock.uptimeMillis();
            invalidate();
            if (model.move)
            {
                handler.sendEmptyMessageDelayed(MESSAGE_ID, 10);
            }else
            {
                ((MainActivity) getContext()).changeView(this);
                handler.removeMessages(MESSAGE_ID);
            }
            return true;
        }
        return false;
    }

    public void resumeView(Module[] modules)
    {
        model.resume(modules);
        elapsed = SystemClock.uptimeMillis();
        handler.sendEmptyMessage(MESSAGE_ID);
        previous = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float viewportSize = RoboView.VIEWPORT_SIZE;
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        float x = event.getX(pointerIndex) * viewportSize / getWidth();
        float y = (getHeight() - event.getY(pointerIndex)) * viewportSize / getWidth();
        int pointerId = event.getPointerId(pointerIndex);
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
        {
            model.userActionStart(pointerId, x, y);
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            for (int i = 0; i < event.getPointerCount(); i++)
            {
                x = event.getX(i) * viewportSize / getWidth();
                y = (getHeight() - event.getY(i)) * viewportSize / getWidth();
                pointerId = event.getPointerId(i);
                model.userActionUpdate(pointerId, x, y);
            }
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
        {
            model.userActionEnd(pointerId, x, y);
        }
        return true; //processed
    }
/////////////////////draw
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.translate(0.0f, getHeight());
        float scale = getWidth() / VIEWPORT_SIZE;
        canvas.scale(scale, -scale);
        drawBodies(canvas);
    }

    private void drawBodies(Canvas canvas)
    {
        Body body = model.getBodyList();
        while (body != null)
        {
            Fixture fixture = body.getFixtureList();
            while (fixture != null)
            {
                Shape shape = fixture.getShape();
                drawShape(canvas, body.getPosition(), body.getAngle(), shape);
                if (previous.size() > 255)
                {
                    previous.remove(0);
                }
                fixture = fixture.getNext();
            }
            body = body.getNext();
        }
    }

    private void drawShape(Canvas canvas, Vec2 pos, float angle, Shape shape)
    {
        canvas.save();
        canvas.rotate(180.0f * angle / MathUtils.PI, pos.x, pos.y);
        if (shape.m_type == ShapeType.CIRCLE) {
            CircleShape circle = (CircleShape) shape;
            paint.setColor(0xFFFF6666);
            canvas.drawCircle(pos.x + circle.m_p.x, pos.y + circle.m_p.y, circle.m_radius, paint);
            paint.setColor(0xFFFFFFFF);
            canvas.drawLine(pos.x + circle.m_p.x, pos.y + circle.m_p.y, pos.x + circle.m_p.x + circle.m_radius, pos.y + circle.m_p.y, paint);
        } else if (shape.m_type == ShapeType.POLYGON)
        {
            PolygonShape polygon = (PolygonShape) shape;
            Path path = new Path();
            Vec2 vec = polygon.m_vertices[polygon.m_vertexCount - 1];
            path.moveTo(pos.x + vec.x, pos.y + vec.y);
            for (int i = 0; i < polygon.m_vertexCount; i++)
            {
                vec = polygon.m_vertices[i];
                path.lineTo(pos.x + vec.x, pos.y + vec.y);
            }
            if (polygon.m_vertexCount == 3)
            {
                paint.setColor(0xFF66FF66);
            } else
            {
                paint.setColor(0xFF6666FF);
            }
            canvas.drawPath(path, paint);
        }
        canvas.restore();
    }
}