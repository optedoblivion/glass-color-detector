package com.holoyolostudios.colorblind.detector.colors;

import android.util.Log;

import java.util.ArrayList;

/**
 * ColorNameCache singleton
 *
 *
 * // TODO: the color matching is not working well, too many colors.
 * // TODO: May need to create buckets of colors or something.
 * // TODO: look at these... http://web.njit.edu/~kevin/rgb.txt.html
 *
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
        int minMSE = Integer.MAX_VALUE;
        int mse;
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
//        mColorList.add(new ColorName("Alice Blue", 0xF0, 0xF8, 0xFF));
//        mColorList.add(new ColorName("Antique White", 0xFA, 0xEB, 0xD7));
        mColorList.add(new ColorName("Aqua", 0x00, 0xFF, 0xFF));
        mColorList.add(new ColorName("Aquamarine", 0x7F, 0xFF, 0xD4));
//        mColorList.add(new ColorName("Azure", 0xF0, 0xFF, 0xFF));
        mColorList.add(new ColorName("Beige", 0xF5, 0xF5, 0xDC));
//        mColorList.add(new ColorName("Bisque", 0xFF, 0xE4, 0xC4));
        mColorList.add(new ColorName("Black", 0x00, 0x00, 0x00));
//        mColorList.add(new ColorName("Blanched Almond", 0xFF, 0xEB, 0xCD));
        mColorList.add(new ColorName("Blue", 0x00, 0x00, 0xFF));
//        mColorList.add(new ColorName("Blue Violet", 0x8A, 0x2B, 0xE2));
        mColorList.add(new ColorName("Brown", 0xA5, 0x2A, 0x2A));
//        mColorList.add(new ColorName("Burly Wood", 0xDE, 0xB8, 0x87));
//        mColorList.add(new ColorName("Cadet Blue", 0x5F, 0x9E, 0xA0));
//        mColorList.add(new ColorName("Chartreuse", 0x7F, 0xFF, 0x00));
//        mColorList.add(new ColorName("Chocolate", 0xD2, 0x69, 0x1E));
//        mColorList.add(new ColorName("Coral", 0xFF, 0x7F, 0x50));
//        mColorList.add(new ColorName("Corn-flower Blue", 0x64, 0x95, 0xED));
//        mColorList.add(new ColorName("Cornsilk", 0xFF, 0xF8, 0xDC));
//        mColorList.add(new ColorName("Crimson", 0xDC, 0x14, 0x3C));
        mColorList.add(new ColorName("Cyan", 0x00, 0xFF, 0xFF));
        mColorList.add(new ColorName("Dark Blue", 0x00, 0x00, 0x8B));
        mColorList.add(new ColorName("Dark Cyan", 0x00, 0x8B, 0x8B));
//        mColorList.add(new ColorName("Dark Golden Rod", 0xB8, 0x86, 0x0B));
        mColorList.add(new ColorName("Dark Gray", 0xA9, 0xA9, 0xA9));
        mColorList.add(new ColorName("Dark Green", 0x00, 0x64, 0x00));
//        mColorList.add(new ColorName("Dark Khaki", 0xBD, 0xB7, 0x6B));
//        mColorList.add(new ColorName("Dark Magenta", 0x8B, 0x00, 0x8B));
//        mColorList.add(new ColorName("Dark Olive Green", 0x55, 0x6B, 0x2F));
        mColorList.add(new ColorName("Dark Orange", 0xFF, 0x8C, 0x00));
        mColorList.add(new ColorName("Dark Orchid", 0x99, 0x32, 0xCC));
        mColorList.add(new ColorName("Dark Red", 0x8B, 0x00, 0x00));
//        mColorList.add(new ColorName("Dark Salmon", 0xE9, 0x96, 0x7A));
        mColorList.add(new ColorName("Dark Sea Green", 0x8F, 0xBC, 0x8F));
//        mColorList.add(new ColorName("Dark Slate Blue", 0x48, 0x3D, 0x8B));
//        mColorList.add(new ColorName("Dark Slate Gray", 0x2F, 0x4F, 0x4F));
//        mColorList.add(new ColorName("Dark Turquoise", 0x00, 0xCE, 0xD1));
//        mColorList.add(new ColorName("Dark Violet", 0x94, 0x00, 0xD3));
//        mColorList.add(new ColorName("Deep Pink", 0xFF, 0x14, 0x93));
        mColorList.add(new ColorName("Deep Sky Blue", 0x00, 0xBF, 0xFF));
        mColorList.add(new ColorName("Dim Gray", 0x69, 0x69, 0x69));
//        mColorList.add(new ColorName("Dodger Blue", 0x1E, 0x90, 0xFF));
//        mColorList.add(new ColorName("Fire Brick", 0xB2, 0x22, 0x22));
//        mColorList.add(new ColorName("Floral White", 0xFF, 0xFA, 0xF0));
//        mColorList.add(new ColorName("Forest Green", 0x22, 0x8B, 0x22));
        mColorList.add(new ColorName("Fuchsia", 0xFF, 0x00, 0xFF));
//        mColorList.add(new ColorName("Gainsboro", 0xDC, 0xDC, 0xDC));
//        mColorList.add(new ColorName("Ghost White", 0xF8, 0xF8, 0xFF));
        mColorList.add(new ColorName("Gold", 0xFF, 0xD7, 0x00));
//        mColorList.add(new ColorName("Golden Rod", 0xDA, 0xA5, 0x20));
        mColorList.add(new ColorName("Gray", 0x80, 0x80, 0x80));
        mColorList.add(new ColorName("Green", 0x00, 0x80, 0x00));
//        mColorList.add(new ColorName("Green Yellow", 0xAD, 0xFF, 0x2F));
//        mColorList.add(new ColorName("Honey Dew", 0xF0, 0xFF, 0xF0));
        mColorList.add(new ColorName("Hot Pink", 0xFF, 0x69, 0xB4));
        mColorList.add(new ColorName("Indian Red", 0xCD, 0x5C, 0x5C));
        mColorList.add(new ColorName("Indigo", 0x4B, 0x00, 0x82));
        mColorList.add(new ColorName("Ivory", 0xFF, 0xFF, 0xF0));
        mColorList.add(new ColorName("Khaki", 0xF0, 0xE6, 0x8C));
//        mColorList.add(new ColorName("Lavender", 0xE6, 0xE6, 0xFA));
//        mColorList.add(new ColorName("Lavender Blush", 0xFF, 0xF0, 0xF5));
//        mColorList.add(new ColorName("Lawn Green", 0x7C, 0xFC, 0x00));
//        mColorList.add(new ColorName("Lemon Chiffon", 0xFF, 0xFA, 0xCD));
        mColorList.add(new ColorName("Light Blue", 0xAD, 0xD8, 0xE6));
        mColorList.add(new ColorName("Light Coral", 0xF0, 0x80, 0x80));
        mColorList.add(new ColorName("Light Cyan", 0xE0, 0xFF, 0xFF));
//        mColorList.add(new ColorName("Light Golden Rod Yellow", 0xFA, 0xFA, 0xD2));
        mColorList.add(new ColorName("Light Gray", 0xD3, 0xD3, 0xD3));
        mColorList.add(new ColorName("Light Green", 0x90, 0xEE, 0x90));
        mColorList.add(new ColorName("Light Pink", 0xFF, 0xB6, 0xC1));
//        mColorList.add(new ColorName("Light Salmon", 0xFF, 0xA0, 0x7A));
//        mColorList.add(new ColorName("Light Sea Green", 0x20, 0xB2, 0xAA));
//        mColorList.add(new ColorName("Light Sky Blue", 0x87, 0xCE, 0xFA));
//        mColorList.add(new ColorName("Light Slate Gray", 0x77, 0x88, 0x99));
//        mColorList.add(new ColorName("Light Steel Blue", 0xB0, 0xC4, 0xDE));
//        mColorList.add(new ColorName("Light Yellow", 0xFF, 0xFF, 0xE0));
        mColorList.add(new ColorName("Lime", 0x00, 0xFF, 0x00));
//        mColorList.add(new ColorName("Lime Green", 0x32, 0xCD, 0x32));
//        mColorList.add(new ColorName("Linen", 0xFA, 0xF0, 0xE6));
        mColorList.add(new ColorName("Magenta", 0xFF, 0x00, 0xFF));
//        mColorList.add(new ColorName("Maroon", 0x80, 0x00, 0x00));
//        mColorList.add(new ColorName("Medium Aqua Marine", 0x66, 0xCD, 0xAA));
//        mColorList.add(new ColorName("Medium Blue", 0x00, 0x00, 0xCD));
//        mColorList.add(new ColorName("Medium Orchid", 0xBA, 0x55, 0xD3));
//        mColorList.add(new ColorName("Medium Purple", 0x93, 0x70, 0xDB));
//        mColorList.add(new ColorName("Medium Sea Green", 0x3C, 0xB3, 0x71));
//        mColorList.add(new ColorName("Medium Slate Blue", 0x7B, 0x68, 0xEE));
//        mColorList.add(new ColorName("Medium Spring Green", 0x00, 0xFA, 0x9A));
//        mColorList.add(new ColorName("Medium Turquoise", 0x48, 0xD1, 0xCC));
//        mColorList.add(new ColorName("Medium Violet Red", 0xC7, 0x15, 0x85));
//        mColorList.add(new ColorName("Midnight Blue", 0x19, 0x19, 0x70));
//        mColorList.add(new ColorName("Mint Cream", 0xF5, 0xFF, 0xFA));
//        mColorList.add(new ColorName("Misty Rose", 0xFF, 0xE4, 0xE1));
//        mColorList.add(new ColorName("Moccasin", 0xFF, 0xE4, 0xB5));
//        mColorList.add(new ColorName("Navajo White", 0xFF, 0xDE, 0xAD));
        mColorList.add(new ColorName("Navy", 0x00, 0x00, 0x80));
//        mColorList.add(new ColorName("Old Lace", 0xFD, 0xF5, 0xE6));
        mColorList.add(new ColorName("Olive", 0x80, 0x80, 0x00));
//        mColorList.add(new ColorName("Olive Drab", 0x6B, 0x8E, 0x23));
        mColorList.add(new ColorName("Orange", 0xFF, 0xA5, 0x00));
        mColorList.add(new ColorName("Orange Red", 0xFF, 0x45, 0x00));
        mColorList.add(new ColorName("Orchid", 0xDA, 0x70, 0xD6));
//        mColorList.add(new ColorName("Pale Golden Rod", 0xEE, 0xE8, 0xAA));
//        mColorList.add(new ColorName("Pale Green", 0x98, 0xFB, 0x98));
//        mColorList.add(new ColorName("Pale Turquoise", 0xAF, 0xEE, 0xEE));
//        mColorList.add(new ColorName("Pale Violet Red", 0xDB, 0x70, 0x93));
//        mColorList.add(new ColorName("Papaya Whip", 0xFF, 0xEF, 0xD5));
//        mColorList.add(new ColorName("Peach Puff", 0xFF, 0xDA, 0xB9));
//        mColorList.add(new ColorName("Peru", 0xCD, 0x85, 0x3F));
        mColorList.add(new ColorName("Pink", 0xFF, 0xC0, 0xCB));
        mColorList.add(new ColorName("Plum", 0xDD, 0xA0, 0xDD));
//        mColorList.add(new ColorName("Powder Blue", 0xB0, 0xE0, 0xE6));
        mColorList.add(new ColorName("Purple", 0x80, 0x00, 0x80));
        mColorList.add(new ColorName("Red", 0xFF, 0x00, 0x00));
//        mColorList.add(new ColorName("Rosy Brown", 0xBC, 0x8F, 0x8F));
        mColorList.add(new ColorName("Royal Blue", 0x41, 0x69, 0xE1));
//        mColorList.add(new ColorName("Saddle Brown", 0x8B, 0x45, 0x13));
//        mColorList.add(new ColorName("Salmon", 0xFA, 0x80, 0x72));
//        mColorList.add(new ColorName("Sandy Brown", 0xF4, 0xA4, 0x60));
//        mColorList.add(new ColorName("Sea Green", 0x2E, 0x8B, 0x57));
//        mColorList.add(new ColorName("Sea Shell", 0xFF, 0xF5, 0xEE));
//        mColorList.add(new ColorName("Sienna", 0xA0, 0x52, 0x2D));
//        mColorList.add(new ColorName("Silver", 0xC0, 0xC0, 0xC0));
        mColorList.add(new ColorName("Sky Blue", 0x87, 0xCE, 0xEB));
//        mColorList.add(new ColorName("Slate Blue", 0x6A, 0x5A, 0xCD));
//        mColorList.add(new ColorName("Slate Gray", 0x70, 0x80, 0x90));
//        mColorList.add(new ColorName("Snow", 0xFF, 0xFA, 0xFA));
//        mColorList.add(new ColorName("Spring Green", 0x00, 0xFF, 0x7F));
//        mColorList.add(new ColorName("Steel Blue", 0x46, 0x82, 0xB4));
        mColorList.add(new ColorName("Tan", 0xD2, 0xB4, 0x8C));
        mColorList.add(new ColorName("Teal", 0x00, 0x80, 0x80));
//        mColorList.add(new ColorName("Thistle", 0xD8, 0xBF, 0xD8));
//        mColorList.add(new ColorName("Tomato", 0xFF, 0x63, 0x47));
        mColorList.add(new ColorName("Turquoise", 0x40, 0xE0, 0xD0));
        mColorList.add(new ColorName("Violet", 0xEE, 0x82, 0xEE));
//        mColorList.add(new ColorName("Wheat", 0xF5, 0xDE, 0xB3));
        mColorList.add(new ColorName("White", 0xFF, 0xFF, 0xFF));
//        mColorList.add(new ColorName("White Smoke", 0xF5, 0xF5, 0xF5));
        mColorList.add(new ColorName("Yellow", 0xFF, 0xFF, 0x00));
//        mColorList.add(new ColorName("Yellow Green", 0x9A, 0xCD, 0x32));

        mInitialized = true;
        return true;
    }

    /**
     * ColorName object to link RGB colors to a name
     */
    private class ColorName {

        // Members
        public int r, g, b;
        public String mColorName;

        /**
         * Constructor
         *
         * @param name {@link String}
         * @param r {@link int}
         * @param g {@link int}
         * @param b {@link int}
         */
        public ColorName(String name, int r, int g, int b) {
            this.mColorName = name;
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
            return mColorName;
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
        public int computeMSE(int pixR, int pixG, int pixB) {
            return (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b) * (pixB - b)) / 3);
        }

        /**
         * Compute the color to get the proximity to the RGB color passed in the argument
         *
         * @param color {@link ColorName}
         * @return {@link int} proximity
         */
        public int computeMSE(ColorName color) {
            return computeMSE(color.getR(), color.getG(), color.getB());
        }

    }

}
