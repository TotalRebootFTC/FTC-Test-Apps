package com.brian.sensortest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity
{
    TextView maccel;
    TextView mbump;
    EditText alpha;
    Sensors sensors;
    SurfaceHolder holder;
    SurfaceView graphs;
    int counter = 0;
    Paint paint;
    Canvas c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sensors = new Sensors(this);
        setContentView(R.layout.activity_main);
        maccel = (TextView) findViewById(R.id.accel);
        alpha = (EditText) findViewById(R.id.alpha);
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        alpha.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
					try
					{
						sensors.setAlpha(Float.parseFloat(alpha.getText().toString()));
					}
					catch (Exception e) {}
					imm.toggleSoftInput(0,0);
                    return true;
                }
                return false;
            }
        });
        mbump = (TextView) findViewById(R.id.bump);
        graphs = (SurfaceView) findViewById(R.id.surfaceView);
        holder = graphs.getHolder();
        paint = new Paint();
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensors.resume();
        update();
    }
    @Override
    protected  void onPause()
    {
        super.onPause();
        sensors.pause();
    }

    public void update()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable()
                             {
                                 public void run()
                                 {
                                     maccel.setText(sensors.getRotationText());
                                     mbump.setText(sensors.getBumpText());

                                     if (holder.getSurface().isValid())
                                     {
                                         //draw vector graphics
                                         if (counter > 5)
                                         {
                                             c = holder.lockCanvas();
                                             paint.setColor(Color.BLACK);
                                             c.drawARGB(250, 250, 250, 250);
                                             c.drawLine(c.getWidth() / 2, 20, c.getWidth() / 2, c.getHeight() - 20, paint);
                                             c.drawLine(20, c.getHeight() / 2, c.getWidth() - 20, c.getHeight() / 2, paint);
                                             if (sensors.significantMotion())
                                             {
                                                 paint.setColor(Color.RED);
                                                 counter = 0;
                                             }
                                             else
                                             {
                                                 paint.setColor(Color.GREEN);
                                             }
                                             c.drawLine(c.getWidth() / 2, c.getHeight() / 2, c.getWidth() / 2 + c.getWidth() * (sensors.accel[0] / 5), c.getHeight() / 2 + c.getHeight() * (sensors.accel[2] / 5), paint);
                                             holder.unlockCanvasAndPost(c);
                                         }else
                                         {
                                             counter++;
                                         }
                                     }
                                 }
                             }
                );
            }
        };
        timer.schedule(doAsynchronousTask, 0, 200);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}