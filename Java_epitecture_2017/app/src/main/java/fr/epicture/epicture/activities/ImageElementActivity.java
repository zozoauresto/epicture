package fr.epicture.epicture.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.APICommentElement;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.asynctasks.ShareAsyncTask;
import fr.epicture.epicture.interfaces.AddCommentInterface;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.interfaces.LoadCommentElementInterface;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.interfaces.LoadUserInfoInterface;
import fr.epicture.epicture.utils.BitmapCache;
import fr.epicture.epicture.utils.DateTimeManager;
import fr.epicture.epicture.utils.StaticTools;

public class ImageElementActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_ELEMENT = "image";
    public static final String EXTRA_COMMENT = "comment";

    public static void setImageElement(Intent intent, APIImageElement element) {
        intent.putExtra(EXTRA_IMAGE_ELEMENT, element);
    }

    public static APIImageElement getImageElement(Intent intent) {
        return intent.getParcelableExtra(EXTRA_IMAGE_ELEMENT);
    }

    public static void setComment(Intent intent, boolean value) {
        intent.putExtra(EXTRA_COMMENT, value);
    }

    public static boolean getComment(Intent intent) {
        return intent.getBooleanExtra(EXTRA_COMMENT, false);
    }

    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView description;
    private TextView date;
    private EditText commentEditText;
    private Button commentSubmitButton;
    private ScrollView page;

    private APIImageElement element;
    private boolean comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_element_activity);

        element = getImageElement(getIntent());
        comment = getComment(getIntent());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ((TextView)toolbar.findViewById(R.id.picture_title)).setText(element.title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageView = (ImageView)findViewById(R.id.image);
        progressBar = (ProgressBar)findViewById(R.id.download_progress);
        description = (TextView)findViewById(R.id.description);
        date = (TextView)findViewById(R.id.date);
        commentEditText = (EditText)findViewById(R.id.comment);
        commentSubmitButton = (Button)findViewById(R.id.comment_submit);
        page = (ScrollView)findViewById(R.id.scrollview);

        page.setSmoothScrollingEnabled(true);

        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 3) {
                    commentSubmitButton.setEnabled(true);
                    commentSubmitButton.setTextColor(ContextCompat.getColor(ImageElementActivity.this, R.color.colorPrimary100));
                } else {
                    commentSubmitButton.setEnabled(false);
                    commentSubmitButton.setTextColor(ContextCompat.getColor(ImageElementActivity.this, R.color.grey_light_50));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        commentEditText.setText("");

        commentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComment();
            }
        });

        refresh();
    }

    private void refresh() {
        BitmapCache.deleteAllCache();
        refreshImage();
        refreshDescription();
        refreshDate();
        refreshTags();
        refreshOwner();
        refreshComments();
        refreshIcons();
    }

    @Override
    public boolean onSupportNavigateUp() {
        StaticTools.hideSoftKeyboard(this, commentEditText);
        finish();
        return true;
    }

    private void refreshDescription() {
        description.setText(Html.fromHtml(element.description));
    }

    private void refreshDate() {
        date.setText(DateTimeManager.getUserFriendlyDateTime(this, element.date));
    }

    private void refreshImage() {
        API api = APIManager.getSelectedAPI();
        progressBar.setVisibility(View.VISIBLE);
        api.loadImage(this, element, new LoadBitmapInterface() {
            @Override
            public void onFinish(Bitmap bitmap) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageElementActivity.this, ImageActivity.class);
                ImageActivity.setImageElement(intent, element);
                startActivity(intent);
            }
        });
    }

    private void refreshTags() {
        ViewGroup container = (ViewGroup)findViewById(R.id.tags_container);
        container.setVisibility(View.VISIBLE);
        if (element.tags.trim().length() > 0) {
            container.setVisibility(View.VISIBLE);
            String[] tags = element.tags.split(" ");

            container.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            for (String tag : tags) {
                View view = inflater.inflate(R.layout.tags_preview, container, false);

                ((TextView) view.findViewById(R.id.tags)).setText(tag);
                container.addView(view);
            }
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private void refreshOwner() {
        final TextView ownerName = (TextView)findViewById(R.id.owner_name);
        final ImageView ownerPicture = (ImageView)findViewById(R.id.owner_picture);

        ownerName.setText(element.ownername);
        findViewById(R.id.expand).setVisibility(View.GONE);

        ownerPicture.setImageResource(R.drawable.placeholder);
        API api = APIManager.getSelectedAPI();
        api.loadUserInformation(this, element.ownerid, new LoadUserInfoInterface() {
            @Override
            public void onFinish(String id, APIAccount result) {
                api.loadUserAvatar(ImageElementActivity.this, result, new LoadBitmapInterface() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        ownerPicture.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    private void refreshComments() {
        API api = APIManager.getSelectedAPI();
        APIAccount account = api.getCurrentAccount();

        ViewGroup container = (ViewGroup)findViewById(R.id.comments_container);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.comments_progress);

        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        api.getComments(this, account.getID(), element.getID(), new LoadCommentElementInterface() {
            @Override
            public void onFinish(List<APICommentElement> datas, boolean error) {
                if (!error) {
                    container.removeAllViews();

                    LayoutInflater inflater = LayoutInflater.from(ImageElementActivity.this);
                    for (APICommentElement commentElement : datas) {
                        addComments(inflater, container, commentElement);
                    }
                    container.setVisibility(View.VISIBLE);
                }
                if (comment) {
                    commentEditText.clearFocus();
                    commentEditText.requestLayout();
                    StaticTools.showSoftKeyboard(ImageElementActivity.this, commentEditText);
                    comment = false;
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addComments(LayoutInflater inflater, ViewGroup container, APICommentElement commentElement) {
        View view = inflater.inflate(R.layout.comment_preview, container, false);

        ((TextView)view.findViewById(R.id.content)).setText(commentElement.content);
        ((TextView)view.findViewById(R.id.user_name)).setText(commentElement.authorName);

        API api = APIManager.getSelectedAPI();
        api.loadUserAvatar(this, commentElement, new LoadBitmapInterface() {
            @Override
            public void onFinish(Bitmap bitmap) {
                ((CircleImageView)view.findViewById(R.id.user_picture)).setImageBitmap(bitmap);
            }
        });

        container.addView(view);
    }

    private void refreshComment(APICommentElement element) {
        boolean submitting = element == null;
        commentEditText.setEnabled(!submitting);
        commentSubmitButton.setEnabled(!submitting);

        if (submitting) {
            commentSubmitButton.setTextColor(ContextCompat.getColor(ImageElementActivity.this, R.color.grey_light_50));
            page.post(new Runnable() {
                @Override
                public void run() {
                    page.fullScroll(View.FOCUS_DOWN);
                }
            });
        } else {
            ViewGroup container = (ViewGroup)findViewById(R.id.comments_container);
            LayoutInflater inflater = LayoutInflater.from(ImageElementActivity.this);

            addComments(inflater, container, element);
            container.setVisibility(View.VISIBLE);
            commentSubmitButton.setTextColor(ContextCompat.getColor(ImageElementActivity.this, R.color.colorPrimary100));
            page.post(new Runnable() {
                @Override
                public void run() {
                    page.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    private void refreshIcons() {
        View commentContainer = findViewById(R.id.comment_container);
        View favoriteContainer = findViewById(R.id.favorite_container);
        View shareContainer = findViewById(R.id.share_container);

        ImageView favoriteIcon = (ImageView)findViewById(R.id.favorite_icon);

        API api = APIManager.getSelectedAPI();

        if (element.favorite) {
            favoriteIcon.setImageResource(R.mipmap.ic_star_on);
        } else {
            favoriteIcon.setImageResource(R.mipmap.ic_star_off);
        }

        commentEditText.clearFocus();
        commentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditText.clearFocus();
                commentEditText.requestFocus();
                StaticTools.showSoftKeyboard(ImageElementActivity.this, commentEditText);
            }
        });

        favoriteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API api = APIManager.getSelectedAPI();
                APIAccount account = api.getCurrentAccount();

                if (!element.favorite) {
                    api.addFavorite(ImageElementActivity.this, account.getID(), element.getID(), new LoadTextInterface() {
                        @Override
                        public void onFinish(String text) {
                            element.favorite = true;
                            favoriteIcon.setImageResource(R.mipmap.ic_star_on);
                        }
                    });
                } else {
                    api.deleteFavorite(ImageElementActivity.this, account.getID(), element.getID(), new LoadTextInterface() {
                        @Override
                        public void onFinish(String text) {
                            element.favorite = false;
                            favoriteIcon.setImageResource(R.mipmap.ic_star_off);
                        }
                    });
                }
            }
        });

        shareContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAsyncTask(ImageElementActivity.this, element).execute();
            }
        });
    }

    private void submitComment() {
        API api = APIManager.getSelectedAPI();
        APIAccount account = api.getCurrentAccount();
        api.addComment(this, account.getID(), element.getID(), commentEditText.getText().toString(), new AddCommentInterface() {
            @Override
            public void onFinish(APICommentElement element) {
                commentEditText.setText("");
                try {
                    refreshComment(element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        refreshComment(null);
    }
}
