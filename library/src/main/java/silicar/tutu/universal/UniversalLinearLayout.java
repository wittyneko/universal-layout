package silicar.tutu.universal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by tutu on 2016/2/24.
 */
public class UniversalLinearLayout extends LinearLayout {

    private final UniversalLayoutHelper mHelper = new UniversalLayoutHelper(this);
    private int mStyle;

    public UniversalLinearLayout(Context context) {
        super(context);
    }

    public UniversalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public UniversalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UniversalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UniversalLayout, defStyleAttr, defStyleRes);
        mStyle = array.getResourceId(R.styleable.UniversalLayout_childStyle, 0);
        array.recycle();
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
        return new LayoutParams(getContext(), attrs, mStyle);
    }

    public int getChildStyle(){
        return mStyle;
    }

    public void setChildStyle(int style){
        mStyle = style;
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
            int baseHeight = mHelper.getHeightScreen();
            tmpHeightMeasureSpec = MeasureSpec.makeMeasureSpec(baseHeight, heightMode);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED && getParent() != null && (getParent() instanceof HorizontalScrollView))
        {
            int baseWidth = mHelper.getWidthScreen();
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


    public static class LayoutParams extends LinearLayout.LayoutParams
            implements UniversalLayoutHelper.UniversalLayoutParams
    {
        private UniversalLayoutHelper.UniversalLayoutInfo mUniversalLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            this(c, attrs, 0);
        }

        public LayoutParams(Context c, AttributeSet attrs, int style) {
            super(c, attrs);
            mUniversalLayoutInfo = UniversalLayoutHelper.getUniversalLayoutInfo(c, attrs, style);
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
        public UniversalLayoutHelper.UniversalLayoutInfo getUniversalLayoutInfo()
        {
            if (mUniversalLayoutInfo == null) {
                mUniversalLayoutInfo = new UniversalLayoutHelper.UniversalLayoutInfo();
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
