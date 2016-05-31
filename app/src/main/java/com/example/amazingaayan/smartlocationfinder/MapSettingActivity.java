package com.example.amazingaayan.smartlocationfinder;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MapSettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private final String MY_PREFS_NAME = "RadiousSelector";
    SharedPreferences myPrefs;//shared preference name;
    Spinner raious_spinner;
    Spinner mapTypeSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setting);

        myPrefs =  PreferenceManager.getDefaultSharedPreferences(MapSettingActivity.this);

        raious_spinner = (Spinner) findViewById(R.id.spinnerRadious);
        mapTypeSpinner = (Spinner) findViewById(R.id.mapTypeSpinner);

        // Spinner Drop down elements
        List<String> radiousAmount = new ArrayList<String>();
        radiousAmount.add("500");
        radiousAmount.add("1000");
        radiousAmount.add("2000");
        radiousAmount.add("3000");
        radiousAmount.add("4000");
        radiousAmount.add("5000");

        Resources res = getResources();
        String[] mapTypes = res.getStringArray(R.array.mapType_arrays);

        // Creating adapter for spinner
        ArrayAdapter<String> radious_spinner_dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, radiousAmount);
        ArrayAdapter<String> mapType_spinner_dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mapTypes);

        // Drop down layout style - list view with radio button
        radious_spinner_dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapType_spinner_dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        raious_spinner.setAdapter(radious_spinner_dataAdapter);
        mapTypeSpinner.setAdapter(mapType_spinner_dataAdapter);

        myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mapTypeSpinner.setSelection(myPrefs.getInt("selectedSpinnerItem2", 0));
        raious_spinner.setSelection(myPrefs.getInt("selectedSpinnerItem1", 0));

        raious_spinner.setOnItemSelectedListener(this);
        mapTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        SharedPreferences.Editor editor = myPrefs.edit();
        //For shared preference;
        if(spinner.getId() == R.id.spinnerRadious){
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            int pos1 = parent.getSelectedItemPosition();
            editor.putInt("selectedSpinnerItem1",pos1);
            editor.putString("SELECTED_RADIOUS", item);
            editor.apply();
        }
        else if(spinner.getId() == R.id.mapTypeSpinner){
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            int pos2 = parent.getSelectedItemPosition();
            editor.putInt("selectedSpinnerItem2",pos2);
            editor.putString("SELECTED_MAP_TYPE", item);
            editor.apply();
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
