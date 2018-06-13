package org.dyndns.gill_roxrud.frodeg.animalexchange.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import org.dyndns.gill_roxrud.frodeg.animalexchange.logic.SyncQueueManager;

import java.util.ArrayList;


public class AnimalGroupAdapter extends BaseAdapter implements View.OnLongClickListener {

    private final ArrayList<AnimalGroup> data;
    private final InventoryActivity activity;
    private final LayoutInflater inflater;

    public AnimalGroupAdapter(InventoryActivity activity, ArrayList<AnimalGroup> data) {
        this.data = data;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            animalRowView.setTag(animalDefinition);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ImageView imageView = animalRowView.findViewById(R.id.animalimage);
                int size = Math.min(imageView.getMaxWidth(), imageView.getMaxHeight());
                imageView.setImageBitmap(animalDefinition.getSquareBitmap(context, size));
            }

            textView = animalRowView.findViewById(R.id.animalname);
            textView.setText(animalDefinition.getName(context));

            textView = animalRowView.findViewById(R.id.animalcount);
            int count[] = syncQueueManager.getAnimalCount(animalDefinition.getLevel());
            textView.setText(Integer.toString(count[SyncQueueManager.FED]) +
                             " + " + Integer.toString(count[SyncQueueManager.HUNGRY]) +
                             " (" + Integer.toString(count[SyncQueueManager.FOR_SALE]) + ")");

            animalRowView.setOnLongClickListener(this);
            linearLayout.addView(animalRowView);
        }

        return v;
    }

    @Override
    public boolean onLongClick(View v) {
        final AnimalDefinition animalDefinition = (AnimalDefinition) v.getTag();
        SyncQueueManager syncQueueManager = GameState.getInstance().getSyncQueueManager();
        int[] animalCount = syncQueueManager.getAnimalCount(animalDefinition.getLevel());
        if (animalDefinition.getFoodRequired() > GameState.getInstance().getSyncQueueManager().getFood() ||
            0 >= animalCount[SyncQueueManager.HUNGRY]) {
            return true;
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    GameState.getInstance().getAnimalManager().feedAnimalT(animalDefinition.getLevel());
                    notifyDataSetChanged();
                    activity.onFoodUpdated();
                }
            }
        };

        Context context = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage(String.format(context.getString(R.string.ack_feed_animal), animalDefinition.getName(AnimalExchangeApplication.getContext())))
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();

        return true;
    }
}
