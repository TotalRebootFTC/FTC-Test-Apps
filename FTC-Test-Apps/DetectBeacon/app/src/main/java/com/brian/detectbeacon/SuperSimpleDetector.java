package com.brian.detectbeacon;

import android.graphics.Color;
import android.util.Log;

import boofcv.alg.color.ColorHsv;
import boofcv.core.encoding.ConvertNV21;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;
import georegression.metric.UtilAngle;

/**
 * Created by brian on 12/31/2015
 */
public class SuperSimpleDetector extends CvAlgorithm
{
    public SuperSimpleDetector(ImageParameters parameters)
    {
        super(parameters);
    }

    @Override
    public void run()
    {
        ConvertNV21.nv21ToMsRgb_F32(p.data, p.image.width, p.image.height, p.image);
        MultiSpectral<ImageFloat32> hsv = p.image.createSameShape();
        ColorHsv.rgbToHsv_F32(p.image, hsv);
        ImageFloat32 H = hsv.getBand(0);
        float left = H.unsafe_get(100, 50);
        float right = H.unsafe_get(200, 50);
        p.bitmap.setPixel(100, 50, Color.CYAN);
        p.bitmap.setPixel(200, 50, Color.CYAN);
        if(UtilAngle.distHalf(Math.PI / 2, left) < UtilAngle.distHalf(Math.PI / 2, right))
        {
            p.bitmap.setPixel(150, 100, Color.RED);
        }else
        {
            p.bitmap.setPixel(150, 100, Color.BLUE);
        }
        Log.w("left", String.valueOf(left));
        Log.w("right", String.valueOf(right));
        done();
    }
}