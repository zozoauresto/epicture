package fr.epicture.epicture.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import fr.epicture.epicture.R;
import fr.epicture.epicture.utils.BitmapCache;

public class AddPictureActivity extends AppCompatActivity {

    private static final int REQUEST_UPLOAD = 1;

    public static final String EXTRA_PHOTOS = "addpicture.photos";

    @NonNull
    public static String getPhotos(@NonNull Intent intent) {
        return intent.getStringExtra(EXTRA_PHOTOS);
    }

    public static void setPhotos(@NonNull Intent intent, @Nullable String value) {
        intent.putExtra(EXTRA_PHOTOS, value);
    }

    private EditText titleEditText;
    private ImageView imageView;
    private EditText descriptionEditText;
    private EditText tagsEditText;

    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_picture_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleEditText = (EditText)findViewById(R.id.title);
        imageView = (ImageView)findViewById(R.id.picture_preview);
        descriptionEditText = (EditText)findViewById(R.id.description);
        tagsEditText = (EditText)findViewById(R.id.tags);

        photo = getPhotos(getIntent());
        refreshPhoto();
    }

    @Override
    public boolean onSupportNavigateUp() {
        confirmFinish(true);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPLOAD) {
            if (resultCode == RESULT_OK) {
                BitmapCache.deleteAllCache();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send) {
            if (validPhotos() && validTitle() && validDescription()) {
                submitContentRequest();
            }
            return true;
        }
        return false;
    }

    private void confirmFinish(boolean finish) {
        if (finish) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.publish_cancel)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmFinish(false);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            finish();
        }
    }

    private boolean validPhotos() {
        return new File(getPhotos(getIntent())).exists();
    }

    private boolean validTitle() {
        if (titleEditText.getText().toString().trim().length() == 0) {
            titleEditText.setError(getResources().getString(R.string.error_empty));
            return false;
        }
        titleEditText.setError(null);
        return true;
    }

    private boolean validDescription() {
        if (descriptionEditText.getText().toString().trim().length() == 0) {
            descriptionEditText.setError(getResources().getString(R.string.error_empty));
            return false;
        }
        descriptionEditText.setError(null);
        return true;
    }

    private void submitContentRequest() {
        Intent intent = new Intent(this, UploadImageActivity.class);
        UploadImageActivity.setTitle(intent, titleEditText.getText().toString());
        UploadImageActivity.setPhotoPath(intent, photo);
        UploadImageActivity.setDescription(intent, descriptionEditText.getText().toString());
        UploadImageActivity.setTags(intent, tagsEditText.getText().toString());
        startActivityForResult(intent, REQUEST_UPLOAD);
    }

    private void refreshPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(photo);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
