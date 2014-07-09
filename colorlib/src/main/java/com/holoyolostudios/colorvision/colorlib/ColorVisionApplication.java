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
package com.holoyolostudios.colorvision.colorlib;

import android.app.Application;
import com.holoyolostudios.colorvision.colorlib.colors.ColorNameCache;

/**
 * ColorVisionApplication
 * <p/>
 * Main application instance
 * <p/>
 *
 * @author Martin Brabham
 * @author Daniel Velazco
 * @see {@link android.app.Application}
 */
public class ColorVisionApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new instance for the ColorNameCache
        ColorNameCache.createInstance();

    }

    public void onTerminate() {
        ColorNameCache.getInstance().destroy();
        super.onTerminate();
    }

}

