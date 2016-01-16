package com.brian.fixkinematic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private RoboView view;
    Button button;
    Graphs graphs;
    TextView out;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.Button);
        graphs = (Graphs) findViewById(R.id.graphs);
        graphs.invalidate();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    public void changeView(View v)
    {
        out = (TextView) findViewById(R.id.out);
        if(v == button)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActionBar().hide();
            setContentView(R.layout.model);
            view = (RoboView)findViewById(R.id.view);
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            view.resumeView(graphs.getModules());
        }else if(v == view)
        {
            setContentView(R.layout.activity_main);
            out.setText(R.string.output);
            button = (Button) findViewById(R.id.Button);
            graphs = (Graphs) findViewById(R.id.graphs);
            graphs.invalidate();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getActionBar().show();
        }
    }
}