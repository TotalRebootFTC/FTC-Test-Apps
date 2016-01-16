package com.brian.detectbeacon;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

/**
 * Created by brian
 */
public class CameraSensor extends SurfaceTexture implements Camera.PreviewCallback, SurfaceHolder.Callback
{
    Visualize view;
    HandlerThread thread;
    Handler handler;
    Camera camera;
    int width, height;
    CvAlgorithm.ImageParameters parameters;

    public CameraSensor(int texName, Visualize view)
    {
        super(texName);
        this.view = view;
        init();
    }

    private void init()
    {
        view.getHolder().addCallback(this);
        parameters = new CvAlgorithm.ImageParameters();
        parameters.view = view;
        parameters.hue = 0;
        parameters.sat = 1;
        parameters.handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == CvAlgorithm.ImageParameters.HANDLER_CODE)
                {
                    view.mBitmap = (Bitmap) msg.obj;
                    view.invalidate();
                }else
                {
                    super.handleMessage(msg);
                }
            }
        };
    }
long time;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if(time < SystemClock.uptimeMillis())
        {
            time = SystemClock.uptimeMillis() + 100;
            parameters.data = data.clone();
            handler.post(new BeaconDetector(parameters));
        }
    }

    public void pause()
    {
        if(thread != null)
        {
            thread.quitSafely();
            thread = null;
        }
        if(camera != null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void mResume()
    {
        setCamera();
        thread = new HandlerThread("processPictures");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    private void setCamera()
    {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters param = camera.getParameters();

        List<Camera.Size> sizes = param.getSupportedPreviewSizes();
        int best = -1;
        int bestScore = Integer.MAX_VALUE;

        for( int i = 0; i < sizes.size(); i++ )
        {
            Camera.Size s = sizes.get(i);

            int dx = s.width - 320;
            int dy = s.height - 240;

            int score = dx*dx + dy*dy;
            if( score < bestScore )
            {
                best = i;
                bestScore = score;
            }
        }

        Camera.Size s = sizes.get(best);
        width = s.width;
        height = s.height;
        parameters.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        parameters.image = new MultiSpectral<>(ImageFloat32.class, width, height, 3);
        param.setPictureFormat(ImageFormat.NV21);
        param.setPreviewSize(width, height);
        camera.setParameters(param);
        //parameters.width = width;
        //parameters.height = height;
        camera.startPreview();
        camera.setPreviewCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if(camera != null)
        {
            try
            {
                camera.setPreviewDisplay(holder);
                //camera.setPreviewTexture(dummy);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (camera != null)
        {
            camera.stopPreview();
        }
    }
}