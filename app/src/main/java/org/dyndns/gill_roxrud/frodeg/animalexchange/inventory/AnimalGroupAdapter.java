package org.dyndns.gill_roxrud.frodeg.animalexchange.inventory;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalDefinition;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalGroup;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.AnimalManager;
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;

import java.util.ArrayList;


public class AnimalGroupAdapter extends BaseAdapter {

    private final ArrayList<AnimalGroup> data;
    private static LayoutInflater inflater = null;

    public AnimalGroupAdapter(Activity activity, ArrayList<AnimalGroup> data) {
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (view == null) {
            v = inflater.inflate(R.layout.animalgrouprow, null);
        }

        Context context = AnimalExchangeApplication.getContext();
        AnimalGroup animalGroup = (AnimalGroup) getItem(i);
        SyncQueueManager syncQueueManager = GameState.getInstance().getSyncQueueManager();

        TextView textView = v.findViewById(R.id.groupname);
        textView.setText(animalGroup.getName(context));

        LinearLayout linearLayout = v.findViewById(R.id.animallist);
        linearLayout.removeAllViews();
        for (AnimalDefinition animalDefinition : animalGroup.getAnimalDefinitionList()) {
            View animalRowView = inflater.inflate(R.layout.inventoryanimalrow, null);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ImageView imageView = animalRowView.findViewById(R.id.animalimage);
                int size = Math.min(imageView.getMaxWidth(), imageView.getMaxHeight());
                imageView.setImageBitmap(animalDefinition.getSquareBitmap(context, size));
            }

            textView = animalRowView.findViewById(R.id.animalname);
            textView.setText(animalDefinition.getName(context));

            textView = animalRowView.findViewById(R.id.animalcount);
            textView.setText(Integer.toString(syncQueueManager.getAnimalCount(animalDefinition.getLevel())));

            linearLayout.addView(animalRowView);
        }

        return v;
    }
}
