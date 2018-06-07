package org.dyndns.gill_roxrud.frodeg.animalexchange.walk;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.MapFragment;
import org.osmdroid.config.Configuration;


public class MapActivity extends AppCompatActivity {
    private static final String MAP_FRAGMENT_TAG = "org.dyndns.gill_roxrud.frodeg.gridwalking.MAP_FRAGMENT_TAG";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = AnimalExchangeApplication.getContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setMapViewHardwareAccelerated(true);

        this.setContentView(R.layout.activity_map);

        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
            MapFragment mapFragment = new MapFragment();
            fm.beginTransaction().add(R.id.activity_map_layout, mapFragment, MAP_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

}
