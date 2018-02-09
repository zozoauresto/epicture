package fr.epicture.epicture.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;

public class APIOverviewAdapter extends BaseAdapter {

    // ========================================================================
    // FIELDS
    // ========================================================================

    private final List<API> apis = new ArrayList<>();
    private final Context context;

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================

    public APIOverviewAdapter(Context context) {
        super();
        this.context = context;
    }

    // ========================================================================
    // METHODS
    // ========================================================================

    public void addItem(API api) {
        apis.add(api);
        notifyDataSetChanged();
    }

    public void addAll(Collection<API> apis) {
        this.apis.clear();
        this.apis.addAll(apis);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return apis.size();
    }

    @Override
    public API getItem(int i) {
        return apis.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final View view = (convertView != null) ? convertView : LayoutInflater.from(context).inflate(R.layout.api_overview, viewGroup, false);
        final API api = getItem(i);

        refreshName(view, api.getName());
        refreshLogo(view, api.getLogo());

        return view;
    }

    private void refreshName(View view, String name) {
        ((TextView)view.findViewById(R.id.api_name)).setText(name);
    }

    private void refreshLogo(View view, int drawable) {
        if (drawable > 0) {
            ((ImageView) view.findViewById(R.id.api_logo)).setImageResource(drawable);
        }
    }
}
