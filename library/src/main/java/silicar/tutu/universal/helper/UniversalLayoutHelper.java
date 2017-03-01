package silicar.tutu.universal.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StyleRes;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import silicar.tutu.universal.R;
import silicar.tutu.universal.helper.base.BaseDisplay;

/**
 * Created by Brady on 2016/5/2.
 */
public class UniversalLayoutHelper implements UniversalView {
    private static final String TAG = "UniversalLayout";

    private ViewGroup mHost;
    private int mStyle;
    private BaseDisplay mDisplay;
    private UniversalLayoutMeasure universalLayoutMeasure;

    public UniversalLayoutHelper(ViewGroup host) {
        mHost = host;
        universalLayoutMeasure = new UniversalLayoutMeasure(mHost);
    }

    ///////// 子控件参数获取解析 ///////////

    /**
     * Constructs a UniversalLayoutInfo from attributes associated with a View. Call this method from
     * {@code LayoutParams(Context c, AttributeSet attrs)} constructor.
     */
    public static UniversalLayoutInfo getUniversalLayoutInfo(Context context, AttributeSet attrs, BaseDisplay display, int style) {
        //if (universalAttrs == null)
        //    universalAttrs = new UniversalAttrs(context, mDisplay);
        UniversalAttrs universalAttrs = new UniversalAttrs(context, display);
        universalAttrs.obtainStyledAttributes(attrs, 0, style);
        return universalAttrs.getUniversalLayoutInfo();
    }

    /**
     * Helper method to be called from {@link ViewGroup.LayoutParams#setBaseAttributes} override
     * that reads layout_width and layout_height attribute values without throwing an exception if
     * they aren't present.
     */
    public static void fetchWidthAndHeight(ViewGroup.LayoutParams params, TypedArray array,
                                           int widthAttr, int heightAttr) {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    ///////// child样式参数获取解析 ///////////

    public void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UniversalLayout, defStyleAttr, defStyleRes);
        mStyle = array.getResourceId(R.styleable.UniversalLayout_childStyle, 0);
        array.recycle();
    }

    @Override
    public int getAutoChildStyle() {
        return mStyle;
    }

    @Override
    public void setAutoChildStyle(@StyleRes int style) {
        mStyle = style;
    }

    @Override
    public BaseDisplay getAutoDisplay() {
        if (mDisplay == null) {
            return BaseDisplay.getInstance();
        }
        return mDisplay;
    }

    @Override
    public void setAutoDisplay(BaseDisplay display) {
        mDisplay = display;
    }

    ///////// 测量计算 ///////////

    /**
     * Iterates over children and changes their width and height to one calculated from percentage
     * values.
     *
     * @param widthMeasureSpec  Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    public void adjustChildren(int widthMeasureSpec, int heightMeasureSpec) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "fillLayout: " + mHost + " widthMeasureSpec: "
                    + View.MeasureSpec.toString(widthMeasureSpec) + " heightMeasureSpec: "
                    + View.MeasureSpec.toString(heightMeasureSpec));
        }
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);

        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "widthHint = " + widthHint + " , heightHint = " + heightHint);

        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "should adjust " + view + " " + params);
            }

            if (params instanceof UniversalLayoutParams) {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "using " + info);
                }
                if (info != null) {
                    universalLayoutMeasure.fillLayout(widthHint, heightHint, view);
                }
            }
        }
    }

    ///////// 错误比较 ///////////

    /**
     * Iterates over children and checks if any of them would like to get more space than it
     * received through the percentage dimension.
     * <p/>
     * If you are building a layout that supports percentage dimensions you are encouraged to take
     * advantage of this method. The developer should be able to specify that a child should be
     * remeasured by adding normal dimension attribute with {@code wrap_content} value. For example
     * he might specify child's attributes as {@code app:layout_widthUniversal="60%p"} and
     * {@code android:layout_width="wrap_content"}. In this case if the child receives too little
     * space, it will be remeasured with width set to {@code WRAP_CONTENT}.
     *
     * @return True if the measure phase needs to be rerun because one of the children would like
     * to receive more space.
     */
    public boolean handleMeasuredStateTooSmall() {
        boolean needsSecondMeasure = false;
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "should handle measured state too small " + view + " " + params);
            }
            if (params instanceof UniversalLayoutParams) {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (info != null) {
                    if (shouldHandleMeasuredWidthTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
            }
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "should trigger second measure pass: " + needsSecondMeasure);
        }
        return needsSecondMeasure;
    }

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, UniversalLayoutInfo info) {
        int state = ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.width == null) {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.width.getMeasureValue() >= 0 &&
                info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, UniversalLayoutInfo info) {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.height == null) {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.height.getMeasureValue() >= 0 &&
                info.mPreservedParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    ///////// 重置参数 ///////////

    /**
     * Iterates over children and restores their original dimensions that were changed for
     * percentage values. Calling this method only makes sense if you previously called
     * {@link UniversalLayoutHelper#adjustChildren(int, int)}.
     */
    public void restoreOriginalParams() {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "should restore " + view + " " + params);
            }
            if (params instanceof UniversalLayoutParams) {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "using " + info);
                }
                if (info != null) {
                    if (params instanceof ViewGroup.MarginLayoutParams) {
                        restoreMarginLayoutParams((ViewGroup.MarginLayoutParams) params, info);
                    } else {
                        restoreLayoutParams(params, info);
                    }
                }
            }
        }
    }

    /**
     * Restores original dimensions and margins after they were changed for percentage based
     * values. Calling this method only makes sense if you previously called
     * {@link UniversalLayoutMeasure#fillLayout}.
     */
    public void restoreMarginLayoutParams(ViewGroup.MarginLayoutParams params, UniversalLayoutInfo info) {
        restoreLayoutParams(params, info);
        params.leftMargin = info.mPreservedParams.leftMargin;
        params.topMargin = info.mPreservedParams.topMargin;
        params.rightMargin = info.mPreservedParams.rightMargin;
        params.bottomMargin = info.mPreservedParams.bottomMargin;
        MarginLayoutParamsCompat.setMarginStart(params,
                MarginLayoutParamsCompat.getMarginStart(info.mPreservedParams));
        MarginLayoutParamsCompat.setMarginEnd(params,
                MarginLayoutParamsCompat.getMarginEnd(info.mPreservedParams));
    }

    /**
     * Restores original dimensions after they were changed for percentage based values. Calling
     * this method only makes sense if you previously called
     * {@link UniversalLayoutMeasure#fillLayout}.
     */
    public void restoreLayoutParams(ViewGroup.LayoutParams params, UniversalLayoutInfo info) {
        params.width = info.mPreservedParams.width;
        params.height = info.mPreservedParams.height;
    }
}
