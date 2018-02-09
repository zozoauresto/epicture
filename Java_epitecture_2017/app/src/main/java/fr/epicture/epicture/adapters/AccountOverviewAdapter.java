package fr.epicture.epicture.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;

public class AccountOverviewAdapter extends BaseAdapter {

    private final List<APIAccount> apis = new ArrayList<>();
    private final Context context;
    private Listener listener;

    public AccountOverviewAdapter(Context context, Listener listener) {
        super();
        this.context = context;
        this.listener = listener;
    }

    public void addItem(APIAccount api) {
        apis.add(api);
        notifyDataSetChanged();
    }

    public void addAll(Collection<APIAccount> accounts) {
        apis.clear();
        apis.addAll(accounts);
        notifyDataSetChanged();
    }

    public void clear() {
        apis.clear();
        notifyDataSetChanged();
    }

    public void set(String id, APIAccount value) {
        for (int i = 0 ; i < apis.size() ; ++i) {
            if (apis.get(i).id.equals(id)) {
                apis.set(i, value);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return apis.size() + 1;
    }

    @Override
    public APIAccount getItem(int i) {
        if (i < apis.size()) {
            return apis.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final View view = (convertView != null) ? convertView : LayoutInflater.from(context).inflate(R.layout.account_overview, viewGroup, false);
        final APIAccount account = getItem(i);

        refreshView(view, account);
        refreshUsername(view, account);
        refreshAvatar(view, account);

        return view;
    }

    private void refreshView(View view, APIAccount account) {
        if (account != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAccountClick(account);
                }
            });
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddAccountClick();
                }
            });
        }
    }

    private void refreshUsername(View view, APIAccount account) {
        TextView textView = (TextView)view.findViewById(R.id.account_name);

        if (account != null) {
            textView.setText(account.getUsername());
        } else {
            textView.setText(context.getString(R.string.add_account));
        }
    }

    private void refreshAvatar(View view, APIAccount account) {
        final CircleImageView image = (CircleImageView)view.findViewById(R.id.account_avatar);

        if (account != null) {
            image.setImageResource(R.drawable.placeholder);
            API api = APIManager.getSelectedAPI();
            api.loadUserAvatar(context, account, new LoadBitmapInterface() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    image.setImageBitmap(bitmap);
                }
            });
        } else {
            image.setImageResource(R.drawable.add_grey);
        }
    }

    public interface Listener {
        void onAccountClick(APIAccount account);
        void onAddAccountClick();
    }

}
