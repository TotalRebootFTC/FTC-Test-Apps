package com.brian.robocamera;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.nio.ByteBuffer;

import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

import static boofcv.android.ConvertBitmap.declareStorage;

/**
 * Created by brian on 12/28/2015
 */
public abstract class CvAlgorithm implements Runnable
{
    ImageParameters p;

    public CvAlgorithm(ImageParameters parameters)
    {
        p = parameters;
    }

    protected void done()
    {
        p.sensor.color = p.output;
    }
/*
    protected int xInput()
    {
        return (int) (p.view.xInput * p.image.width);
    }

    protected int yInput()
    {
        return (int) (p.view.yInput * p.image.width);
    }*/
    protected static void colorizeSign( ImageFloat32 input , float maxAbsValue , Bitmap output , byte[] storage )
    {
        if( storage == null )
            storage = declareStorage(output,null);

        if( maxAbsValue < 0 )
            maxAbsValue = ImageStatistics.maxAbs(input);

        int indexDst = 0;

        for( int y = 0; y < input.height; y++ )
        {
            int indexSrc = input.startIndex + y*input.stride;
            for( int x = 0; x < input.width; x++ )
            {
                float value = input.data[ indexSrc++ ];
                if (value > 0)
                {
                    storage[indexDst++] = (byte) (255f * value / maxAbsValue);
                    storage[indexDst++] = 0;
                    storage[indexDst++] = 0;
                    storage[indexDst++] = (byte) 0xFF;
                } else if(value > 0)
                {
                    storage[indexDst++] = 0;
                    storage[indexDst++] = 0;
                    storage[indexDst++] = (byte) (-255f * value / maxAbsValue);
                    storage[indexDst++] = (byte) 0xFF;
                }else
                {
                    storage[indexDst++] = 0;
                    storage[indexDst++] = 0;
                    storage[indexDst++] = 0;
                    storage[indexDst++] = 0;
                }
            }
        }

        output.copyPixelsFromBuffer(ByteBuffer.wrap(storage));
    }

    protected void colorizeGradient( ImageFloat32 derivX , ImageFloat32 derivY , float maxAbsValue, Bitmap output)
    {
        byte[] storage = declareStorage(output, null);

        if( maxAbsValue < 0 )
        {
            maxAbsValue = ImageStatistics.maxAbs(derivX);
            maxAbsValue = Math.max(maxAbsValue, ImageStatistics.maxAbs(derivY));
        }
        if( maxAbsValue == 0 )
            return;

        int indexDst = 0;

        for( int y = 0; y < derivX.height; y++ )
        {
            int indexX = derivX.startIndex + y*derivX.stride;
            int indexY = derivY.startIndex + y*derivY.stride;

            for( int x = 0; x < derivX.width; x++ )
            {
                float valueX = derivX.data[ indexX++ ];
                float valueY = derivY.data[ indexY++ ];

                int r=0,g=0,b=0;

                if( valueX > 0 )
                {
                    r = (int)(255f*valueX/maxAbsValue);
                } else
                {
                    g = (int)(-255f*valueX/maxAbsValue);
                }
                if( valueY > 0 )
                {
                    b = (int)(255f*valueY/maxAbsValue);
                } else
                {
                    int v = (int)(-255f*valueY/maxAbsValue);
                    r += v;
                    g += v;
                    if( r > 255 ) r = 255;
                    if( g > 255 ) g = 255;
                }
                storage[indexDst++] = (byte) r;
                storage[indexDst++] = (byte) g;
                storage[indexDst++] = (byte) b;
                if(r + g + b < 150)
                {
                    storage[indexDst++] = (byte) 0x00;
                }else
                {
                    storage[indexDst++] = (byte) 0xFF;
                }
            }
        }
        output.copyPixelsFromBuffer(ByteBuffer.wrap(storage));
    }

    public void set1(ImageFloat32 input)
    {
        for( int y = 0; y < input.height; y++ )
        {
            int indexSrc = input.startIndex + y*input.stride;
            for( int x = 0; x < input.width; x++ )
            {
                input.data[ indexSrc++ ] = 1;
            }
        }
    }

    /**
     * Created by brian
     */
    public static class ImageParameters
    {
        static final int HANDLER_CODE = 776;
        public byte[] data;
        TextView view;
        int output;
        MultiSpectral<ImageFloat32> image;
        CameraSensor sensor;
    }
}
