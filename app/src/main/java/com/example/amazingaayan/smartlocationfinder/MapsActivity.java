package com.example.amazingaayan.smartlocationfinder;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DirectionFinderListener  {

    private GoogleMap mMap;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private static final String GOOGLE_API_KEY = "AIzaSyDp4569OfuGs1GNGYba_47OLu-eNEMMqKE";
    // server key that worked   AIzaSyAqh_u16ji0SMGdIusI7g4pepV2ciE7Mb4 ;
    // release google place android api key :  ;

    static double latitude = 0;
    static double longitude = 0;
    private static int PROXIMITY_RADIUS;
    private static String receivedMapType;
    private static int MapType;
    GoogleApiClient mGoogleApiClient = null;
    Location mLastLocation = null;
    LocationRequest mLocationRequest;
    LatLng latLng;
    Marker currLocationMarker;
    TextView time_and_Distance;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        time_and_Distance = (TextView) findViewById(R.id.time_Distance);

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); //retrieving shared preference data;
        String receivedRadious = myPrefs.getString("SELECTED_RADIOUS", "500");
        receivedMapType = myPrefs.getString("SELECTED_MAP_TYPE", "NORMAL");
        PROXIMITY_RADIUS = Integer.valueOf(receivedRadious);

        if(receivedMapType.equals("NONE"))
            MapType = 0;
        else if(receivedMapType.equals("NORMAL"))
            MapType = 1;
        else if(receivedMapType.equals("SATELLITE"))
            MapType = 2;
        else if(receivedMapType.equals("TERRAIN"))
            MapType = 3;
        else if(receivedMapType.equals("HYBRID"))
            MapType = 4;

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MapsActivity.this, "Radious "+PROXIMITY_RADIUS, Toast.LENGTH_LONG).show();
                Intent intent = getIntent();
                String type = intent.getStringExtra("TYPE");
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&type=" + type);
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = mMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
                //Toast.makeText(MapsActivity.this, "URL"+googlePlacesUrl.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    
    private boolean isGooglePlayServicesAvailable() { // Custom Method for checking if google service available.
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, REQUEST_GOOGLE_PLAY_SERVICES).show();
            }

            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        //for setting map type;
        if(MapType == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            //Toast.makeText(MapsActivity.this, "Map Type "+receivedMapType, Toast.LENGTH_LONG).show();
        }
        if(MapType == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //Toast.makeText(MapsActivity.this, "Map Type "+receivedMapType, Toast.LENGTH_LONG).show();
        }
        if(MapType == 2) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //Toast.makeText(MapsActivity.this, "Map Type "+receivedMapType, Toast.LENGTH_LONG).show();
        }
        if(MapType == 3) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            //Toast.makeText(MapsActivity.this, "Map Type "+receivedMapType, Toast.LENGTH_LONG).show();
        }
        if(MapType == 4) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            //Toast.makeText(MapsActivity.this, "Map Type "+receivedMapType, Toast.LENGTH_LONG).show();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // to draw a direction line when a marker will be touched;
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng end_position = marker.getPosition();
                Double end_latitude = end_position.latitude;
                Double end_longitude = end_position.longitude;

                String end_Latitude = String.valueOf(end_latitude);
                String end_Longitude = String.valueOf(end_longitude);

                String end_Position = end_Latitude+","+end_Longitude;

                sendRequest(end_Position);
                //Toast.makeText(MapsActivity.this, "Marker position "+end_position, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void sendRequest(String  end_position) {
        String origin_Latitude = String.valueOf(latitude);
        String origin_Longitude = String.valueOf(longitude);
        String origin = origin_Latitude+","+origin_Longitude;
        String destination = end_position.toString();
        //Toast.makeText(MapsActivity.this, "Origin = "+origin+" Destination "+destination, Toast.LENGTH_LONG).show();

        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(MapsActivity.this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mMap.addMarker(markerOptions);

        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(18).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // for implementing own(DirectionFinderListener) listener class;
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }

    }

    // for implementing own(DirectionFinderListener) listener class;
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            String duration = (route.duration.text);
            String distance = (route.distance.text);
            String distance_and_duration = "Distance: "+distance+" Duration: "+duration;

            time_and_Distance.setText(distance_and_duration);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(6);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

}
