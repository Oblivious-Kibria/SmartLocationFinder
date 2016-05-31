package com.example.amazingaayan.smartlocationfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int PICK_ActivityContact_REQUEST = 100;
    private String person_name;
    private String person_email;
    private String photo_url;
    private  String imageName = "userProfilePicture";
    private static int check_if_SignedIn = 0;
    private final String MY_PREFS_NAME = "SmartLocationFinder"; //shared preference name;

    NavigationView navigationView;
    GridView categories_GridView;
    TextView userName, userEmail;
    ImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // For GridView
        categories_GridView = (GridView) findViewById(R.id.categories_GridView);
        categories_GridView.setAdapter(new Adapter_for_GridView(this));

        categories_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                // Send intent to SingleViewActivity
                if(position == 0){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "atm");
                    startActivity(googleMapActivity);
                }
                else if(position == 1){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "bank");
                    startActivity(googleMapActivity);
                }
                else if(position == 2){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "hospital");
                    startActivity(googleMapActivity);
                }
                else if(position == 3){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "cafe");
                    startActivity(googleMapActivity);
                }
                else if(position == 4){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "police");
                    startActivity(googleMapActivity);
                }
                else if(position == 5){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "airport");
                    startActivity(googleMapActivity);
                }
                else if(position == 6){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "gas_station");
                    startActivity(googleMapActivity);
                }
                else if(position == 7){
                    Intent googleMapActivity = new Intent(getApplicationContext(), MapsActivity.class);
                    googleMapActivity.putExtra("TYPE", "fire_station");
                    startActivity(googleMapActivity);
                }
            }
        });

        // for showing user sign in info in navigation drawer;
        View hView = navigationView.getHeaderView(0);
        userName = (TextView) hView.findViewById(R.id.user_Name);
        userEmail = (TextView) hView.findViewById(R.id.user_Email);
        userImage = (ImageView) hView.findViewById(R.id.user_Image);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE); //retrieving shared preference data;
        check_if_SignedIn = prefs.getInt("CHECK_IF_SIGNIN",0);

        if(check_if_SignedIn != 0){
            String User_name = prefs.getString("Name", "No name defined");// "No name defined" is the default value.
            String User_email = prefs.getString("Email", "gmail.com");   // "gmail.com" is the default value.
            userName.setText(User_name);
            userEmail.setText(User_email);
            LoadProfileImage loadProfileImage = new LoadProfileImage(userImage,  MainActivity.this);
            Bitmap userProfilePic = loadProfileImage.loadBitmap(MainActivity.this, imageName); // for fetching NavigationDrawer's user image
            userImage.setImageBitmap(userProfilePic);                                         // from provided url & set image into ImageView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // To get Intent Activity Result
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ActivityContact_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                person_name = data.getStringExtra("PERSON_NAME");
                person_email = data.getStringExtra("PERSON_EMAIL");
                photo_url = data.getStringExtra("PHOTO_URL");

                //For shared preference;
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("Name", person_name);
                editor.putString("Email", person_email);
                editor.putInt("CHECK_IF_SIGNIN", 1);
                editor.apply();

                userName.setText(person_name);
                userEmail.setText(person_email);
                new LoadProfileImage(userImage,  MainActivity.this).execute(photo_url); // for fetching NavigationDrawer's user image
                // from provided url & set image into ImageView;
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pick_a_place) {
            Intent pickPlace_Activity_Intent = new Intent(getBaseContext(), Pick_a_Place.class);
            MainActivity.this.startActivity(pickPlace_Activity_Intent);
        } else if (id == R.id.nav_setting) {
            Intent MapSetting_Activity_Intent = new Intent(getBaseContext(), MapSettingActivity.class);
            MainActivity.this.startActivity(MapSetting_Activity_Intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sign_in) {
            if(check_if_SignedIn==0) { //to check if already signed in;
                Intent signIn_Activity_Intent = new Intent(getBaseContext(), Sign_In_Activity.class);
                MainActivity.this.startActivity(signIn_Activity_Intent);
                startActivityForResult(signIn_Activity_Intent, PICK_ActivityContact_REQUEST);
                check_if_SignedIn = 1;
            }
            else {
                Toast.makeText(this, "Already Signed In!", Toast.LENGTH_LONG).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}