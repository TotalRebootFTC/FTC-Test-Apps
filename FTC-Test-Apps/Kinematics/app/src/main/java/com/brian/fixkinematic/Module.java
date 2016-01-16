package com.brian.fixkinematic;
import org.jbox2d.common.Vec2;

public class Module
{
    public Vec2 XY = new Vec2(0,0);
    public float x = 0;
    public float y = 0;

    public void setXY(float ix, float iy)
    {
        XY = new Vec2(ix, iy);
        x = ix;
        y = iy;
    }
    public void setXY(Vec2 vec)
    {
        XY = vec;
    }
    public Vec2 getXY()
    {
        return XY;
    }


    public Vec2 angle;
    public Vec2 speed;
    public void set(Vec2 iAngle, Vec2 ispeed)
    {
        angle = iAngle;
        speed = ispeed;
    }
}