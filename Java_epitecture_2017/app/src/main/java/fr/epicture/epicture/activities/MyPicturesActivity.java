package fr.epicture.epicture.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import fr.epicture.epicture.R;
import fr.epicture.epicture.adapters.PhotoAppsAdapter;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.fragments.ImageListFragment;
import fr.epicture.epicture.interfaces.ImageListInterface;
import fr.epicture.epicture.interfaces.LoadImageElementInterface;
import fr.epicture.epicture.interfaces.RetractableToolbarInterface;
import fr.epicture.epicture.utils.BitmapCache;
import fr.epicture.epicture.utils.StaticTools;

public class MyPicturesActivity extends AppCompatActivity implements ImageListInterface, RetractableToolbarInterface {

    private static final String TEMP_FOLDER = "temp";

    private final static String PHOTO_PREFIX = "photo";
    private final static String PHOTO_SUFFIX = ".jpg";

    private static final int REQUEST_ADD_PICTURE = 1;
    private static final int REQUEST_CODE_PHOTO_CAPTURE = 2;
    private static final int REQUEST_CODE_PHOTO_IMPORT = 3;

    private FloatingActionButton addFab;

    private PhotoAppsAdapter publishAdapter;

    private ImageListFragment imageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pictures_activity);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        addFab = (FloatingActionButton)findViewById(R.id.add_item);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClick();
            }
        });

        BitmapCache.deleteAllCache();
        refreshFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PHOTO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                addPhotoCallback(Uri.fromFile(getCapturePhotoFile()));
            }
        } else if (requestCode == REQUEST_CODE_PHOTO_IMPORT) {
            if (resultCode == RESULT_OK && data != null) {
                addPhotoCallback(data.getData());
            }
        } else if (requestCode == REQUEST_ADD_PICTURE) {
            if (resultCode == RESULT_OK) {
                imageListFragment.refresh();
                Toast.makeText(this, "upload successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(publishAdapter.getIntent(0), publishAdapter.getRequestCode(0));
                final LinearLayout popUpBg = (LinearLayout) findViewById(R.id.add_image_parent);
                popUpBg.setVisibility(View.GONE);
            }
        }
    }

    private void refreshFragment() {
        imageListFragment = new ImageListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, imageListFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            API api = APIManager.getSelectedAPI();
            SearchActivity.setUserID(intent, api.getCurrentAccount().getID());
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestImageList(int page, String search, String userID) {
        API api = APIManager.getSelectedAPI();
        api.getMyPictures(this, page, new LoadImageElementInterface() {
            @Override
            public void onFinish(List<APIImageElement> result, boolean error) {
                if (!error) {
                    imageListFragment.refreshList(result);
                } else {
                    imageListFragment.refreshList(null);
                }
            }
        });
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

    private void onAddButtonClick() {
        selectPhotoSource();
    }

    private void selectPhotoSource() {
        File captureFile = getCapturePhotoFile();
        if (captureFile != null) {
            ListView publishList = (ListView) findViewById(R.id.publish_popup_list);
            final LinearLayout popUpBg = (LinearLayout) findViewById(R.id.add_image_parent);

            popUpBg.setVisibility(View.VISIBLE);

            publishAdapter = new PhotoAppsAdapter(this, captureFile,
                    REQUEST_CODE_PHOTO_CAPTURE, REQUEST_CODE_PHOTO_IMPORT);
            publishList.setAdapter(publishAdapter);
            publishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (publishAdapter.getPage() == 0) {
                        if (position == 0) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MyPicturesActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                            } else {
                                startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                                popUpBg.setVisibility(View.GONE);
                            }
                        } else if (position == 1) {
                            publishAdapter.nextPage();
                        } else {
                            publishAdapter.setPage(2);
                        }
                    } else if (publishAdapter.getPage() == 1) {
                        startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                        publishAdapter.setPage(0);
                        popUpBg.setVisibility(View.GONE);
                    } else if (publishAdapter.getPage() == 2) {
                        startActivityForResult(publishAdapter.getIntent(position), publishAdapter.getRequestCode(position));
                        publishAdapter.setPage(0);
                        popUpBg.setVisibility(View.GONE);
                    }
                }
            });

            popUpBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpBg.setVisibility(View.GONE);
                }
            });
        }
    }

    private File getCapturePhotoFile() {
        File dir = getTempPhotoDir();
        if (dir != null) {
            dir.mkdirs();
            return new File(dir, PHOTO_PREFIX + PHOTO_SUFFIX);
        }
        return null;
    }

    @Nullable
    private File getTempPhotoDir() {
        File dir = getExternalCacheDir();
        if (dir != null) {
            String path = dir.getAbsolutePath();
            path += File.separator + TEMP_FOLDER;
            return new File(path);
        }
        return null;
    }

    private File createTempPhotoFile() {
        File dir = getTempPhotoDir();
        if (dir != null) {
            dir.mkdirs();
            try {
                return File.createTempFile(PHOTO_PREFIX, PHOTO_SUFFIX, dir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void addPhotoCallback(Uri uri) {
        InputStream input = null;
        try {
            input = getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (input == null) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        final String extension = uri.toString().substring(uri.toString().length() - 4);
        File file = null;
        int pos = uri.toString().indexOf(".");

        if (pos == -1 || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".png") || extension.equalsIgnoreCase(".jpeg")) {
            file = createTempPhotoFile();
        }

        if (file == null) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean copied = StaticTools.copyStreamToFile(input, file);
        if (!copied) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            return;
        }
        String photoToUpload = file.getAbsolutePath();
        rotateImage(photoToUpload);

        final String ext = photoToUpload.substring(photoToUpload.lastIndexOf('.'));
        if ((ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".png")
                || ext.equalsIgnoreCase(".jpg")) && !uri.toString().contains("video")) {
            Intent intent = new Intent(this, AddPictureActivity.class);
            AddPictureActivity.setPhotos(intent, photoToUpload);
            startActivityForResult(intent, REQUEST_ADD_PICTURE);
        } else {
            Toast.makeText(this, "Format de fichier non pris en charge", Toast.LENGTH_SHORT).show();
        }
    }

    private void rotateImage(String photo) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(photo);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            if (orientation != 0) {
                File file = new File(photo);
                Bitmap bitmap = BitmapFactory.decodeFile(photo);
                Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                FileOutputStream out = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                scaledBitmap.recycle();
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHideToolbar() {
        AppBarLayout toolbar = (AppBarLayout)findViewById(R.id.appbarlayout);
        toolbar.animate().setDuration(200).translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_item);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    @Override
    public void onShowToolbar() {
        AppBarLayout toolbar = (AppBarLayout)findViewById(R.id.appbarlayout);
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_item);
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
}
