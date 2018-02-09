package fr.epicture.epicture.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import fr.epicture.epicture.R;
import fr.epicture.epicture.utils.StaticTools;

public class AutoResizableGridView extends GridView {
    private float scale;
    private int nbLine;
    private float space;

    public AutoResizableGridView(Context context) {
        super(context);
        init();
    }

    public AutoResizableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoResizableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        scale = getContext().getResources().getDimension(R.dimen.gridview_photo_size);
        space = StaticTools.convertDpToPixel(4.0f, getContext());
        nbLine = 1;
    }

    public void setScale(int dp) {
        scale = StaticTools.convertDpToPixel((float)dp, getContext());
    }

    public void setNbLine(int nbLine) {
        this.nbLine = nbLine;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
