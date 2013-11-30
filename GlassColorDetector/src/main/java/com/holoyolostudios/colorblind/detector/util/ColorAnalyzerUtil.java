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
        int j = 0;
        int k = 0;
        int m = 0;
        int n = x1;
        if (n >= x2) {
            return new RGBColor(j / i, k / i, m / i);
        }
        for (int i1 = y1; i1 < y2; i1++) {
            int i2 = getColorAtPoint(yuv, n, i1);
            j += Color.red(i2);
            k += Color.green(i2);
            m += Color.blue(i2);
            n++;
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
        int i2 = (int) (j + 1.402D * i1);
        int i3 = (int) (j - 0.344D * n - 0.714D * i1);
        int i4 = (int) (j + 1.772D * n);
        i2 = (i2 < 0) ? 0 : i2;
        i2 = (i2 > 255) ? 255 : i2;
        i3 = (i3 < 0) ? 0 : i3;
        i3 = (i3 > 255) ? 255 : i3;
        i4 = (i4 < 0) ? 0 : i4;
        i4 = (i4 > 255) ? 255 : i4;
        return Color.rgb(i2, i3, i4);
    }

    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data   byte array on YUV420 NV21 format.
     * @param width  pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    public static int[] convertYUV420_NV21toRGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for (int i = 0, k = 0; i < size; i += 2, k += 2) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            u = data[offset + k] & 0xff;
            v = data[offset + k + 1] & 0xff;
            u = u - 128;
            v = v - 128;

            pixels[i] = convertYUVtoRGB(y1, u, v);
            pixels[i + 1] = convertYUVtoRGB(y2, u, v);
            pixels[width + i] = convertYUVtoRGB(y3, u, v);
            pixels[width + i + 1] = convertYUVtoRGB(y4, u, v);

            if (i != 0 && (i + 2) % width == 0)
                i += width;
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r, g, b;

        r = y + (int) 1.402f * v;
        g = y - (int) (0.344f * u + 0.714f * v);
        b = y + (int) 1.772f * u;
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (b << 16) | (g << 8) | r;
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
                mHexCode = String.format("#%02x%02x%02x", getRed(), getGreen(), getBlue());
            }
            return mHexCode;
        }

    }

}
