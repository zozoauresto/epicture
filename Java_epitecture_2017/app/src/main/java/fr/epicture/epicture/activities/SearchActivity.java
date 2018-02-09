package fr.epicture.epicture.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.fragments.ImageListFragment;
import fr.epicture.epicture.interfaces.ImageListInterface;
import fr.epicture.epicture.interfaces.LoadImageElementInterface;

public class SearchActivity extends AppCompatActivity implements ImageListInterface {

    public static final String EXTRA_USERID = "userid";
    public static final String EXTRA_SEARCH = "toseach";

    public static void setUserID(Intent intent, String id) {
        intent.putExtra(EXTRA_USERID, id);
    }

    public static String getUserID(Intent intent) {
        return intent.getStringExtra(EXTRA_USERID);
    }

    public static void setSearch(Intent intent, String search) {
        intent.putExtra(EXTRA_SEARCH, search);
    }

    public static String getSearch(Intent intent) {
        return intent.getStringExtra(EXTRA_SEARCH);
    }

    private ImageListFragment imageListFragment;

    private String userID;
    private String toSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userID = getUserID(getIntent());}

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.search));

        searchView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary25));

        String search = getSearch(getIntent());
        if (search != null && !search.isEmpty()) {
            MenuItemCompat.expandActionView(searchItem);
            searchView.setQuery(search, true);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                if (search != null && search.length() > 0) {
                    setSearch(getIntent(), search);
                    refreshFragment(search);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Research size must be > 3", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                return false;
            }
        });

        return true;
    }

    private void refreshFragment(String search) {
        imageListFragment = new ImageListFragment();
        imageListFragment.setSearch(userID, search);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, imageListFragment).commit();
    }

    @Override
    public void onRequestImageList(int page, String search, String userID) {
        API api = APIManager.getSelectedAPI();
        api.search(this, search, userID, page, new LoadImageElementInterface() {
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
