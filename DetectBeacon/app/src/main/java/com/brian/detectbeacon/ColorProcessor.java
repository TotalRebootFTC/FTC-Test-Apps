package com.brian.detectbeacon;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import boofcv.alg.color.ColorHsv;
import boofcv.core.encoding.ConvertNV21;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;
import georegression.metric.UtilAngle;

/**
 * Created by brian
 */
public class ColorProcessor extends CvAlgorithm
{
    ImageParameters p;
    Bitmap bitmap;

    MultiSpectral<ImageFloat32> image;

    public ColorProcessor(ImageParameters params)
    {
        super(params);
    }

    @Override
    public void run()
    {
        //TODO: image processing
        ConvertNV21.nv21ToMsRgb_F32(p.data, p.image.width, p.image.height, image);
        bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888);

        MultiSpectral<ImageFloat32> hsv = image.createSameShape();
        ColorHsv.rgbToHsv_F32(image, hsv);

        final float maxDist = .16f;
        final float adjustUnits = (float) (Math.PI / 2.0);

        ImageFloat32 H = hsv.getBand(0);
        ImageFloat32 S = hsv.getBand(1);

        if(p.view.reset)
        {
            p.hue = H.unsafe_get(xInput(), yInput());
            p.sat = S.unsafe_get(xInput(), yInput());
            Log.w("touched", "hue:" + String.valueOf(p.hue) + " sat:" + String.valueOf(p.hue));
            p.view.reset = false;
        }

        for (int y = 0; y < hsv.height; y++)
        {
            for (int x = 0; x < hsv.width; x++)
            {
                // Hue is an angle in radians, so simple subtraction doesn't work
                float dh = UtilAngle.dist(H.unsafe_get(x, y), p.hue);
                float satsqare = (S.unsafe_get(x, y) - .7f) * adjustUnits;
                satsqare *= satsqare;
                // this distance measure is a bit naive, but good enough for to demonstrate the concept
                if (dh * dh + satsqare < maxDist)
                {
                    bitmap.setPixel(x, y, Color.GREEN);
                }
                dh = UtilAngle.dist(H.unsafe_get(x, y), (float)Math.PI);
                /*if (dh * dh + satsqare < maxDist)
                {
                    bitmap.setPixel(x, y, Color.BLUE);
                }
                dh = UtilAngle.dist(H.unsafe_get(x, y), 2 * (float)Math.PI);
                if (dh * dh + satsqare < maxDist)
                {
                    bitmap.setPixel(x, y, Color.RED);
                }*/
            }
        }
        done();
    }
}