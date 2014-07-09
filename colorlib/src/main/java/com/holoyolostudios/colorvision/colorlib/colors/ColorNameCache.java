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
package com.holoyolostudios.colorvision.colorlib.colors;

import android.util.Log;

import java.util.ArrayList;

/**
 * ColorNameCache
 * <p/>
 * Singleton instance for deriving human readable color names from provided RGB values
 * <p/>
 *
 * @author Daniel Velazco
 * @author Martin Brabham
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
     * Create a new instance of {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache}.<br/>
     * Throws an {@link IllegalStateException} if an instance has already been initialized.
     *
     * @return {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache}
     */
    public static ColorNameCache createInstance() {
        if (mInstance == null) {
            mInstance = new ColorNameCache();
            mInstance.init();
        }
        return mInstance;
    }

    /**
     * Get an existing {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache} instance.
     * Throws an {@link IllegalStateException} if an instance hasn't been created yet.
     *
     * @return {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache}
     */
    public static ColorNameCache getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("A ColorNameCache instance must be created first.");
        }
        return mInstance;
    }

    /**
     * Destroy the {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache} instance
     */
    public void destroy() {
        mInitialized = false;
        mInstance = null;
    }

    /**
     * Check whether or not the {@link com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache} instance
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
            return closestMatch.getName() + " (" + closestMatch.getShade() + ")";
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

        mColorList.add(new ColorName("white", "white", 0xff, 0xff, 0xff));
        mColorList.add(new ColorName("white", "white smoke", 0xf5, 0xf5, 0xf5));
        mColorList.add(new ColorName("white", "ghost white", 0xf8, 0xf8, 0xff));
        mColorList.add(new ColorName("white", "baby powder", 0xfe, 0xfe, 0xfa));
        mColorList.add(new ColorName("white", "snow", 0xff, 0xfa, 0xfa));
        mColorList.add(new ColorName("white", "ivory", 0xff, 0xff, 0xf0));
        mColorList.add(new ColorName("white", "floral white", 0xff, 0xfa, 0xf0));
        mColorList.add(new ColorName("white", "seashell", 0xff, 0xf5, 0xee));
        mColorList.add(new ColorName("white", "cornsilk", 0xff, 0xf8, 0xdc));
        mColorList.add(new ColorName("white", "old lace", 0xfd, 0xf5, 0xe6));
        mColorList.add(new ColorName("white", "cream", 0xff, 0xfd, 0xd0));
        mColorList.add(new ColorName("white", "beige", 0xf5, 0xf5, 0xdc));
        mColorList.add(new ColorName("white", "linen", 0xfa, 0xf0, 0xe6));
        mColorList.add(new ColorName("white", "antique white", 0xfa, 0xeb, 0xd7));
        mColorList.add(new ColorName("white", "champagne", 0xf7, 0xe7, 0xc3));
        mColorList.add(new ColorName("white", "eggshell", 0xf0, 0xea, 0xd6));
        mColorList.add(new ColorName("white", "dutch white", 0xef, 0xdf, 0xbb));
        mColorList.add(new ColorName("white", "bone", 0xe3, 0xda, 0xc9));
        mColorList.add(new ColorName("white", "vanilla", 0xf3, 0xe5, 0xab));
        mColorList.add(new ColorName("white", "flax", 0xee, 0xdc, 0x82));
        mColorList.add(new ColorName("white", "navajo white", 0xff, 0xde, 0xad));
        mColorList.add(new ColorName("white", "ecru", 0xc2, 0xb2, 0x80));
        mColorList.add(new ColorName("black", "black", 0x00, 0x00, 0x00));
        mColorList.add(new ColorName("black", "midnight blue", 0x19, 0x19, 0x70));
        mColorList.add(new ColorName("black", "ebony", 0x55, 0x5d, 0x50));
        mColorList.add(new ColorName("black", "taupe", 0x48, 0x3c, 0x32));
        mColorList.add(new ColorName("black", "charcoal", 0x36, 0x45, 0x4f));
        mColorList.add(new ColorName("black", "outer space", 0x41, 0x4a, 0x4c));
        mColorList.add(new ColorName("black", "cafe noir", 0x4b, 0x36, 0x21));
        mColorList.add(new ColorName("black", "black bean", 0x3d, 0x0c, 0x02));
        mColorList.add(new ColorName("black", "black olive", 0x3b, 0x3c, 0x36));
        mColorList.add(new ColorName("black", "onyx", 0x35, 0x38, 0x39));
        mColorList.add(new ColorName("black", "phthalo green", 0x12, 0x35, 0x24));
        mColorList.add(new ColorName("black", "jet", 0x34, 0x34, 0x34));
        mColorList.add(new ColorName("black", "black leather jacket", 0x25, 0x35, 0x29));
        mColorList.add(new ColorName("black", "charleston green", 0x23, 0x2b, 0x2b));
        mColorList.add(new ColorName("black", "eerie black", 0x1b, 0x1b, 0x1b));
        mColorList.add(new ColorName("black", "licorice", 0x1a, 0x11, 0x10));
        mColorList.add(new ColorName("gray", "dark slate gray", 0x2f, 0x4f, 0x4f));
        mColorList.add(new ColorName("gray", "light slate gray", 0x77, 0x88, 0x99));
        mColorList.add(new ColorName("achromatic gray", "gainsboro", 0xdc, 0xdc, 0xdc));
        mColorList.add(new ColorName("achromatic gray", "light gray", 0xd3, 0xd3, 0xd3));
        mColorList.add(new ColorName("achromatic gray", "silver", 0xc0, 0xc0, 0xc0));
        mColorList.add(new ColorName("achromatic gray", "medium gray", 0xbe, 0xbe, 0xbe));
        mColorList.add(new ColorName("achromatic gray", "dark medium gray", 0xa9, 0xa9, 0xa9));
        mColorList.add(new ColorName("achromatic gray", "spanish gray", 0x98, 0x98, 0x98));
        mColorList.add(new ColorName("achromatic gray", "gray", 0x80, 0x80, 0x80));
        mColorList.add(new ColorName("achromatic gray", "dim gray", 0x69, 0x69, 0x69));
        mColorList.add(new ColorName("achromatic gray", "davy's gray", 0x55, 0x55, 0x55));
        mColorList.add(new ColorName("off gray", "platinum", 0xb2, 0xbe, 0xb5));
        mColorList.add(new ColorName("off gray", "battleship gray", 0x84, 0x84, 0x82));
        mColorList.add(new ColorName("cool gray", "cool gray", 0x8c, 0x92, 0xac));
        mColorList.add(new ColorName("cool gray", "cadet gray", 0x91, 0xa3, 0xb0));
        mColorList.add(new ColorName("cool gray", "blue-gray", 0x66, 0x99, 0xcc));
        mColorList.add(new ColorName("cool gray", "glaucos", 0x60, 0x82, 0xb6));
        mColorList.add(new ColorName("cool gray", "slate gray", 0x70, 0x80, 0x90));
        mColorList.add(new ColorName("warm gray", "puce", 0x72, 0x2f, 0x37));
        mColorList.add(new ColorName("warm gray", "rose quartz", 0xaa, 0x98, 0xa9));
        mColorList.add(new ColorName("warm gray", "cinereous", 0x98, 0x81, 0x7b));
        mColorList.add(new ColorName("warm gray", "rocket metallic", 0x8a, 0x7f, 0x8d));
        mColorList.add(new ColorName("pink", "coral", 0xff, 0x7f, 0x50));
        mColorList.add(new ColorName("pink", "dark magenta", 0x8b, 0x00, 0x8b));
        mColorList.add(new ColorName("pink", "dark orchid", 0x99, 0x32, 0xcc));
        mColorList.add(new ColorName("pink", "dark salmon", 0xe9, 0x96, 0x7a));
        mColorList.add(new ColorName("pink", "deep pink", 0xff, 0x14, 0x93));
        mColorList.add(new ColorName("pink", "fuchsia", 0xff, 0x00, 0xff));
        mColorList.add(new ColorName("pink", "light coral", 0xf0, 0x80, 0x80));
        mColorList.add(new ColorName("pink", "light pink", 0xff, 0xb6, 0xc1));
        mColorList.add(new ColorName("pink", "light salmon", 0xff, 0xa0, 0x7a));
        mColorList.add(new ColorName("pink", "medium orchid", 0xba, 0x55, 0xd3));
        mColorList.add(new ColorName("pink", "orchid", 0xda, 0x70, 0xd6));
        mColorList.add(new ColorName("pink", "peach puff", 0xff, 0xda, 0xb9));
        mColorList.add(new ColorName("pink", "salmon", 0xfa, 0x80, 0x72));
        mColorList.add(new ColorName("pink", "hot magenta", 0xff, 0x1d, 0xce));
        mColorList.add(new ColorName("pink", "pink", 0xff, 0xc0, 0xcb));
        mColorList.add(new ColorName("pink", "hot pink", 0xff, 0x69, 0xb4));
        mColorList.add(new ColorName("pink", "deep pink", 0xff, 0x69, 0xb4));
        mColorList.add(new ColorName("pink", "champagne pink", 0xf1, 0xdd, 0xcf));
        mColorList.add(new ColorName("pink", "pink lace", 0xff, 0xdd, 0xf4));
        mColorList.add(new ColorName("pink", "piggy pink", 0xfd, 0xdd, 0xe6));
        mColorList.add(new ColorName("pink", "pale pink", 0xf9, 0xcc, 0xca));
        mColorList.add(new ColorName("pink", "baby pink", 0xf3, 0xc2, 0xc2));
        mColorList.add(new ColorName("pink", "spanish pink", 0xf7, 0xbf, 0xbe));
        mColorList.add(new ColorName("pink", "cameo pink", 0xef, 0xbb, 0xcc));
        mColorList.add(new ColorName("pink", "orchid pink", 0xf2, 0xbd, 0xcd));
        mColorList.add(new ColorName("pink", "cherry blossom pink", 0xff, 0xb7, 0xc5));
        mColorList.add(new ColorName("pink", "light hot pink", 0xff, 0xb3, 0xde));
        mColorList.add(new ColorName("pink", "lavendar pink", 0xfb, 0xae, 0xd2));
        mColorList.add(new ColorName("pink", "cotton candy", 0xff, 0xbc, 0xd9));
        mColorList.add(new ColorName("pink", "carnation pink", 0xff, 0xa6, 0xc9));
        mColorList.add(new ColorName("pink", "baker-miller pink", 0xff, 0x91, 0xaf));
        mColorList.add(new ColorName("pink", "tickle me pink", 0xfc, 0x89, 0xac));
        mColorList.add(new ColorName("pink", "amaranth pink", 0xf1, 0x9c, 0xbb));
        mColorList.add(new ColorName("pink", "charm pink", 0xe6, 0x8f, 0xac));
        mColorList.add(new ColorName("pink", "china pink", 0xde, 0x6f, 0xa1));
        mColorList.add(new ColorName("pink", "tango pink", 0xe4, 0x71, 0x7a));
        mColorList.add(new ColorName("pink", "congo pink", 0xf8, 0x83, 0x79));
        mColorList.add(new ColorName("pink", "pastel pink", 0xde, 0xa5, 0xa4));
        mColorList.add(new ColorName("pink", "new york pink", 0xd7, 0x83, 0x7f));
        mColorList.add(new ColorName("pink", "solid pink", 0x89, 0x38, 0x43));
        mColorList.add(new ColorName("pink", "silver pink", 0xc4, 0xae, 0xad));
        mColorList.add(new ColorName("pink", "queen pink", 0xe8, 0xcc, 0xd7));
        mColorList.add(new ColorName("pink", "pink lavender", 0xd8, 0xb2, 0xd1));
        mColorList.add(new ColorName("pink", "mountbatten pink", 0x99, 0x7a, 0x8d));
        mColorList.add(new ColorName("pink", "pink (pantone)", 0xd7, 0x48, 0x94));
        mColorList.add(new ColorName("pink", "mexican pink", 0xe4, 0x00, 0x7c));
        mColorList.add(new ColorName("pink", "barbie pink", 0xe0, 0x21, 0x8a));
        mColorList.add(new ColorName("pink", "fandango pink", 0xde, 0x52, 0x85));
        mColorList.add(new ColorName("pink", "paradise pink", 0xe6, 0x3e, 0x62));
        mColorList.add(new ColorName("pink", "brink pink", 0xfb, 0x60, 0x7f));
        mColorList.add(new ColorName("pink", "french pink", 0xfd, 0x6c, 0x9e));
        mColorList.add(new ColorName("pink", "bright pink", 0xff, 0x00, 0x7f));
        mColorList.add(new ColorName("pink", "persian pink", 0xf7, 0x7f, 0xbe));
        mColorList.add(new ColorName("pink", "light deep pink", 0xff, 0x5c, 0xcd));
        mColorList.add(new ColorName("pink", "ultra pink", 0xff, 0x6f, 0xff));
        mColorList.add(new ColorName("pink", "shocking pink", 0xfc, 0x0f, 0xc0));
        mColorList.add(new ColorName("pink", "super pink", 0xcf, 0x6b, 0xa9));
        mColorList.add(new ColorName("pink", "steel pink", 0xcc, 0x33, 0xcc));
        mColorList.add(new ColorName("red", "indian red", 0xcd, 0x5c, 0x5c));
        mColorList.add(new ColorName("red", "indigo", 0x4b, 0x00, 0x82));
        mColorList.add(new ColorName("red", "misty rose", 0xff, 0xe4, 0xe1));
        mColorList.add(new ColorName("red", "orange red", 0xff, 0x45, 0x00));
        mColorList.add(new ColorName("red", "red", 0xff, 0x00, 0x00));
        mColorList.add(new ColorName("red", "imperial red", 0xed, 0x29, 0x39));
        mColorList.add(new ColorName("red", "spanish red", 0xe6, 0x00, 0x26));
        mColorList.add(new ColorName("red", "desire", 0xea, 0x3c, 0x53));
        mColorList.add(new ColorName("red", "ruby", 0xe6, 0x20, 0x20));
        mColorList.add(new ColorName("red", "crimson", 0xdc, 0x14, 0x3c));
        mColorList.add(new ColorName("red", "rusty red", 0xda, 0x2c, 0x43));
        mColorList.add(new ColorName("red", "cardinal red", 0xc4, 0x1e, 0x3a));
        mColorList.add(new ColorName("red", "cornell red", 0xb3, 0x1b, 0x1b));
        mColorList.add(new ColorName("red", "fire brick", 0xb2, 0x22, 0x22));
        mColorList.add(new ColorName("red", "redwood", 0xa4, 0x5a, 0x52));
        mColorList.add(new ColorName("red", "OU crimson red", 0x99, 0x00, 0x00));
        mColorList.add(new ColorName("red", "dark red", 0x8b, 0x00, 0x00));
        mColorList.add(new ColorName("red", "maroon", 0x80, 0x00, 0x00));
        mColorList.add(new ColorName("red", "barn red", 0x7c, 0x0a, 0x02));
        mColorList.add(new ColorName("brown", "wheat", 0xf5, 0xde, 0xb3));
        mColorList.add(new ColorName("brown", "bisque", 0xff, 0xe4, 0xc4));
        mColorList.add(new ColorName("brown", "blanched almond", 0xff, 0xeb, 0xcd));
        mColorList.add(new ColorName("brown", "brown", 0xa5, 0x2a, 0x2a));
        mColorList.add(new ColorName("brown", "burly wood", 0xde, 0xb8, 0x87));
        mColorList.add(new ColorName("brown", "chocolate", 0xd2, 0x69, 0x1e));
        mColorList.add(new ColorName("brown", "dark khaki", 0xbd, 0xb7, 0x6b));
        mColorList.add(new ColorName("brown", "khaki", 0xf0, 0xe6, 0x8c));
        mColorList.add(new ColorName("brown", "moccasin", 0xff, 0xe4, 0xb5));
        mColorList.add(new ColorName("brown", "olive drab", 0x6b, 0x8e, 0x23));
        mColorList.add(new ColorName("brown", "saddle brown", 0x8b, 0x45, 0x13));
        mColorList.add(new ColorName("brown", "sienna", 0xa0, 0x52, 0x2d));
        mColorList.add(new ColorName("brown", "brown", 0x96, 0x4b, 0x00));
        mColorList.add(new ColorName("brown", "beaver", 0x9f, 0x81, 0x70));
        mColorList.add(new ColorName("brown", "beige", 0xf5, 0xf5, 0xdc));
        mColorList.add(new ColorName("brown", "buff", 0xf0, 0xdc, 0x82));
        mColorList.add(new ColorName("brown", "burnt umber", 0x8a, 0x33, 0x24));
        mColorList.add(new ColorName("brown", "chestnut", 0x95, 0x45, 0x35));
        mColorList.add(new ColorName("brown", "desert sand", 0xed, 0xc9, 0xaf));
        mColorList.add(new ColorName("brown", "khaki", 0xc3, 0xb0, 0x91));
        mColorList.add(new ColorName("brown", "kobicha", 0x6b, 0x44, 0x23));
        mColorList.add(new ColorName("brown", "peru", 0xcd, 0x85, 0x3f));
        mColorList.add(new ColorName("brown", "raw umber", 0x82, 0x66, 0x44));
        mColorList.add(new ColorName("brown", "rosy brown", 0xbc, 0x8f, 0x8f));
        mColorList.add(new ColorName("brown", "russet", 0x80, 0x46, 0x1b));
        mColorList.add(new ColorName("brown", "sandy brown", 0xf4, 0xa4, 0x60));
        mColorList.add(new ColorName("brown", "smokey topaz", 0x83, 0x2a, 0x0d));
        mColorList.add(new ColorName("brown", "tan", 0xd2, 0xb4, 0x8c));
        mColorList.add(new ColorName("brown", "taupe", 0x48, 0x3c, 0x32));
        mColorList.add(new ColorName("brown", "wood brown", 0xc1, 0x9a, 0x6b));
        mColorList.add(new ColorName("orange", "traditional orange", 0xff, 0x7f, 0x00));
        mColorList.add(new ColorName("orange", "orange", 0xff, 0xa5, 0x00));
        mColorList.add(new ColorName("orange", "dark orange", 0xff, 0x8c, 0x00));
        mColorList.add(new ColorName("orange", "papaya whip", 0xff, 0xef, 0xd5));
        mColorList.add(new ColorName("orange", "peach", 0xff, 0xe5, 0xb4));
        mColorList.add(new ColorName("orange", "apricot", 0xfb, 0xce, 0xb1));
        mColorList.add(new ColorName("orange", "melon", 0xfd, 0xbc, 0xb4));
        mColorList.add(new ColorName("orange", "atomic tangerine", 0xff, 0x99, 0x66));
        mColorList.add(new ColorName("orange", "tea rose", 0xf8, 0x83, 0x79));
        mColorList.add(new ColorName("orange", "carrot orange", 0xed, 0x91, 0x21));
        mColorList.add(new ColorName("orange", "orange peel", 0xff, 0x9f, 0x00));
        mColorList.add(new ColorName("orange", "princeton orange", 0xf5, 0x80, 0x25));
        mColorList.add(new ColorName("orange", "spanish orange", 0xe8, 0x61, 0x00));
        mColorList.add(new ColorName("orange", "pumpkin", 0xff, 0x75, 0x18));
        mColorList.add(new ColorName("orange", "giants orange", 0xfe, 0x5a, 0x1d));
        mColorList.add(new ColorName("orange", "vermillion", 0xe3, 0x42, 0x34));
        mColorList.add(new ColorName("orange", "tomato", 0xff, 0x63, 0x47));
        mColorList.add(new ColorName("orange", "bittersweet", 0xfe, 0x6f, 0x5e));
        mColorList.add(new ColorName("orange", "persimmon", 0xec, 0x58, 0x00));
        mColorList.add(new ColorName("orange", "persian orange", 0xd9, 0x90, 0x58));
        mColorList.add(new ColorName("orange", "alloy orange", 0xc4, 0x63, 0x10));
        mColorList.add(new ColorName("orange", "burnt orange", 0xcc, 0x55, 0x00));
        mColorList.add(new ColorName("orange", "bittersweet shimmer", 0xbf, 0x4f, 0x51));
        mColorList.add(new ColorName("yellow", "dark golden rod", 0xb8, 0x86, 0x0b));
        mColorList.add(new ColorName("yellow", "lemon chiffon", 0xff, 0xfa, 0xcd));
        mColorList.add(new ColorName("yellow", "light golden rod yellow", 0xfa, 0xfa, 0xd2));
        mColorList.add(new ColorName("yellow", "light yellow", 0xff, 0xff, 0xe0));
        mColorList.add(new ColorName("yellow", "pale golden rod", 0xee, 0xe8, 0xaa));
        mColorList.add(new ColorName("yellow", "yellow", 0xff, 0xff, 0x00));
        mColorList.add(new ColorName("yellow", "light yellow", 0xff, 0xff, 0xed));
        mColorList.add(new ColorName("yellow", "cream", 0xff, 0xff, 0xcc));
        mColorList.add(new ColorName("yellow", "unmellow yellow", 0xff, 0xff, 0x66));
        mColorList.add(new ColorName("yellow", "lemon", 0xff, 0xac, 0xd));
        mColorList.add(new ColorName("yellow", "mellow yellow", 0xf8, 0xde, 0x7e));
        mColorList.add(new ColorName("yellow", "royal yellow", 0xfa, 0xda, 0x5e));
        mColorList.add(new ColorName("yellow", "gold", 0xff, 0xd7, 0x00));
        mColorList.add(new ColorName("yellow", "cyber yellow", 0xff, 0xd3, 0x00));
        mColorList.add(new ColorName("yellow", "safety yellow", 0xed, 0xd2, 0x02));
        mColorList.add(new ColorName("yellow", "goldenrod", 0xda, 0xa5, 0x20));
        mColorList.add(new ColorName("yellow", "olive", 0x80, 0x80, 0x00));
        mColorList.add(new ColorName("green", "mint cream", 0xf5, 0xff, 0xfa));
        mColorList.add(new ColorName("green", "chartreuse", 0x7f, 0xff, 0x00));
        mColorList.add(new ColorName("green", "dark olive green", 0x55, 0x6b, 0x2f));
        mColorList.add(new ColorName("green", "dark sea green", 0x8f, 0xbc, 0x8f));
        mColorList.add(new ColorName("green", "dark turquoise", 0x00, 0xce, 0xd1));
        mColorList.add(new ColorName("green", "honey dew", 0xf0, 0xff, 0xf0));
        mColorList.add(new ColorName("green", "lawn green", 0x7c, 0xfc, 0x00));
        mColorList.add(new ColorName("green", "lime green", 0x32, 0xcd, 0x32));
        mColorList.add(new ColorName("green", "lime", 0x00, 0xff, 0x00));
        mColorList.add(new ColorName("green", "medium sea green", 0x3c, 0xb3, 0x71));
        mColorList.add(new ColorName("green", "medium spring green", 0x00, 0xfa, 0x9a));
        mColorList.add(new ColorName("green", "medium turquoise", 0x48, 0xd1, 0xcc));
        mColorList.add(new ColorName("green", "pale turquoise", 0xaf, 0xee, 0xee));
        mColorList.add(new ColorName("green", "spring green", 0x00, 0xff, 0x7f));
        mColorList.add(new ColorName("green", "yellow green", 0x9a, 0xcd, 0x32));
        mColorList.add(new ColorName("green", "artichoke", 0x8f, 0x97, 0x79));
        mColorList.add(new ColorName("green", "asparagus", 0x87, 0xa9, 0x6b));
        mColorList.add(new ColorName("green", "avocado", 0x56, 0x82, 0x03));
        mColorList.add(new ColorName("green", "fern green", 0x71, 0xbc, 0x78));
        mColorList.add(new ColorName("green", "forest green", 0x22, 0x8b, 0x22));
        mColorList.add(new ColorName("green", "hooker's green", 0x49, 0x79, 0x6b));
        mColorList.add(new ColorName("green", "jungle green", 0x29, 0xab, 0x87));
        mColorList.add(new ColorName("green", "laurel green", 0xa9, 0xba, 0x9d));
        mColorList.add(new ColorName("green", "light green", 0x90, 0xee, 0x90));
        mColorList.add(new ColorName("green", "mantis", 0x74, 0xc3, 0x65));
        mColorList.add(new ColorName("green", "moss green", 0x8a, 0x9a, 0x58));
        mColorList.add(new ColorName("green", "myrtle green", 0x31, 0x78, 0x73));
        mColorList.add(new ColorName("green", "mint green", 0x98, 0xfb, 0x98));
        mColorList.add(new ColorName("green", "pine green", 0x01, 0x79, 0x6f));
        mColorList.add(new ColorName("green", "shamrock green", 0x00, 0x9e, 0x60));
        mColorList.add(new ColorName("green", "teal", 0x00, 0x80, 0x80));
        mColorList.add(new ColorName("green", "dark green", 0x00, 0x64, 0x00));
        mColorList.add(new ColorName("green", "bright green", 0x66, 0xff, 0x00));
        mColorList.add(new ColorName("green", "brunswick green", 0x1b, 0x4d, 0x3e));
        mColorList.add(new ColorName("green", "cal poly pomona green", 0x1e, 0x4d, 0x2b));
        mColorList.add(new ColorName("green", "dark pastel green", 0x03, 0xc0, 0x3c));
        mColorList.add(new ColorName("green", "dartmouth green", 0x00, 0x70, 0x3c));
        mColorList.add(new ColorName("green", "emerald", 0x50, 0xc8, 0x78));
        mColorList.add(new ColorName("green", "feldgrau", 0x4d, 0x5d, 0x53));
        mColorList.add(new ColorName("green", "go green", 0x00, 0xab, 0x66));
        mColorList.add(new ColorName("green", "yellow-green", 0xad, 0xff, 0x2f));
        mColorList.add(new ColorName("green", "harlequin", 0x3f, 0xff, 0x00));
        mColorList.add(new ColorName("green", "hunter green", 0x35, 0x5e, 0x3b));
        mColorList.add(new ColorName("green", "india green", 0x13, 0x88, 0x08));
        mColorList.add(new ColorName("green", "islamic green", 0x00, 0x99, 0x00));
        mColorList.add(new ColorName("green", "jade", 0x00, 0xa8, 0x6b));
        mColorList.add(new ColorName("green", "kelly green", 0x4c, 0xbb, 0x17));
        mColorList.add(new ColorName("green", "malachite", 0xbd, 0xa5, 0x1));
        mColorList.add(new ColorName("green", "msu green", 0x18, 0x45, 0x3b));
        mColorList.add(new ColorName("green", "north texas green", 0x00, 0x27, 0x79));
        mColorList.add(new ColorName("green", "office green", 0x00, 0x80, 0x00));
        mColorList.add(new ColorName("green", "pakistan green", 0x00, 0x66, 0x00));
        mColorList.add(new ColorName("green", "paris green", 0x50, 0xc8, 0x78));
        mColorList.add(new ColorName("green", "persian green", 0x00, 0xa6, 0x93));
        mColorList.add(new ColorName("green", "rifle green", 0x44, 0x4c, 0x38));
        mColorList.add(new ColorName("green", "russian green", 0x67, 0x92, 0x67));
        mColorList.add(new ColorName("green", "sacramento state green", 0x00, 0x56, 0x3f));
        mColorList.add(new ColorName("green", "sea green", 0x2e, 0x8b, 0x57));
        mColorList.add(new ColorName("green", "spanish green", 0x00, 0x91, 0x50));
        mColorList.add(new ColorName("cyan", "cyan", 0x00, 0xff, 0xff));
        mColorList.add(new ColorName("cyan", "light cyan", 0xe0, 0xff, 0xff));
        mColorList.add(new ColorName("cyan", "aero blue", 0xc9, 0xff, 0xe5));
        mColorList.add(new ColorName("cyan", "celeste", 0xb2, 0xff, 0xff));
        mColorList.add(new ColorName("cyan", "electric blue", 0x7d, 0xf9, 0xff));
        mColorList.add(new ColorName("cyan", "turquoise", 0x40, 0xe0, 0xd0));
        mColorList.add(new ColorName("cyan", "robin egg blue", 0x00, 0xcc, 0xcc));
        mColorList.add(new ColorName("cyan", "light sea green", 0x20, 0xb2, 0xaa));
        mColorList.add(new ColorName("cyan", "blue-green", 0x0d, 0x98, 0xba));
        mColorList.add(new ColorName("cyan", "keppel", 0x3a, 0xb0, 0x9e));
        mColorList.add(new ColorName("cyan", "cerulean", 0x00, 0x7b, 0xa7));
        mColorList.add(new ColorName("cyan", "dark cyan", 0x00, 0x8b, 0x8b));
        mColorList.add(new ColorName("cyan", "midnight green", 0x00, 0x49, 0x53));
        mColorList.add(new ColorName("cyan", "charleston green", 0x23, 0x2b, 0x2b));
        mColorList.add(new ColorName("cyan", "aquamarine", 0x7f, 0xff, 0xd4));
        mColorList.add(new ColorName("cyan", "medium aqua marine", 0x66, 0xcd, 0xaa));
        mColorList.add(new ColorName("blue", "alice blue", 0xf0, 0xf8, 0xff));
        mColorList.add(new ColorName("blue", "azure", 0xf0, 0xff, 0xff));
        mColorList.add(new ColorName("blue", "blue violet", 0x8a, 0x2b, 0xe2));
        mColorList.add(new ColorName("blue", "cadet blue", 0x5f, 0x9e, 0xa0));
        mColorList.add(new ColorName("blue", "cornflower blue", 0x64, 0x95, 0xed));
        mColorList.add(new ColorName("blue", "dark slate blue", 0x48, 0x3d, 0x8b));
        mColorList.add(new ColorName("blue", "deep sky blue", 0x00, 0xbf, 0xff));
        mColorList.add(new ColorName("blue", "dodger blue", 0x1e, 0x90, 0xff));
        mColorList.add(new ColorName("blue", "light sky blue", 0x87, 0xce, 0xfa));
        mColorList.add(new ColorName("blue", "light steel blue", 0xb0, 0xc4, 0xde));
        mColorList.add(new ColorName("blue", "medium slate blue", 0x7b, 0x68, 0xee));
        mColorList.add(new ColorName("blue", "royal blue", 0x41, 0x69, 0xe1));
        mColorList.add(new ColorName("blue", "sky blue", 0x87, 0xce, 0xeb));
        mColorList.add(new ColorName("blue", "slate blue", 0x6a, 0x5a, 0xcd));
        mColorList.add(new ColorName("blue", "steel blue", 0x46, 0x82, 0xb4));
        mColorList.add(new ColorName("blue", "blue", 0x00, 0x00, 0xff));
        mColorList.add(new ColorName("blue", "periwinkle", 0xcc, 0xcc, 0xff));
        mColorList.add(new ColorName("blue", "powder blue", 0xb0, 0xe0, 0xe6));
        mColorList.add(new ColorName("blue", "light blue", 0xad, 0xd8, 0xe6));
        mColorList.add(new ColorName("blue", "baby blue", 0x89, 0xcf, 0xf0));
        mColorList.add(new ColorName("blue", "crayola blue", 0x1f, 0x75, 0xfe));
        mColorList.add(new ColorName("blue", "medium blue", 0x00, 0x00, 0xcd));
        mColorList.add(new ColorName("blue", "spanish blue", 0x00, 0x70, 0xbb));
        mColorList.add(new ColorName("blue", "liberty", 0x54, 0x5a, 0xa7));
        mColorList.add(new ColorName("blue", "egyption blue", 0x10, 0x34, 0xa6));
        mColorList.add(new ColorName("blue", "dark blue", 0x00, 0x00, 0x8b));
        mColorList.add(new ColorName("blue", "electric ultramarine", 0x3f, 0x00, 0xff));
        mColorList.add(new ColorName("blue", "resolution blue", 0x00, 0x23, 0x87));
        mColorList.add(new ColorName("blue", "navy blue", 0x00, 0x00, 0x80));
        mColorList.add(new ColorName("blue", "catalina blue", 0x06, 0x2a, 0x78));
        mColorList.add(new ColorName("blue", "midnight blue", 0x19, 0x19, 0x70));
        mColorList.add(new ColorName("blue", "independence", 0x4c, 0x51, 0x6d));
        mColorList.add(new ColorName("blue", "space cadet", 0x1d, 0x29, 0x51));
        mColorList.add(new ColorName("purple", "purple", 0x80, 0x00, 0x80));
        mColorList.add(new ColorName("purple", "tyrian purple", 0x66, 0x02, 0x3c));
        mColorList.add(new ColorName("purple", "royal purple", 0x78, 0x51, 0xa9));
        mColorList.add(new ColorName("purple", "red-violet", 0xc7, 0x15, 0x85));
        mColorList.add(new ColorName("purple", "thistle", 0xd8, 0xbf, 0xd8));
        mColorList.add(new ColorName("purple", "mauve", 0xe0, 0xb0, 0xff));
        mColorList.add(new ColorName("purple", "orchid", 0xda, 0x70, 0xd6));
        mColorList.add(new ColorName("purple", "heliotrope", 0xdf, 0x73, 0xff));
        mColorList.add(new ColorName("purple", "phlox", 0xdf, 0x00, 0xff));
        mColorList.add(new ColorName("purple", "purple pizzazz", 0xfe, 0x4e, 0xda));
        mColorList.add(new ColorName("purple", "liseran purple", 0xde, 0x6f, 0xa1));
        mColorList.add(new ColorName("purple", "mulberry", 0xc5, 0x4b, 0x8c));
        mColorList.add(new ColorName("purple", "pearly purple", 0xb7, 0x68, 0xa2));
        mColorList.add(new ColorName("purple", "purpureus", 0x9a, 0x4e, 0xae));
        mColorList.add(new ColorName("purple", "ksu purple", 0x51, 0x28, 0x88));
        mColorList.add(new ColorName("purple", "pomp and power", 0x86, 0x60, 0x8e));
        mColorList.add(new ColorName("purple", "mardi gras", 0x88, 0x00, 0x95));
        mColorList.add(new ColorName("purple", "eminence", 0x6c, 0x30, 0x82));
        mColorList.add(new ColorName("purple", "byzantium", 0x70, 0x29, 0x63));
        mColorList.add(new ColorName("purple", "pansy", 0x78, 0x18, 0x4a));
        mColorList.add(new ColorName("purple", "dark violet", 0x94, 0x00, 0xd3));
        mColorList.add(new ColorName("purple", "lavender blush", 0xff, 0xf0, 0xf5));
        mColorList.add(new ColorName("purple", "lavender", 0xe6, 0xe6, 0xfa));
        mColorList.add(new ColorName("purple", "medium purple", 0x93, 0x70, 0xdb));
        mColorList.add(new ColorName("purple", "medium violet red", 0xc7, 0x15, 0x85));
        mColorList.add(new ColorName("purple", "pale violet red", 0xdb, 0x70, 0x93));
        mColorList.add(new ColorName("purple", "plum", 0xdd, 0xa0, 0xdd));
        mColorList.add(new ColorName("purple", "purple", 0x80, 0x00, 0x80));
        mColorList.add(new ColorName("purple", "violet", 0xee, 0x82, 0xee));

        mInitialized = true;
        return true;
    }

    /**
     * ColorName object to link RGB colors to a name
     */
    private class ColorName {

        // Members
        private String mColorName = null;
        private String mShadeName = null;
        private int r = 0, g = 0, b = 0;

        /**
         * Constructor
         *
         * @param shadeName {@link String} name of shade
         * @param colorName {@link String} name of the color
         * @param r         {@link int} red value
         * @param g         {@link int} green value
         * @param b         {@link int} blue value
         */
        public ColorName(String shadeName, String colorName, int r, int g, int b) {
            this.mShadeName = shadeName;
            this.mColorName = colorName;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        /**
         * Get the name of the shade
         *
         * @return {@link String}
         */
        public String getShade() {
            return mShadeName;
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
         * @return {@link int} proximity
         */
        public double computeMSE(int pixR, int pixG, int pixB) {

            // Logging
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("Computing MSE:");

            // Red difference
            int rdiff = pixR - r;
            logMsg.append("\n\t");
            logMsg.append("RDIFF: ");
            logMsg.append(rdiff);

            // Green difference
            int gdiff = pixG - g;
            logMsg.append("\n\t");
            logMsg.append("GDIFF: ");
            logMsg.append(gdiff);

            // Blue difference
            int bdiff = pixB - b;
            logMsg.append("\n\t");
            logMsg.append("BDIFF: ");
            logMsg.append(bdiff);

            int lr = rdiff * rdiff;
            int lg = gdiff * gdiff;
            int lb = bdiff * bdiff;

            // Log rdiff^2
            logMsg.append("\n\t");
            logMsg.append("LR: ");
            logMsg.append(lr);

            // Log gdiff^2
            logMsg.append("\n\t");
            logMsg.append("LG: ");
            logMsg.append(lg);

            // Log bdiff^2
            logMsg.append("\n\t");
            logMsg.append("LB: ");
            logMsg.append(lb);

            double mse = (double) (lr + lg + lb) / 3.0d;
            logMsg.append("\n\t");
            logMsg.append("MSE: ");
            logMsg.append(mse);
            logMsg.append("\n");
            return mse;
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
