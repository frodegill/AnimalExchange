package org.dyndns.gill_roxrud.frodeg.animalexchange.inventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGroup;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;

import java.util.ArrayList;


public class InventoryActivity extends AppCompatActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_inventory);

        AnimalManager animalManager = GameState.getInstance().getAnimalManager();

        ArrayList<AnimalGroup> inventoryArrayList = new ArrayList<>();
        AnimalGroupAdapter inventoryListViewAdapter = new AnimalGroupAdapter(this, inventoryArrayList);

        final ListView inventoryListView = findViewById(R.id.animalgrouplist);
        inventoryListView.setAdapter(inventoryListViewAdapter);

        int level = 0;
        AnimalGroup animalGroup;
        while (null != (animalGroup = animalManager.getAnimalGroupByLevel(level++))) {
            inventoryArrayList.add(animalGroup);
        }
        inventoryListViewAdapter.notifyDataSetChanged();

        onFoodUpdated();
    }

    public void onFoodUpdated() {
        TextView view = findViewById(R.id.food);
        if (view != null) {
            SyncQueueManager syncQueueManager = GameState.getInstance().getSyncQueueManager();
            view.setText(Integer.toString((int) syncQueueManager.getFood()));
        }
    }
}
