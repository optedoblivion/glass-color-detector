package com.holoyolostudios.colorblind.detector.util;

import android.graphics.Color;

/**
 * ColorAnalyzerUtil
 */
public class ColorAnalyzerUtil {

    public static int FRAME_WIDTH = 640;
    public static int FRAME_HEIGHT = 480;

    /**
     * Get the average color of a rect area of a YUV420SPNV21 byte array
     *
     * @param yuv byte array
     * @param x1  {@link java.lang.Integer}
     * @param y1  {@link java.lang.Integer}
     * @param x2  {@link java.lang.Integer}
     * @param y2  {@link java.lang.Integer}
     * @return {@link com.holoyolostudios.colorblind.detector.util.ColorAnalyzerUtil.RGBColor}
     */
    public static RGBColor getAverageColor(byte[] yuv, int x1, int y1, int x2, int y2) {

        // Set variables
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;

        // Gather pixel data for the square
        for (int i1 = x1; i1 <= x2; i1++) {
            for (int i2 = y1; i2 < y2; i2++) {
                int color = getColorAtPoint(yuv, i1, i2);
                j += Color.red(color);
                k += Color.green(color);
                m += Color.blue(color);
                i++;
            }
        }

        // Average data
        j = j / i;
        k = k / i;
        m = m / i;

        // Normalize data
        j = (j > 255) ? 255 : j;
        j = (j < 0) ? 0 : j;
        k = (k > 255) ? 255 : k;
        k = (k < 0) ? 0 : k;
        m = (m > 255) ? 255 : m;
        m = (m < 0) ? 0 : m;

        return new RGBColor(j, k, m);
    }

    /**
     * Gets the RGB pixel at the given position in a YUV420SPNV21 byte array
     *
     * @param yuv byte array
     * @param x   {@link java.lang.Integer}
     * @param y   {@link java.lang.Integer}
     * @return {@link java.lang.Integer}
     */
    public static int getColorAtPoint(byte[] yuv, int x, int y) {
        int i = (FRAME_WIDTH * FRAME_HEIGHT) + FRAME_WIDTH * (y >> 1) + (x & 0xFFFFFFFE);
        int j = 0xFF & yuv[x + y * FRAME_WIDTH];
        int k = 0xFF & yuv[(i + 1)];
        int m = 0xFF & yuv[i];
        int n = k - 128;
        int i1 = m - 128;
        int i2 = (int) (j + 1.402f * i1);
        int i3 = (int) (j - 0.344f * n - 0.714f * i1);
        int i4 = (int) (j + 1.772f * n);
        i2 = (i2 < 0) ? 0 : i2;
        i2 = (i2 > 255) ? 255 : i2;
        i3 = (i3 < 0) ? 0 : i3;
        i3 = (i3 > 255) ? 255 : i3;
        i4 = (i4 < 0) ? 0 : i4;
        i4 = (i4 > 255) ? 255 : i4;
        return Color.rgb(i2, i3, i4);
    }

    /**
     * A color representation
     */
    public static class RGBColor {

        // Members
        private int mAlpha = 0xFF000000;
        private int mRed = 0x00000000;
        private int mGreen = 0x00000000;
        private int mBlue = 0x00000000;
        private String mColorName = null;
        private String mHexCode = null;

        /**
         * Constructor
         *
         * @param r {@link java.lang.Integer}
         * @param g {@link java.lang.Integer}
         * @param b {@link java.lang.Integer}
         */
        public RGBColor(int r, int g, int b) {
            this(0xFF, r, g, b);
        }

        /**
         * Constructor
         *
         * @param a {@link java.lang.Integer}
         * @param r {@link java.lang.Integer}
         * @param g {@link java.lang.Integer}
         * @param b {@link java.lang.Integer}
         */
        public RGBColor(int a, int r, int g, int b) {
            mAlpha = a;
            mRed = r;
            mGreen = g;
            mBlue = b;
        }

        public int getPixel() {
            return Color.rgb(mRed, mGreen, mBlue);
        }

        public int getAlpha() {
            return mAlpha;
        }

        public int getRed() {
            return mRed;
        }

        public int getGreen() {
            return mGreen;
        }

        public int getBlue() {
            return mBlue;
        }

        public String getName() {
            return mColorName;
        }

        public String getHexCode() {
            if (mHexCode == null) {
                mHexCode = String.format("%06x", getPixel());
            }
            return mHexCode;
        }

    }

}
