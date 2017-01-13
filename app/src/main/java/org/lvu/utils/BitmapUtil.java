package org.lvu.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by wuyr on 1/13/17 3:35 PM.
 */

public class BitmapUtil {
    public static Bitmap compress(Bitmap bm) {
        int w = bm.getWidth(), h = bm.getHeight();
        Matrix matrix = new Matrix();
        float scale = 100f / w;//100f represent target width
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
    }
}
