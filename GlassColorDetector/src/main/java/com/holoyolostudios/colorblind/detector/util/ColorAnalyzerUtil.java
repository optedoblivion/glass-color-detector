package com.holoyolostudios.colorblind.detector.util;

import android.graphics.Color;

/**
 * ColorAnalyzerUtil
 *
 * @author Martin Brabham
 * @since 11/26/13
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
        int i = (x2 - x1) * (y2 - y1);
        i /= 10;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = x1;

        for (int i1 = y1; i1 < y2; i1++) {
            int i2 = getColorAtPoint(yuv, n, i1);
            j += Color.red(i2);
            k += Color.green(i2);
            m += Color.blue(i2);
            if (n >= x2) {
                return new RGBColor(j / i, k / i, m / i);
            } else {
                n++;
            }
        }

        return new RGBColor(j / i, k / i, m / i);
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
            return Color.argb(mAlpha, mRed, mGreen, mBlue);
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
                mHexCode = String.format("#%06x", getPixel());
            }
            return mHexCode;
        }

    }

}
