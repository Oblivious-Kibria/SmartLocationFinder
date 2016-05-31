package com.example.amazingaayan.smartlocationfinder;

import java.util.List;

/**
 * Created by Amazing Aayan on 27-May-16.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
