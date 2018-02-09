package fr.epicture.epicture.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.activities.FavoritesActivity;
import fr.epicture.epicture.activities.LogoutActivity;
import fr.epicture.epicture.activities.MyPicturesActivity;
import fr.epicture.epicture.interfaces.MainDrawerInterface;

public class MainDrawerListAdapter extends BaseAdapter {

    private static final int MY_PICTURES = 0;
    private static final int MY_FAVORITES = 1;
    private static final int LOG_OUT = 2;

    private Context context;
    private List<String> contents;
    private List<Intent> intents;
    private MainDrawerInterface listener;

    public MainDrawerListAdapter(Context context, MainDrawerInterface listener) {
        this.context = context;
        this.contents = new ArrayList<>();
        this.intents = new ArrayList<>();
        this.listener = listener;

        contents.add(context.getString(R.string.my_pictures));
        contents.add(context.getString(R.string.favorites));
        contents.add(context.getString(R.string.logout));

        intents.add(MY_PICTURES, new Intent(context, MyPicturesActivity.class));
        intents.add(MY_FAVORITES, new Intent(context, FavoritesActivity.class));
        intents.add(LOG_OUT, new Intent(context, LogoutActivity.class));
    }

    @Override
    public int getCount() {
        return (contents.size());
    }

    @Override
    public String getItem(int index) {
        return (contents.get(index));
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    private Intent getIntent(int index) {
        return intents.get(index);
    }

    @Override
    public View getView(int index, View old, ViewGroup parent) {
        View view;

        if (old != null) {
            view = old;
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listitem_drawer, parent, false);
        }

        String item = getItem(index);

        refreshView(view, index);
        refreshLabel(view, item);

        return view;
    }

    private void refreshView(View view, final int index) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(getIntent(index));
            }
        });
    }

    private void refreshLabel(View view, String label) {
        TextView textView = (TextView) view.findViewById(R.id.listitem_drawer_label);
        textView.setText(label);
        //label.setTextColor(ContextCompat.getColor(context, R.color.list_indice));
    }

    private void refreshImage() {
        //ImageView image = (ImageView) view.findViewById(R.id.listitem_drawer_image);
    }

}
