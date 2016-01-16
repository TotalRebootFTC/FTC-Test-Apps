package com.brian.fixkinematic;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.util.Log;

public class Simulator
{
    private static final String TAG = Simulator.class.getSimpleName();

    private World world;

    private long timeAccumulator = 0;
    private int stepCount = 0;
    public boolean move = false;

    private static final long stepInMillis = 20;
    private static final float stepInSeconds = stepInMillis / 1000.0f;
    private static final int velocityIterations = 10;
    private static final int positionIterations = 5;

    private Vec2 zero = new Vec2(0,0);

    private List<MouseJoint> userActions = new ArrayList<>();
    Body mbody;

    public Simulator()
    {
        Vec2 gravity = new Vec2(0.0f, 0.0f);
        world = new World(gravity, true);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(10.0f, 0.0f);
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(11.0f, 1.0f, new Vec2(0.0f, -1.0f), 0.0f);
        groundBody.createFixture(polygonShape, 1.0f);
        polygonShape.setAsBox(1.0f, 17.0f, new Vec2(-11.0f, 16.0f), 0.0f);
        groundBody.createFixture(polygonShape, 1.0f);
        polygonShape.setAsBox(1.0f, 17.0f, new Vec2(11.0f, 16.0f), 0.0f);
        groundBody.createFixture(polygonShape, 1.0f);
        polygonShape.setAsBox(11.0f, 1.0f, new Vec2(0.0f, 33.0f), 0.0f);
        groundBody.createFixture(polygonShape, 1.0f);
        createRobot(10, 17);
    }

    public void resume(Module[] modules)
    {
        move = true;
        mbody.applyLinearImpulse(modules[0].getXY(), new Vec2(1, 1));
        mbody.applyLinearImpulse(modules[1].getXY(), new Vec2(-1, 1));
        mbody.applyLinearImpulse(modules[2].getXY(), new Vec2(-1, -1));
        mbody.applyLinearImpulse(modules[3].getXY(), new Vec2(1, -1));
    }

    public void createRobot(float x, float y)
    {
        BodyDef def = new BodyDef();
        def.type = BodyType.DYNAMIC;
        def.position.set(x, y);
        mbody = world.createBody(def);
        createModule(1, 1);
        createModule(-1, 1);
        createModule(-1, -1);
        createModule(1, -1);
    }

    private void createModule(float x,float y)
    {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .5f, new Vec2(x, y), 0);
//define fixture of the body.
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.5f;
        fd.friction = 0.3f;
        fd.restitution = 0.5f;
        mbody.createFixture(fd);
    }

    public void update(long dt)
    {
        timeAccumulator += dt;
        while (timeAccumulator >= stepInMillis)
        {
            world.step(stepInSeconds, velocityIterations, positionIterations);
            timeAccumulator -= stepInMillis;
            stepCount++;
        }
        if(stepCount > 200)
        {
            stopBot();
        }
    }

    private void stopBot()
    {
        Log.w("model", "done");
        stepCount = 0;
        mbody.setLinearVelocity(zero);
        mbody.setAngularVelocity(0);
        move = false;
    }

    public Body getBodyList()
    {
        return world.getBodyList();
    }

    public void userActionStart(int pointerId, final float x, final float y)
    {
        final List<Fixture> fixtures = new ArrayList<Fixture>();
        final Vec2 vec = new Vec2(x, y);
        world.queryAABB(new QueryCallback()
        {
            public boolean reportFixture(Fixture fixture)
            {
                Log.i(TAG, "reportFixture: " + fixture);
                //if (fixture.testPoint(vec))
                //{
                fixtures.add(fixture);
                //}
                return true;
            }
        }, new AABB(vec, vec));
        if (fixtures.size() > 0)
        {
            Fixture fixture = fixtures.get(0);
            Log.i(TAG, "creating mouse joint: " + fixture);
            Body body = fixture.getBody();

            MouseJointDef def = new MouseJointDef();
            def.bodyA = body;
            def.bodyB = body;
            def.maxForce = 1000.0f * body.getMass();
            def.target.set(x, y);

            MouseJoint joint = (MouseJoint) world.createJoint(def);
            joint.m_userData = pointerId;
            userActions.add(joint);
        }
    }

    public void userActionUpdate(int pointerId, float x, float y)
    {
        for (MouseJoint joint : userActions)
        {
            if (pointerId == (Integer) joint.m_userData)
            {
                joint.setTarget(new Vec2(x, y));
                break;
            }
        }
    }

    public void userActionEnd(int pointerId, float x, float y)
    {
        for (MouseJoint joint : userActions)
        {
            if (pointerId == (Integer) joint.m_userData)
            {
                world.destroyJoint(joint);
                userActions.remove(joint);
                break;
            }
        }
    }
}