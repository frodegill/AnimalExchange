package org.dyndns.gill_roxrud.frodeg.animalexchange.inventory;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGroup;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;

import java.util.ArrayList;


public class InventoryActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_inventory);

        AnimalManager animalManager = GameState.getInstance().getAnimalManager();

        ArrayList<AnimalGroup> inventoryArrayList = new ArrayList<>();
        AnimalGroupAdapter inventoryListViewAdapter = new AnimalGroupAdapter(this, inventoryArrayList);

        final ListView inventoryListView = findViewById(R.id.animalgrouplist);
        inventoryListView.setAdapter(inventoryListViewAdapter);
        inventoryListView.setOnItemLongClickListener(this);

        int level = 0;
        AnimalGroup animalGroup;
        while (null != (animalGroup = animalManager.getAnimalGroupByLevel(level++))) {
            inventoryArrayList.add(animalGroup);
        }
        inventoryListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final ListView inventoryListView = findViewById(R.id.animalgrouplist);
        AnimalGroup item = (AnimalGroup) inventoryListView.getItemAtPosition(position);
        /* TODO */
        return true;
    }

}
