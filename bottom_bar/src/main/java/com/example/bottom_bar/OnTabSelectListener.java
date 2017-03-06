package com.example.bottom_bar;

/**
 * Created by edenkes on 3/3/2017.
 */

import android.support.annotation.IdRes;

public interface OnTabSelectListener {
    /**
     * The method being called when currently visible {@link BottomBarTab} changes.
     *
     * This listener is fired for the first time after the items have been set and
     * also after a configuration change, such as when screen orientation changes
     * from portrait to landscape.
     *
     * @param tabId the new visible {@link BottomBarTab}
     */
    void onTabSelected(@IdRes int tabId);
}
