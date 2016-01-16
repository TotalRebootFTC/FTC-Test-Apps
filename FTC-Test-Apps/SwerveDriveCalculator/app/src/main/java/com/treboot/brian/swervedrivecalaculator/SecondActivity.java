package com.treboot.brian.swervedrivecalaculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SecondActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chain_mode);
        SurfaceView view = (SurfaceView) findViewById(R.id.surfaceViewc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.chain_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.individual)
        {
            this.finish();
        }
        else if(id == R.id.reset)
        {
            MySurfaceView.reset();
        }

        return super.onOptionsItemSelected(item);
    }
}