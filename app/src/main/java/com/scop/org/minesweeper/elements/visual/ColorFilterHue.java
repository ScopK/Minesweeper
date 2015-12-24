package com.scop.org.minesweeper.elements.visual;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;


public class ColorFilterHue {
    /**
     * Creates a HUE ajustment ColorFilter
     * @see http://groups.google.com/group/android-developers/browse_thread/thread/9e215c83c3819953
     * @see http://gskinner.com/blog/archives/2007/12/colormatrix_cla.html
     * @param value degrees to shift the hue.
     * @return
     */
    public static ColorFilter adjustHue(float value, float brightness) {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, value, brightness);
        return new ColorMatrixColorFilter(cm);
    }

    /**
     * @see http://groups.google.com/group/android-developers/browse_thread/thread/9e215c83c3819953
     * @see http://gskinner.com/blog/archives/2007/12/colormatrix_cla.html
     * @param cm
     * @param value
     */
    public static void adjustHue(ColorMatrix cm, float value, float brightness) {
        value = cleanValue(value, 180f) / 180f * (float) Math.PI;
        if (value == 0) {
            return;
        }
        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.2125f*brightness;
        float lumG = 0.7154f*brightness;
        float lumB = 0.0721f*brightness;
        float[] mat = new float[] {
            lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
            lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
            lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        };
        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustHue(Bitmap bm, float value, float brightness) {
        value = cleanValue(value, 180f) / 180f * (float) Math.PI;
        if (value == 0) {
            return;
        }
        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.2125f*brightness;
        float lumG = 0.7154f*brightness;
        float lumB = 0.0721f*brightness;
        float[][] mat = new float[][] {
                {lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0},
                {lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0},
                {lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0},
                {0f, 0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 0f, 1f}
        };

        for (int i=0;i<bm.getWidth();i++){
            for (int j=0;j<bm.getHeight();j++){
                float[][] res = {{0f,0f,0f,0f,0f}};
                float[][] pixel = new float[1][5];

                int pix = bm.getPixel(i,j);
                pixel[0][3] = (pix>>24)/255f;
                pixel[0][0] = (pix>>16 & 0xFF)/255f;
                pixel[0][1] = (pix>>8 & 0xFF)/255f;
                pixel[0][2] = (pix & 0xFF)/255f;
                pixel[0][4] = 1f;

                multiply(pixel,mat,res);

                pix = 0;
                pix += ((int)(res[0][3]*255));
                pix <<= 8;
                pix += ((int)(res[0][0]*255));
                pix <<= 8;
                pix += ((int)(res[0][1]*255));
                pix <<= 8;
                pix += ((int)(res[0][2]*255));

                bm.setPixel(i,j,pix);
            }
        }
    }

    private static float[][] multiply(float[][] A, float[][] B, float[][] C){
        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        for (int j = 0; j < aColumns; j++) { // aRow
            if (C[0][j]>1)
                C[0][j]=1f;
            else if (C[0][j]<-1)
                C[0][j]=-1f;
        }
        return C;
    }

    protected static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }
}
