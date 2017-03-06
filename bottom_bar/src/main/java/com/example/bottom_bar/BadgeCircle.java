package com.example.bottom_bar;

/**
 * Created by edenkes on 3/3/2017.
 */


import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

class BadgeCircle {
    /**
     * Creates a new circle for the Badge background.
     *
     * @param size  the width and height for the circle
     * @param color the activeIconColor for the circle
     * @return a nice and adorable circle.
     */
    static ShapeDrawable make(int size, int color) {
        ShapeDrawable indicator = new ShapeDrawable(new OvalShape());
        indicator.setIntrinsicWidth(size);
        indicator.setIntrinsicHeight(size);
        indicator.getPaint().setColor(color);
        return indicator;
    }
}