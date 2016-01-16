package com.brian.detectbeacon;

import boofcv.alg.color.ColorHsv;
import boofcv.alg.filter.derivative.DerivativeType;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.core.encoding.ConvertNV21;
import boofcv.core.image.border.BorderType;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

/**
 * Created by brian on 12/26/2015
 */
public class GradientProcessor extends CvAlgorithm
{
    public GradientProcessor(ImageParameters params)
    {
        super(params);
    }

    @Override
    public void run()
    {
        MultiSpectral<ImageFloat32> hsv = p.image.createSameShape();
        ImageFloat32 derivX = new ImageFloat32(p.image.width, p.image.height);
        ImageFloat32 derivY = new ImageFloat32(p.image.width, p.image.height);

        //TODO: image processing
        ConvertNV21.nv21ToMsRgb_F32(p.data, p.image.width, p.image.height, p.image);
        ColorHsv.rgbToHsv_F32(p.image, hsv);
        GImageDerivativeOps.gradient(DerivativeType.SOBEL, hsv.getBand(1), derivX, derivY, BorderType.EXTENDED);
        colorizeSign(derivX, 1, p.bitmap, null);
        done();
    }
}
