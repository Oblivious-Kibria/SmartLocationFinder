package com.example.amazingaayan.smartlocationfinder;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by Amazing Aayan on 27-May-16.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
