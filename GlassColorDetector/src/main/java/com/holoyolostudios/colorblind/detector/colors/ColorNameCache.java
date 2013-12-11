package com.holoyolostudios.colorblind.detector.colors;

import android.util.Log;

import java.util.ArrayList;

/**
 * ColorNameCache singleton
 */
public class ColorNameCache {

    // Constants
    private static final String LOG_TAG = "ColorNameCache";

    // Instance
    private static ColorNameCache mInstance = null;

    // Members
    private boolean mInitialized = false;
    private ArrayList<ColorName> mColorList = new ArrayList<ColorName>();

    /**
     * Private constructor
     */
    private ColorNameCache() {
    }

    /**
     * Create a new instance of {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache}.<br/>
     * Throws an {@link java.lang.IllegalStateException} if an instance has already been initialized.
     *
     * @return {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache}
     */
    public static ColorNameCache createInstance() {
        if (mInstance == null) {
            mInstance = new ColorNameCache();
            mInstance.init();
        }
        return mInstance;
    }

    /**
     * Get an existing {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache} instance.
     * Throws an {@link java.lang.IllegalStateException} if an instance hasn't been created yet.
     *
     * @return {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache}
     */
    public static ColorNameCache getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("A ColorNameCache instance must be created first.");
        }
        return mInstance;
    }

    /**
     * Destroy the {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache} instance
     */
    public void destroy() {
        mInitialized = false;
        mInstance = null;
    }

    /**
     * Check whether or not the {@link com.holoyolostudios.colorblind.detector.colors.ColorNameCache} instance
     * has been initialized.
     *
     * @return {@link boolean}
     */
    public boolean isInitialized() {
        return mInitialized;
    }

    /**
     * Get the name of a color by passing the RGB values as the argument.
     *
     * @param r {@link int}
     * @param g {@link int}
     * @param b {@link int}
     * @return {@link String}
     */
    public String getColorName(int r, int g, int b) {
        if (!mInitialized) {
            throw new IllegalStateException("This instance has not been initialized yet.");
        }

        ColorName closestMatch = null;
        double minMSE = Double.MAX_VALUE;
        double mse;
        for (ColorName c : mColorList) {
            mse = c.computeMSE(r, g, b);
            if (mse < minMSE) {
                minMSE = mse;
                closestMatch = c;
            }
        }

        if (closestMatch != null) {
            return closestMatch.getName();
        } else {
            return null;
        }
    }

    /**
     * Initialize this ColorNameCache. If it was already initialized, it will simply return false;
     *
     * @return {@link boolean}
     */
    public boolean init() {
        if (mInitialized) {
            Log.d(LOG_TAG, "The ColorNameCache has already been initialized");
            return false;
        }

        // Add all the colors to the list
        mColorList.add(new ColorName("Aqua", 0x00, 0xFF, 0xFF));
        mColorList.add(new ColorName("Dark Aqua", 0x00, 0xA1, 0xA3));
        mColorList.add(new ColorName("Aquamarine", 0x7F, 0xFF, 0xD4));
        mColorList.add(new ColorName("Dark Aquamarine", 0x20, 0xB2, 0xAA));
        mColorList.add(new ColorName("Beige", 0xF5, 0xF5, 0xDC));
        mColorList.add(new ColorName("Light Orange", 0xFF, 0xE4, 0xC4));
        mColorList.add(new ColorName("Black", 0x00, 0x00, 0x00));
        mColorList.add(new ColorName("Blue", 0x00, 0x00, 0xFF));
        mColorList.add(new ColorName("Purple", 0x8A, 0x2B, 0xE2));
        mColorList.add(new ColorName("Brown", 0x73, 0x29, 0x229));
        mColorList.add(new ColorName("Light Brown", 0xDE, 0xB0, 0x87));
        mColorList.add(new ColorName("Cadet Blue", 0x5F, 0x9E, 0xA0));
        mColorList.add(new ColorName("Green", 0x7F, 0xFF, 0x00));
        mColorList.add(new ColorName("Dark Orange", 0xD2, 0x69, 0x1E));
        mColorList.add(new ColorName("Light Red", 0xFF, 0x7F, 0x50));
        mColorList.add(new ColorName("Light Blue", 0x64, 0x95, 0xED));
        mColorList.add(new ColorName("Light Yellow", 0xFF, 0xF8, 0xDC));
        mColorList.add(new ColorName("Dark Red", 0xDC, 0x14, 0x3C));
        mColorList.add(new ColorName("Cyan", 0x00, 0xFF, 0xFF));
        mColorList.add(new ColorName("Dark Blue", 0x00, 0x00, 0x8B));
        mColorList.add(new ColorName("Dark Cyan", 0x00, 0x8B, 0x8B));
        mColorList.add(new ColorName("Dark Gold", 0xB8, 0x86, 0x0B));
        mColorList.add(new ColorName("Grey", 0xA9, 0xA9, 0xA9));
        mColorList.add(new ColorName("Dark Grey", 0x69, 0x69, 0x69));
        mColorList.add(new ColorName("Light Grey", 0xDC, 0xDC, 0xDC));
        mColorList.add(new ColorName("Grey", 0x80, 0x80, 0x80));
        mColorList.add(new ColorName("Light Grey", 0xD3, 0xD3, 0xD3));
        mColorList.add(new ColorName("Dark Green", 0x00, 0x64, 0x00));
        mColorList.add(new ColorName("Purple", 0x8B, 0x00, 0x8B));
        mColorList.add(new ColorName("Dark Green", 0x55, 0x6B, 0x2F));
        mColorList.add(new ColorName("Orange", 0xFF, 0x8C, 0x00));
        mColorList.add(new ColorName("Purple", 0x99, 0x32, 0xCC));
        mColorList.add(new ColorName("Dark Red", 0x8B, 0x00, 0x00));
        mColorList.add(new ColorName("Light Red", 0xE9, 0x96, 0x7A));
        mColorList.add(new ColorName("Light Green", 0x8F, 0xBC, 0x8F));
        mColorList.add(new ColorName("Dark Purple", 0x48, 0x3D, 0x8B));
        mColorList.add(new ColorName("Dark Green", 0x2F, 0x4F, 0x4F));
        mColorList.add(new ColorName("Purple", 0x94, 0x00, 0xD3));
        mColorList.add(new ColorName("Pink", 0xFF, 0x14, 0x93));
        mColorList.add(new ColorName("Sky Blue", 0x00, 0xBF, 0xFF));
        mColorList.add(new ColorName("Blue", 0x1E, 0x90, 0xFF));
        mColorList.add(new ColorName("Dark Red", 0xB2, 0x22, 0x22));
        mColorList.add(new ColorName("Green", 0x22, 0x8B, 0x22));
        mColorList.add(new ColorName("Fuchsia", 0xFF, 0x00, 0xFF));
        mColorList.add(new ColorName("Gold", 0xFF, 0xD7, 0x00));
        mColorList.add(new ColorName("Light Orange", 0xDA, 0xA5, 0x20));
        mColorList.add(new ColorName("Green", 0x00, 0x80, 0x00));
        mColorList.add(new ColorName("Light Green", 0xAD, 0xFF, 0x2F));
        mColorList.add(new ColorName("Pink", 0xFF, 0x69, 0xB4));
        mColorList.add(new ColorName("Dark Red", 0xBA, 0x54E, 0x4E));
        mColorList.add(new ColorName("Purple", 0x4B, 0x00, 0x82));
        mColorList.add(new ColorName("Green", 0x7C, 0xFC, 0x00));
        mColorList.add(new ColorName("Light Blue", 0xAD, 0xD8, 0xE6));
        mColorList.add(new ColorName("Light Red", 0xF0, 0x80, 0x80));
        mColorList.add(new ColorName("Light Red", 0xFF, 0xB6, 0xC1));
        mColorList.add(new ColorName("Light Red", 0xFF, 0xA0, 0x7A));
        mColorList.add(new ColorName("Light Blue", 0x87, 0xCE, 0xFA));
        mColorList.add(new ColorName("Green", 0x00, 0xFF, 0x00));
        mColorList.add(new ColorName("Green", 0x32, 0xCD, 0x32));
        mColorList.add(new ColorName("Magenta", 0xFF, 0x00, 0xFF));
        mColorList.add(new ColorName("Dark Red", 0x80, 0x00, 0x00));
        mColorList.add(new ColorName("Dark Blue", 0x00, 0x00, 0xCD));
        mColorList.add(new ColorName("Pink", 0xBA, 0x55, 0xD3));
        mColorList.add(new ColorName("Purple", 0x93, 0x70, 0xDB));
        mColorList.add(new ColorName("Green", 0x3C, 0xB3, 0x71));
        mColorList.add(new ColorName("Purple", 0x7B, 0x68, 0xEE));
        mColorList.add(new ColorName("Light Green", 0x00, 0xFA, 0x9A));
        mColorList.add(new ColorName("Dark Pink", 0xC7, 0x15, 0x85));
        mColorList.add(new ColorName("Dark Blue", 0x19, 0x19, 0x70));
        mColorList.add(new ColorName("Dark Blue", 0x00, 0x00, 0x80));
        mColorList.add(new ColorName("Olive", 0x80, 0x80, 0x00));
        mColorList.add(new ColorName("Orange", 0xFF, 0xA5, 0x00));
        mColorList.add(new ColorName("Orange", 0xFF, 0x45, 0x00));
        mColorList.add(new ColorName("Light Purple", 0xDA, 0x70, 0xD6));
        mColorList.add(new ColorName("Light Green", 0x98, 0xFB, 0x98));
        mColorList.add(new ColorName("Light Orange", 0xCD, 0x85, 0x3F));
        mColorList.add(new ColorName("Light Purple", 0xDD, 0xA0, 0xDD));
        mColorList.add(new ColorName("Purple", 0x80, 0x00, 0x80));
        mColorList.add(new ColorName("Red", 0xFF, 0x00, 0x00));
        mColorList.add(new ColorName("Green", 0x2E, 0x8B, 0x57));
        mColorList.add(new ColorName("Dark Orange", 0xA0, 0x52, 0x2D));
        mColorList.add(new ColorName("Grey", 0xC0, 0xC0, 0xC0));
        mColorList.add(new ColorName("Light Blue", 0x87, 0xCE, 0xEB));
        mColorList.add(new ColorName("Purple", 0x6A, 0x5A, 0xCD));
        mColorList.add(new ColorName("Green", 0x00, 0xFF, 0x7F));
        mColorList.add(new ColorName("Light Orange", 0xD2, 0xB4, 0x8C));
        mColorList.add(new ColorName("Dark Green", 0x00, 0x80, 0x80));
        mColorList.add(new ColorName("Orange", 0xFF, 0x63, 0x47));
        mColorList.add(new ColorName("Pink", 0xEE, 0x82, 0xEE));
        mColorList.add(new ColorName("White", 0xFF, 0xFF, 0xFF));
        mColorList.add(new ColorName("Yellow", 0xFF, 0xFF, 0x00));
        mColorList.add(new ColorName("Light Green", 0x9A, 0xCD, 0x32));

        mInitialized = true;
        return true;
    }

    /**
     * ColorName object to link RGB colors to a name
     */
    private class ColorName {

        // Members
        public String mmColorName;
        private int r, g, b;

        /**
         * Constructor
         *
         * @param name {@link String}
         * @param r {@link int}
         * @param g {@link int}
         * @param b {@link int}
         */
        public ColorName(String name, int r, int g, int b) {
            this.mmColorName = name;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        /**
         * Get the name of the color
         *
         * @return {@link String}
         */
        public String getName() {
            return mmColorName;
        }

        /**
         * Get the RED value of this color.
         *
         * @return {@link int}
         */
        public int getR() {
            return r;
        }

        /**
         * Get the GREEN value of this color.
         *
         * @return {@link int}
         */
        public int getG() {
            return g;
        }

        /**
         * Get the BLUE value of this color.
         *
         * @return {@link int}
         */
        public int getB() {
            return b;
        }

        /**
         * Compute the color to get the proximity to the RGB color passed in the argument
         *
         * @param pixR {@link int}
         * @param pixG {@link int}
         * @param pixB {@link int}
         *
         * @return {@link int} proximity
         */
        public double computeMSE(int pixR, int pixG, int pixB) {
            return ((((pixR - r) * (pixR - r)) + ((pixG - g) * (pixG - g)) + ((pixB - b) * (pixB - b))) / 3);
        }

        /**
         * Compute the color to get the proximity to the RGB color passed in the argument
         *
         * @param color {@link ColorName}
         * @return {@link int} proximity
         */
        public double computeMSE(ColorName color) {
            return computeMSE(color.getR(), color.getG(), color.getB());
        }

    }

}
