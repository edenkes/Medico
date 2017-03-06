package com.example.bottom_bar;

import android.support.annotation.IdRes;

/**
 * Created by edenkes on 3/3/2017.
 */

public interface OnTabReselectListener {
    /**
     * The method being called when currently visible {@link BottomBarTab} is
     * reselected. Use this method for scrolling to the top of your content,
     * as recommended by the Material Design spec
     *
     * @param tabId the {@link BottomBarTab} that was reselected.
     */
    void onTabReSelected(@IdRes int tabId);
}
