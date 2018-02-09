package fr.epicture.epicture.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import fr.epicture.epicture.R;

public class ShareAppsAdapter extends BaseAdapter {

    private final Context context;
    private final int shareCode;
    private final PackageManager packageManager;
    private final Intent shareIntent;
    private final List<ResolveInfo> shareList;

    public ShareAppsAdapter(Context context, File captureFile, int shareCode) {
        this.context = context;
        this.shareCode = shareCode;
        packageManager = context.getPackageManager();
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(captureFile));
        shareList = packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    @Override
    public int getCount() {
        return shareList.size();
    }

    @Override
    @NonNull
    public String getItem(int itemIndex) {
        return shareList.get(itemIndex).loadLabel(packageManager).toString();
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    private Drawable getIcon(int itemIndex) {
        return shareList.get(itemIndex).loadIcon(packageManager);
    }

    @NonNull
    public Intent getIntent(int itemIndex) {
        ResolveInfo info = shareList.get(itemIndex);
        String packageName = info.activityInfo.applicationInfo.packageName;
        return shareIntent.setPackage(packageName);
    }

    @Override
    @NonNull
    public View getView(int itemIndex, View itemView, ViewGroup parentView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.listitem_image, parentView, false);
        }
        ImageView itemImage = (ImageView) itemView.findViewById(R.id.listitem_image);
        itemImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        itemImage.setImageDrawable(getIcon(itemIndex));
        TextView itemLabel = (TextView) itemView.findViewById(R.id.listitem_label);
        itemLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary100));
        itemLabel.setText(getItem(itemIndex));
        return itemView;
    }
}
