package com.treboot.brian.swervedrivecalaculator;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{
    boolean graph = true;
    public float r = (float) 38.4187;
    public float w = (float) 24.0;
    public float l = (float) 30.0;
    public float a;
    public float b;
    public float c;
    public float d;
    public float FWD;
    public float STR;
    public float RCW;
    public float spd1 = (float) 0;
    public float spd2;
    public float spd3;
    public float spd4;
    public float angle1;
    public float angle2;
    public float angle3;
    public float angle4;
    public float max;

    SurfaceHolder holder;
    SurfaceView graphs;
    InputMethodManager imm;
    EditText fwd;
    EditText str;
    EditText rcw;
    TextView ws1;
    TextView ws2;
    TextView ws3;
    TextView ws4;

    public void updateUI(View view)
    {
        try
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch(NullPointerException e) {}
        try
        {
            FWD = Float.parseFloat(fwd.getText().toString());
        }catch (NumberFormatException e)
        {
            FWD = 0;
        }
        try
        {
            STR = Float.parseFloat(str.getText().toString());
        }catch (NumberFormatException e)
        {
            STR = 0;
        }
        try
        {
            RCW = Float.parseFloat(rcw.getText().toString());
        }catch (NumberFormatException e)
        {
            RCW = 0;
        }

        a = STR - RCW * (l/r);
        b = STR + RCW * (l/r);
        c = FWD - RCW * (w/r);
        d = FWD + RCW * (w/r);

        angle1 = (float) Math.atan2(b, c);
        angle2 = (float) Math.atan2(b, d);
        angle3 = (float) Math.atan2(a, d);
        angle4 = (float) Math.atan2(a, c);
        spd1 = (float)Math.sqrt(Math.pow(b, 2) + Math.pow(c, 2));
        spd2 = (float)Math.sqrt(Math.pow(b, 2) + Math.pow(d, 2));
        spd3 = (float)Math.sqrt(Math.pow(a, 2) + Math.pow(d, 2));
        spd4 = (float)Math.sqrt(Math.pow(a, 2) + Math.pow(c, 2));
        max = Math.max(Math.max(spd1, spd2), Math.max(spd3, spd4));
        if (max >= 1)
        {
            spd1 /= max;
            spd2 /= max;
            spd3 /= max;
            spd4 /= max;
        }

        ws1.setText("speed 1 =" + spd1 + " angle 1 =" + String.format("%.2f", angle1 * 180/(float) Math.PI));
        ws2.setText("speed 2 =" + spd2 + " angle 1 =" + String.format("%.2f", angle2 * 180/(float) Math.PI));
        ws3.setText("speed 3 =" + spd3 + " angle 1 =" + String.format("%.2f", angle3 * 180/(float) Math.PI));
        ws4.setText("speed 4 =" + spd4 + " angle 1 =" + String.format("%.2f", angle4 * 180/(float) Math.PI));

        if(holder.getSurface().isValid())
        {
            //draw raster graphics
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);

            Canvas c = holder.lockCanvas();
            c.drawARGB(250, 250, 250, 250);

            c.drawLine(c.getWidth() / 4, 20, c.getWidth() / 4, c.getHeight() / 2 - 20, paint);
            c.drawLine(20, c.getHeight() / 4, c.getWidth() / 2 - 20, c.getHeight() / 4, paint);
            c.drawLine(3 * c.getWidth() / 4, 20, 3 * c.getWidth() / 4, c.getHeight() / 2 - 20, paint);
            c.drawLine(c.getWidth() / 2 + 20, c.getHeight() / 4, c.getWidth() - 20, c.getHeight() / 4, paint);
            c.drawLine(c.getWidth() / 4, c.getHeight() / 2 + 20, c.getWidth() / 4, c.getHeight() - 20, paint);
            c.drawLine(20, 3 * c.getHeight() / 4, c.getWidth() / 2 - 20, 3 * c.getHeight() / 4, paint);
            c.drawLine(3 * c.getWidth() / 4, c.getHeight() / 2 + 20, 3 * c.getWidth() / 4, c.getHeight() - 20, paint);
            c.drawLine(c.getWidth() / 2 + 20, 3 * c.getHeight() / 4, c.getWidth() - 20, 3 * c.getHeight() / 4, paint);

            paint.setColor(Color.RED);
            final int scalar = c.getHeight() / 2 - 20;
            c.drawLine(3 * c.getWidth() / 4, c.getHeight() / 4, 3 * c.getWidth() / 4 + spd1 * scalar * (float) Math.cos(Math.PI / 2 - angle1), c.getHeight() / 4 + spd1 * scalar * (float) Math.sin(-Math.PI / 2 - angle1), paint);
            c.drawLine(c.getWidth() / 4, c.getHeight() / 4, c.getWidth() / 4 + spd2 * scalar * (float) Math.cos(Math.PI / 2 - angle2), c.getHeight() / 4 + spd2 * scalar * (float) Math.sin(-Math.PI / 2 - angle2), paint);
            c.drawLine(c.getWidth() / 4, 3 * c.getHeight() / 4, c.getWidth() / 4 + spd3 * scalar * (float) Math.cos(Math.PI / 2 - angle3), 3 * c.getHeight() / 4 + spd3 * scalar * (float) Math.sin(-Math.PI / 2 - angle3), paint);
            c.drawLine(3 * c.getWidth() / 4, 3 * c.getHeight() / 4, 3 * c.getWidth() / 4 + spd4 * scalar * (float) Math.cos(Math.PI / 2 - angle4), 3 * c.getHeight() / 4 + spd4 * scalar * (float) Math.sin(-Math.PI / 2 - angle4), paint);
            holder.unlockCanvasAndPost(c);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fwd = (EditText) findViewById(R.id.fwdText);
        str = (EditText) findViewById(R.id.strText);
        rcw = (EditText) findViewById(R.id.rcwText);
        ws1 = (TextView) findViewById(R.id.wheel1);
        ws2 = (TextView) findViewById(R.id.wheel2);
        ws3 = (TextView) findViewById(R.id.wheel3);
        ws4 = (TextView) findViewById(R.id.wheel4);
        graphs = (SurfaceView) findViewById(R.id.surfaceView);
        holder = graphs.getHolder();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        updateUI(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }else if(id == R.id.graphicLayout)
        {
            if(!graph)
            {
                graph = true;
                graphs.setVisibility(View.VISIBLE);
            }else
            {
                graph = false;
                graphs.setVisibility(View.GONE);
            }
            return true;
        }else if(id == R.id.chain2wheels)
        {
            Intent i = new Intent(this, SecondActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
