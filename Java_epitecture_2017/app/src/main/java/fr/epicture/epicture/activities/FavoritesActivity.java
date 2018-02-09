package fr.epicture.epicture.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.fragments.ImageListFragment;
import fr.epicture.epicture.interfaces.ImageListInterface;
import fr.epicture.epicture.interfaces.LoadImageElementInterface;

public class FavoritesActivity extends AppCompatActivity implements ImageListInterface {

    private ImageListFragment imageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        refreshFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void refreshFragment() {
        imageListFragment = new ImageListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, imageListFragment).commit();
    }

    @Override
    public void onRequestImageList(int page, String search, String userID) {
        API api = APIManager.getSelectedAPI();
        APIAccount account = api.getCurrentAccount();

        api.getFavorites(this, account.getID(), page, new LoadImageElementInterface() {
            @Override
            public void onFinish(List<APIImageElement> datas, boolean error) {
                if (!error) {
                    imageListFragment.refreshList(datas);
                }
            }
        });
        imageListFragment.refreshList(null);
    }

    @Override
    public void onImageClick(APIImageElement element) {
        Intent intent = new Intent(this, ImageElementActivity.class);
        ImageElementActivity.setImageElement(intent, element);
        startActivity(intent);
    }

    @Override
    public void onCommentClick(APIImageElement element) {
        Intent intent = new Intent(this, ImageElementActivity.class);
        ImageElementActivity.setImageElement(intent, element);
        ImageElementActivity.setComment(intent, true);
        startActivity(intent);
    }

    @Override
    public void onHideToolbar() {
        AppBarLayout toolbar = (AppBarLayout)findViewById(R.id.appbarlayout);
        toolbar.animate().setDuration(200).translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void onShowToolbar() {
        AppBarLayout toolbar = (AppBarLayout)findViewById(R.id.appbarlayout);
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }
}
