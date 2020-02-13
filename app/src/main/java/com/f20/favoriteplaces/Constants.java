package com.f20.favoriteplaces;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Constants {

    public static final int MAP_TYPE_DEFAULT = 0;
    public static final int MAP_TYPE_SATELITE = 1;
    public static final int MAP_TYPE_HYBRID = 2;
    public static final int MAP_TYPE_TERRAIN = 3;

    public static final int RADIUS = 1500;


    public static final int[] Colors = {Color.BLUE, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.BLACK, Color.YELLOW, Color.MAGENTA};
    public static final float[] Hues = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN,
    BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_MAGENTA};

}
