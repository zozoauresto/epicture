package fr.epicture.epicture.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.utils.BitmapCache;

public class UploadImageActivity extends AppCompatActivity {

    public static final String EXTRA_PATH = "photo.path";
    public static final String EXTRA_TITLE = "photo.title";
    public static final String EXTRA_DESCRIPTION = "photo.description";
    public static final String EXTRA_TAGS = "photo.tags";

    public static void setPhotoPath(Intent intent, String value) {
        intent.putExtra(EXTRA_PATH, value);
    }

    public static String getPhotoPath(Intent intent) {
        return intent.getStringExtra(EXTRA_PATH);
    }

    public static void setTitle(Intent intent, String value) {
        intent.putExtra(EXTRA_TITLE, value);
    }

    public static String getTitle(Intent intent) {
        return intent.getStringExtra(EXTRA_TITLE);
    }

    public static void setDescription(Intent intent, String value) {
        intent.putExtra(EXTRA_DESCRIPTION, value);
    }

    public static String getDescription(Intent intent) {
        return intent.getStringExtra(EXTRA_DESCRIPTION);
    }

    public static void setTags(Intent intent, String value) {
        intent.putExtra(EXTRA_TAGS, value);
    }

    public static String getTags(Intent intent) {
        return intent.getStringExtra(EXTRA_TAGS);
    }

    private String path;
    private String title;
    private String description;
    private String tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        path = getPhotoPath(getIntent());
        title = getTitle(getIntent());
        description = getDescription(getIntent());
        tags = getTags(getIntent());

        upload();
    }

    private void upload() {
        API api = APIManager.getSelectedAPI();
        BitmapCache.deleteAllCache();
        api.uploadImage(this, api.getCurrentAccount(), path, title, description, tags, new LoadTextInterface() {
            @Override
            public void onFinish(String text) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
