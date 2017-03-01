package silicar.tutu.universal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import silicar.tutu.universal.helper.*;
import silicar.tutu.universal.helper.base.BaseDisplay;

/**
 * Created by tutu on 2016/2/24.
 */
public class UniversalLinearLayout extends LinearLayout implements UniversalView {

    private final UniversalLayoutHelper mHelper = new UniversalLayoutHelper(this);

    public UniversalLinearLayout(Context context) {
        super(context);
    }

    public UniversalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public UniversalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UniversalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mHelper.init(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (getOrientation() == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new LayoutParams(getContext(), attrs, getAutoDisplay(), getAutoChildStyle());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int tmpHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int tmpWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);

        //fixed scrollview height problems
        if (heightMode == MeasureSpec.UNSPECIFIED && getParent() != null && (getParent() instanceof ScrollView))
        {
            int baseHeight = mHelper.getAutoDisplay().getDisplayHeight();
            tmpHeightMeasureSpec = MeasureSpec.makeMeasureSpec(baseHeight, heightMode);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED && getParent() != null && (getParent() instanceof HorizontalScrollView))
        {
            int baseWidth = mHelper.getAutoDisplay().getDisplayWidth();
            tmpWidthMeasureSpec = MeasureSpec.makeMeasureSpec(baseWidth, widthMode);
        }

        mHelper.adjustChildren(tmpWidthMeasureSpec, tmpHeightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHelper.handleMeasuredStateTooSmall())
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        mHelper.restoreOriginalParams();
    }

    @Override
    public BaseDisplay getAutoDisplay() {
        return mHelper.getAutoDisplay();
    }

    @Override
    public void setAutoDisplay(BaseDisplay display) {
        mHelper.setAutoDisplay(display);
    }

    @Override
    public int getAutoChildStyle() {
        return mHelper.getAutoChildStyle();
    }

    @Override
    public void setAutoChildStyle(@StyleRes int style) {
        mHelper.setAutoChildStyle(style);
    }


    public static class LayoutParams extends LinearLayout.LayoutParams
            implements UniversalLayoutParams
    {
        private UniversalLayoutInfo mUniversalLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            this(c, attrs, BaseDisplay.getInstance(), 0);
        }

        public LayoutParams(Context c, AttributeSet attrs, BaseDisplay display, int style) {
            super(c, attrs);
            mUniversalLayoutInfo = UniversalLayoutHelper.getUniversalLayoutInfo(c, attrs, display, style);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LinearLayout.LayoutParams source) {
            super((MarginLayoutParams)source);
            this.weight = source.weight;
            this.gravity = source.gravity;
        }

        public LayoutParams(UniversalLinearLayout.LayoutParams source) {
            super((MarginLayoutParams)source);
            this.weight = source.weight;
            this.gravity = source.gravity;
            this.mUniversalLayoutInfo = source.mUniversalLayoutInfo;
       }

        @Override
        public UniversalLayoutInfo getUniversalLayoutInfo()
        {
            if (mUniversalLayoutInfo == null) {
                mUniversalLayoutInfo = new UniversalLayoutInfo();
            }
            return mUniversalLayoutInfo;
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr)
        {
            UniversalLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }

    }
}
