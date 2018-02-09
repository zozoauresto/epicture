package fr.epicture.epicture.recyclers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import fr.epicture.epicture.R;
import fr.epicture.epicture.api.API;
import fr.epicture.epicture.api.APIAccount;
import fr.epicture.epicture.api.APIImageElement;
import fr.epicture.epicture.api.APIManager;
import fr.epicture.epicture.asynctasks.ShareAsyncTask;
import fr.epicture.epicture.interfaces.ImageListAdapterInterface;
import fr.epicture.epicture.interfaces.LoadBitmapInterface;
import fr.epicture.epicture.interfaces.LoadTextInterface;
import fr.epicture.epicture.interfaces.LoadUserInfoInterface;
import fr.epicture.epicture.interfaces.PhotoIsInFavoritesInterface;
import fr.epicture.epicture.utils.DateTimeManager;

public class ImageListRecyclerItemViewHolder extends RecyclerView.ViewHolder {

    private static Map<String, Boolean> FavoritesArray = new HashMap<>();

    private Activity activity;
    private View parent;
    private ImageListAdapterInterface listener;

    private ImageListRecyclerItemViewHolder(Activity activity, View parent, ImageListAdapterInterface listener) {
        super(parent);
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;
    }

    public static ImageListRecyclerItemViewHolder newInstance(Activity activity, View parent, ImageListAdapterInterface listener) {
        return (new ImageListRecyclerItemViewHolder(activity, parent, listener));
    }

    public void refreshView(APIImageElement imageElement, boolean isFooter) {
        if (isFooter) {
            parent.findViewById(R.id.footer).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.footer).setVisibility(View.GONE);
        }

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean favorite = FavoritesArray.get(imageElement.getID());
                imageElement.favorite = (favorite != null) ? favorite : false;
                listener.onImageClick(imageElement);
            }
        });
    }

    public void refreshImage(APIImageElement element) {
        final ImageView imageView = (ImageView)parent.findViewById(R.id.image);
        final View imageContainer = parent.findViewById(R.id.image_container);
        final ProgressBar progressBar = (ProgressBar)parent.findViewById(R.id.download_progress);
        final float height = element.getHeightSize();

        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        DisplayMetrics screen = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(screen);

        if (height > 0) {
            imageContainer.getLayoutParams().width = screen.widthPixels;
            imageContainer.getLayoutParams().height = (int)height;
            imageContainer.requestLayout();
        }

        API api = APIManager.getSelectedAPI();
        api.loadImage(activity, element, new LoadBitmapInterface() {
            @Override
            public void onFinish(Bitmap bitmap){
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshDescription(APIImageElement element) {
        final TextView description = (TextView) parent.findViewById(R.id.description);

        description.setEllipsize(TextUtils.TruncateAt.END);
        description.setMaxLines(3);
        if (element.description != null) {
            description.setText(Html.fromHtml(element.description));
        }
    }

    public void refreshDate(APIImageElement element) {
        final TextView date = (TextView)parent.findViewById(R.id.date);
        date.setText(DateTimeManager.getUserFriendlyDateTime(activity, element.date));
    }

    public void refreshOwner(APIImageElement element) {
        final TextView ownerName = (TextView)parent.findViewById(R.id.owner_name);
        final ImageView ownerPicture = (ImageView)parent.findViewById(R.id.owner_picture);

        ownerName.setText(element.ownername);

        parent.findViewById(R.id.header_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API api = APIManager.getSelectedAPI();
                api.deletePhoto(activity, element.getID(), api.getCurrentAccount().getID(), new LoadTextInterface() {
                    @Override
                    public void onFinish(String text) {
                        listener.onImageDelete(element);
                    }
                });
            }
        });

        if (element.ownerid.equals(APIManager.getSelectedAPI().getCurrentAccount().id)) {
            parent.findViewById(R.id.expand).setVisibility(View.VISIBLE);
        } else {
            parent.findViewById(R.id.expand).setVisibility(View.GONE);
        }

        ownerPicture.setImageResource(R.drawable.placeholder);
        API api = APIManager.getSelectedAPI();
        api.loadUserInformation(activity, element.ownerid, new LoadUserInfoInterface() {
            @Override
            public void onFinish(String id, APIAccount result) {
                api.loadUserAvatar(activity, result, new LoadBitmapInterface() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        ownerPicture.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    public void refreshIcons(APIImageElement element) {
        View commentContainer = parent.findViewById(R.id.comment_container);
        View favoriteContainer = parent.findViewById(R.id.favorite_container);
        View shareContainer = parent.findViewById(R.id.share_container);

        ImageView favoriteIcon = (ImageView)parent.findViewById(R.id.favorite_icon);

        API api = APIManager.getSelectedAPI();
        APIAccount account = api.getCurrentAccount();

        if (element.isFavorite()) {
            favoriteIcon.setImageResource(R.mipmap.ic_star_on);
        } else {
            api.isFavorite(activity, account.getID(), element.getID(), new PhotoIsInFavoritesInterface() {
                @Override
                public void onFinish(boolean response) {
                    if (response) {
                        favoriteIcon.setImageResource(R.mipmap.ic_star_on);
                    } else {
                        favoriteIcon.setImageResource(R.mipmap.ic_star_off);
                    }
                    FavoritesArray.put(element.getID(), response);
                }
            });
        }

        commentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCommentClick(element);
            }
        });

        favoriteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API api = APIManager.getSelectedAPI();
                APIAccount account = api.getCurrentAccount();

                Boolean inFavorite = FavoritesArray.get(element.getID());
                if (inFavorite == null || !inFavorite) {
                    api.addFavorite(activity, account.getID(), element.getID(), new LoadTextInterface() {
                        @Override
                        public void onFinish(String text) {
                            FavoritesArray.put(element.getID(), true);
                            favoriteIcon.setImageResource(R.mipmap.ic_star_on);
                        }
                    });
                } else {
                    api.deleteFavorite(activity, account.getID(), element.getID(), new LoadTextInterface() {
                        @Override
                        public void onFinish(String text) {
                            FavoritesArray.put(element.getID(), false);
                            favoriteIcon.setImageResource(R.mipmap.ic_star_off);
                        }
                    });
                }
            }
        });

        shareContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAsyncTask(activity, element).execute();
            }
        });
    }

}
