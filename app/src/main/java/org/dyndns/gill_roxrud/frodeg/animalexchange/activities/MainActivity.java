package org.dyndns.gill_roxrud.frodeg.animalexchange.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.BuildConfig;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.inventory.InventoryActivity;
import org.dyndns.gill_roxrud.frodeg.animalexchange.profile.ProfileActivity;
import org.dyndns.gill_roxrud.frodeg.animalexchange.ranking.RankingActivity;
import org.dyndns.gill_roxrud.frodeg.animalexchange.trade.TradeActivity;
import org.dyndns.gill_roxrud.frodeg.animalexchange.walk.MapActivity;
import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    }

    public void onInventoryButtonClicked(View v) {
        ContextCompat.startActivity(this, new Intent(this, InventoryActivity.class), null);
    }

    public void onWalkButtonClicked(View v) {
        // Request permissions to support Android Marshmallow and above devices
        if (!needsAppPermissions()) {
            openMap();
        }
    }

    private void openMap() {
        ContextCompat.startActivity(this, new Intent(this, MapActivity.class), null);
    }

    public void onTradeButtonClicked(View v) {
        ContextCompat.startActivity(this, new Intent(this, TradeActivity.class), null);
    }

    public void onRankingButtonClicked(View v) {
        ContextCompat.startActivity(this, new Intent(this, RankingActivity.class), null);
    }

    public void onProfileButtonClicked(View v) {
        ContextCompat.startActivity(this, new Intent(this, ProfileActivity.class), null);
    }

    public void onHelpButtonClicked(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AnimalExchangeApplication.HELP_URL)));
    }

    // START PERMISSION CHECK
    final private int REQUEST_CODE_ASK_MULTIPLE_APP_PERMISSIONS = 124;

    private boolean needsAppPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_APP_PERMISSIONS);
                return true;
            } // else: We already have permissions, so handle as normal
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        TextView toastView = findViewById(R.id.toast);
        if (toastView != null) {
            toastView.setText("");
        }

        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_APP_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
                    final String msg = "Location permission is required to show the user's location on map.";
                    if (toastView != null) {
                        toastView.setText(msg);
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    openMap();
                }
                break;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
