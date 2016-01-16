package com.brian.detectbeacon;

import android.graphics.Color;
import android.util.Log;

import boofcv.abst.feature.detect.peak.SearchLocalPeak;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.filter.derivative.DerivativeType;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.core.encoding.ConvertNV21;
import boofcv.core.image.border.BorderType;
import boofcv.factory.feature.detect.peak.FactorySearchLocalPeak;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

/**
 * Created by brian on 12/28/2015
 */
public class BeaconDetector extends CvAlgorithm
{
    public BeaconDetector(ImageParameters parameters)
    {
        super(parameters);
    }

    @Override
    public void run()
    {
        ConvertNV21.nv21ToMsRgb_F32(p.data, p.image.width, p.image.height, p.image);
        MultiSpectral<ImageFloat32> hsv = p.image.createSameShape();
        ImageFloat32 derivX = new ImageFloat32(p.image.width, p.image.height);
        ImageFloat32 derivY = new ImageFloat32(p.image.width, p.image.height);
        SearchLocalPeak<ImageFloat32> peak = FactorySearchLocalPeak.meanShiftUniform(10, .001f, ImageFloat32.class);

        //TODO: image processing
        ConvertNV21.nv21ToMsRgb_F32(p.data, p.image.width, p.image.height, p.image);
        ColorHsv.rgbToHsv_F32(p.image, hsv);
        GImageDerivativeOps.gradient(DerivativeType.SOBEL, hsv.getBand(0), derivX, derivY, BorderType.EXTENDED);
        //colorizeSign(derivX, 1, p.bitmap, null);
        peak.setImage(derivX);
        peak.setSearchRadius(10);
        peak.search(p.image.width / 2, p.image.height / 2);
        int x,y;
        if(peak.getPeakX() > p.bitmap.getWidth())
        {
            x = p.bitmap.getWidth() - 1;
        }else
        {
            if(peak.getPeakX() < 0)
            {
                x = 0;
            }else
            {
                x = (int) peak.getPeakX();
            }
        }
        if(peak.getPeakY() > p.bitmap.getHeight() - 1)
        {
            y = p.bitmap.getHeight();
        }else
        {
            if(peak.getPeakY() < 0)
            {
                y = 0;
            }else
            {
                y = (int) peak.getPeakY();
            }
        }
        p.bitmap.setPixel(x, y, Color.CYAN);
        Log.w("peak", String.valueOf(peak.getPeakX()));
        done();
    }
}