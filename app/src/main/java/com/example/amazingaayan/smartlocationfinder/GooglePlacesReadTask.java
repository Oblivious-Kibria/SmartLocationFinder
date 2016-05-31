package com.example.amazingaayan.smartlocationfinder;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
/**
 * Created by Amazing Aayan on 23-May-16.
 */
public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap mMap;
    @Override
    protected String doInBackground(Object... params) {

        try {
            mMap = (GoogleMap) params[0];
            String googlePlacesUrl = (String) params[1];
            Log.d("URL",googlePlacesUrl);
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
            Log.d("GooglePlace HTTP DATA",googlePlacesData);
        }
        catch(Exception e){
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }
}
