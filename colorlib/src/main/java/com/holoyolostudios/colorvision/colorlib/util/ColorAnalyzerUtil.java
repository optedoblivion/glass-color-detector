/*
 * Copyright 2014 Martin Brabham
 * Copyright 2014 Daniel Velazco
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.holoyolostudios.colorvision.colorlib.util;

import android.graphics.Color;

/**
 * ColorAnalyzerUtil
 * <p/>
 * Utility to handle detecting and averaging color from YUV data
 * <p/>
 *
 * @author Martin Brabham
 * @author Daniel Velazco
 */
public class ColorAnalyzerUtil {

    // Constants
    public static int FRAME_WIDTH = 640;
    public static int FRAME_HEIGHT = 480;

    /**
     * Get the average color of a rect area of a YUV420SPNV21 byte array
     *
     * @param yuv byte array
     * @param x1  {@link Integer}
     * @param y1  {@link Integer}
     * @param x2  {@link Integer}
     * @param y2  {@link Integer}
     * @return {@link com.holoyolostudios.colorvision.colorlib.util.ColorAnalyzerUtil.RGBColor}
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
     * @param x   {@link Integer}
     * @param y   {@link Integer}
     * @return {@link Integer}
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
     * RGBColor
     * <p/>
     * Represents a color of a pixel
     * <p/>
     *
     * @author Martin Brabham
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
         * @param r {@link Integer}
         * @param g {@link Integer}
         * @param b {@link Integer}
         */
        public RGBColor(int r, int g, int b) {
            this(0xFF, r, g, b);
        }

        /**
         * Constructor
         *
         * @param a {@link Integer}
         * @param r {@link Integer}
         * @param g {@link Integer}
         * @param b {@link Integer}
         */
        public RGBColor(int a, int r, int g, int b) {
            mAlpha = a;
            mRed = r;
            mGreen = g;
            mBlue = b;
        }

        /**
         * Get data of a single pixel
         *
         * @return {@link Integer}
         */
        public int getPixel() {
            return Color.rgb(mRed, mGreen, mBlue);
        }

        /**
         * Get the alpha
         *
         * @return {@link Integer}
         */
        public int getAlpha() {
            return mAlpha;
        }

        /**
         * Get the Red
         *
         * @return {@link Integer}
         */
        public int getRed() {
            return mRed;
        }

        /**
         * Get the Green
         *
         * @return {@link Integer}
         */
        public int getGreen() {
            return mGreen;
        }

        /**
         * Get the Blue
         *
         * @return {@link Integer}
         */
        public int getBlue() {
            return mBlue;
        }

        /**
         * Get the color name
         *
         * @return {@link String}
         */
        public String getName() {
            return mColorName;
        }

        /**
         * Get the hex code representation of the RGB
         *
         * @return {@link String}
         */
        public String getHexCode() {
            if (mHexCode == null) {
                mHexCode = String.format("%06x", getPixel());
            }
            return mHexCode;
        }

    }

}
