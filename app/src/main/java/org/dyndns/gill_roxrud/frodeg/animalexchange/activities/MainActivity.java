package org.dyndns.gill_roxrud.frodeg.animalexchange.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;


public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onInventoryButtonClicked(View v) {
    }

    public void onWalkButtonClicked(View v) {
    }

    public void onTradeButtonClicked(View v) {
    }

    public void onRankingButtonClicked(View v) {
    }

    public void onProfileButtonClicked(View v) {
    }

    public void onHelpButtonClicked(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AnimalExchangeApplication.HELP_URL)));
    }

}
