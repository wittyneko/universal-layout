/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package silicar.tutu.universal;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for layouts that want to support percentage based dimensions.
 * <p/>
 * <p>This class collects utility methods that are involved in extracting percentage based dimension
 * attributes and applying them to ViewGroup's children. If you would like to implement a layout
 * that supports percentage based dimensions, you need to take several steps:
 * <p/>
 * <ol>
 * <li> You need a {@link ViewGroup.LayoutParams} subclass in your ViewGroup that implements
 * {@link UniversalLayoutHelper.UniversalLayoutParams}.
 * <li> In your {@code LayoutParams(Context c, AttributeSet attrs)} constructor create an instance
 * of {@link UniversalLayoutHelper.UniversalLayoutInfo} by calling
 * {@link UniversalLayoutHelper#getUniversalLayoutInfo(Context, AttributeSet)}. Return this
 * object from {@code public UniversalLayoutHelper.UniversalLayoutInfo getUniversalLayoutInfo()}
 * method that you implemented for {@link UniversalLayoutHelper.UniversalLayoutParams} interface.
 * <li> Override
 * {@link ViewGroup.LayoutParams#setBaseAttributes(TypedArray, int, int)}
 * with a single line implementation {@code UniversalLayoutHelper.fetchWidthAndHeight(this, a,
 * widthAttr, heightAttr);}
 * <li> In your ViewGroup override {@link ViewGroup#generateLayoutParams(AttributeSet)} to return
 * your LayoutParams.
 * <li> In your {@link ViewGroup#onMeasure(int, int)} override, you need to implement following
 * pattern:
 * <pre class="prettyprint">
 * protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 * mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
 * super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 * if (mHelper.handleMeasuredStateTooSmall()) {
 * super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 * }
 * }
 * </pre>
 * <li>In your {@link ViewGroup#onLayout(boolean, int, int, int, int)} override, you need to
 * implement following pattern:
 * <pre class="prettyprint">
 * protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
 * super.onLayout(changed, left, top, right, bottom);
 * mHelper.restoreOriginalParams();
 * }
 * </pre>
 * </ol>
 */
public class UniversalLayoutHelper
{
    private static final String TAG = "UniversalLayout";

    private final ViewGroup mHost;

    private static int mWidthScreen;
    private static int mHeightScreen;

    public UniversalLayoutHelper(ViewGroup host)
    {
        mHost = host;
        getDefaultSize();
    }

    private void getDefaultSize()
    {
//        WindowManager wm = (WindowManager) mHost.getContext().getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        mWidthScreen = outMetrics.widthPixels;
//        mHeightScreen = outMetrics.heightPixels;
        mHeightScreen = mHost.getResources().getDisplayMetrics().heightPixels;
        mWidthScreen = mHost.getResources().getDisplayMetrics().widthPixels;
    }

    public int getWidthScreen() {
        return mWidthScreen;
    }

    public int getHeightScreen() {
        return mHeightScreen;
    }

    /**
     * Helper method to be called from {@link ViewGroup.LayoutParams#setBaseAttributes} override
     * that reads layout_width and layout_height attribute values without throwing an exception if
     * they aren't present.
     */
    public static void fetchWidthAndHeight(ViewGroup.LayoutParams params, TypedArray array,
                                           int widthAttr, int heightAttr)
    {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    /**
     * Iterates over children and changes their width and height to one calculated from percentage
     * values.
     *
     * @param widthMeasureSpec  Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    public void adjustChildren(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "adjustChildren: " + mHost + " widthMeasureSpec: "
                    + View.MeasureSpec.toString(widthMeasureSpec) + " heightMeasureSpec: "
                    + View.MeasureSpec.toString(heightMeasureSpec));
        }
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);

        if (Log.isLoggable(TAG, Log.DEBUG))
            Log.d(TAG, "widthHint = " + widthHint + " , heightHint = " + heightHint);

        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();

            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should adjust " + view + " " + params);
            }

            if (params instanceof UniversalLayoutParams)
            {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "using " + info);
                }
                if (info != null)
                {
                    fillTextSize(widthHint, heightHint, view, info);
                    fillPadding(widthHint, heightHint, view, info);
                    fillMinOrMaxDimension(widthHint, heightHint, view, info);

                    if (params instanceof ViewGroup.MarginLayoutParams)
                    {
                        fillMarginLayoutParams((ViewGroup.MarginLayoutParams) params,
                                widthHint, heightHint, info);
                    } else
                    {
                        fillLayoutParams(params, widthHint, heightHint, info);
                    }
                }
            }
        }


    }

    /**
     * Iterates over children and restores their original dimensions that were changed for
     * percentage values. Calling this method only makes sense if you previously called
     * {@link UniversalLayoutHelper#adjustChildren(int, int)}.
     */
    public void restoreOriginalParams()
    {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should restore " + view + " " + params);
            }
            if (params instanceof UniversalLayoutParams)
            {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "using " + info);
                }
                if (info != null)
                {
                    if (params instanceof ViewGroup.MarginLayoutParams)
                    {
                        restoreMarginLayoutParams((ViewGroup.MarginLayoutParams) params, info);
                    } else
                    {
                        restoreLayoutParams(params, info);
                    }
                }
            }
        }
    }


    ///////////// 计算过程 /////////////

    public void fillTextSize(int widthHint, int heightHint, View view, UniversalLayoutInfo info)
    {
        //textsize value support

        UniversalLayoutInfo.UniversalVal textSizeUniversal = info.textSizeUniversal;
        if (textSizeUniversal == null) return;

        float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, textSizeUniversal);
        float textSize = (int) (base * textSizeUniversal.value);

        //Button 和 EditText 是TextView的子类
        if (view instanceof TextView)
        {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    public void fillPadding(int widthHint, int heightHint, View view, UniversalLayoutInfo info)
    {
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), top = view.getPaddingTop(), bottom = view.getPaddingBottom();
        UniversalLayoutInfo.UniversalVal universalVal = info.paddingLeftUniversal;
        if (universalVal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, universalVal);
            left = (int) (base * universalVal.value);
        }
        universalVal = info.paddingRightUniversal;
        if (universalVal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, universalVal);
            right = (int) (base * universalVal.value);
        }

        universalVal = info.paddingTopUniversal;
        if (universalVal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, universalVal);
            top = (int) (base * universalVal.value);
        }

        universalVal = info.paddingBottomUniversal;
        if (universalVal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, universalVal);
            bottom = (int) (base * universalVal.value);
        }
        view.setPadding(left, top, right, bottom);


    }

    public void fillMinOrMaxDimension(int widthHint, int heightHint, View view, UniversalLayoutInfo info)
    {
        Class clazz = view.getClass();
        try
        {
            invokeMethod("setMaxWidth", widthHint, heightHint, view, clazz, info, info.maxWidthUniversal);
            invokeMethod("setMaxHeight", widthHint, heightHint, view, clazz, info, info.maxHeightUniversal);
            invokeMethod("setMinWidth", widthHint, heightHint, view, clazz, info, info.minWidthUniversal);
            invokeMethod("setMinHeight", widthHint, heightHint, view, clazz, info, info.minHeightUniversal);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void invokeMethod(String methodName, int widthHint, int heightHint, View view, Class clazz, UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal universalVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        //if (Log.isLoggable(TAG, Log.DEBUG))
        //    Log.d(TAG, methodName + " ==> " + universalVal);
        if (universalVal != null)
        {
            Method setMaxWidthMethod = clazz.getMethod(methodName, int.class);
            setMaxWidthMethod.setAccessible(true);
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, universalVal);
            setMaxWidthMethod.invoke(view, (int) (base * universalVal.value));
        }
    }

    /**
     * Fills {@code ViewGroup.MarginLayoutParams} dimensions and margins based on percentage
     * values.
     */
    public void fillMarginLayoutParams(ViewGroup.MarginLayoutParams params, int widthHint,
                                       int heightHint, UniversalLayoutInfo info)
    {
        fillLayoutParams(params, widthHint, heightHint, info);

        // Preserver the original margins, so we can restore them after the measure step.
        info.mPreservedParams.leftMargin = params.leftMargin;
        info.mPreservedParams.topMargin = params.topMargin;
        info.mPreservedParams.rightMargin = params.rightMargin;
        info.mPreservedParams.bottomMargin = params.bottomMargin;
        MarginLayoutParamsCompat.setMarginStart(info.mPreservedParams,
                MarginLayoutParamsCompat.getMarginStart(params));
        MarginLayoutParamsCompat.setMarginEnd(info.mPreservedParams,
                MarginLayoutParamsCompat.getMarginEnd(params));

        if (info.leftMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.leftMarginUniversal);
            params.leftMargin = (int) (base * info.leftMarginUniversal.value);
        }
        if (info.topMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.topMarginUniversal);
            params.topMargin = (int) (base * info.topMarginUniversal.value);
        }
        if (info.rightMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.rightMarginUniversal);
            params.rightMargin = (int) (base * info.rightMarginUniversal.value);
        }
        if (info.bottomMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.bottomMarginUniversal);
            params.bottomMargin = (int) (base * info.bottomMarginUniversal.value);
        }
        if (info.startMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.startMarginUniversal);
            MarginLayoutParamsCompat.setMarginStart(params,
                    (int) (base * info.startMarginUniversal.value));
        }
        if (info.endMarginUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.endMarginUniversal);
            MarginLayoutParamsCompat.setMarginEnd(params,
                    (int) (base * info.endMarginUniversal.value));
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "after fillMarginLayoutParams: (" + params.width + ", " + params.height
                    + ")");
        }
    }

    /**
     * Fills {@code ViewGroup.LayoutParams} dimensions based on percentage values.
     */
    public void fillLayoutParams(ViewGroup.LayoutParams params, int widthHint,
                                 int heightHint, UniversalLayoutInfo info)
    {
        // Preserve the original layout params, so we can restore them after the measure step.
        info.mPreservedParams.width = params.width;
        info.mPreservedParams.height = params.height;

        if (info.widthUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.widthUniversal);
            params.width = (int) (base * info.widthUniversal.value);
        }
        if (info.heightUniversal != null)
        {
            float base = UniversalLayoutInfo.getUniversalBase(widthHint, heightHint, info, info.heightUniversal);
            params.height = (int) (base * info.heightUniversal.value);
        }

        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "after fillLayoutParams: (" + params.width + ", " + params.height + ")");
        }
    }


    ///////////// 重置过程 /////////////

    /**
     * Restores original dimensions and margins after they were changed for percentage based
     * values. Calling this method only makes sense if you previously called
     * {@link UniversalLayoutHelper.UniversalLayoutInfo#fillMarginLayoutParams}.
     */
    public void restoreMarginLayoutParams(ViewGroup.MarginLayoutParams params, UniversalLayoutInfo info)
    {
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
     * {@link UniversalLayoutHelper.UniversalLayoutInfo#fillLayoutParams}.
     */
    public void restoreLayoutParams(ViewGroup.LayoutParams params, UniversalLayoutInfo info)
    {
        params.width = info.mPreservedParams.width;
        params.height = info.mPreservedParams.height;
    }

    ///////////// 赋值过程 /////////////

    /**
     * Constructs a UniversalLayoutInfo from attributes associated with a View. Call this method from
     * {@code LayoutParams(Context c, AttributeSet attrs)} constructor.
     */
    public static UniversalLayoutInfo getUniversalLayoutInfo(Context context, AttributeSet attrs){
        return getUniversalLayoutInfo(context, attrs, 0);
    }

    public static UniversalLayoutInfo getUniversalLayoutInfo(Context context, AttributeSet attrs, int style){
        UniversalLayoutInfo info = null;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UniversalLayoutInfo, 0, style);

        info = setDesignSizeVal(array, info);

        info = setWidthAndHeightVal(array, info);

        info = setMarginRelatedVal(array, info);

        info = setTextSizeSupportVal(array, info);

        info = setMinMaxWidthHeightRelatedVal(array, info);

        info = setPaddingRelatedVal(array, info);


        array.recycle();

        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "constructed: " + info);
        }
        return info;
    }

    private static UniversalLayoutInfo setDesignSizeVal(TypedArray array, UniversalLayoutInfo info) {
        info = checkForInfoExists(info);
        info.widthDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_widthDesign, mWidthScreen);
        info.heightDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_heightDesign, mWidthScreen);
        return info;
    }

    private static UniversalLayoutInfo setWidthAndHeightVal(TypedArray array, UniversalLayoutInfo info)
    {
        UniversalLayoutInfo.UniversalVal universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_widthUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value width: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.widthUniversal = universalVal;
        }
        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_heightUniversal, false);

        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value height: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.heightUniversal = universalVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setTextSizeSupportVal(TypedArray array, UniversalLayoutInfo info)
    {
        //textSizeUniversal 默认以宽度作为基准
        UniversalLayoutInfo.UniversalVal universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_textSizeUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value text size: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.textSizeUniversal = universalVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray array, UniversalLayoutInfo info)
    {
        //maxWidth
        UniversalLayoutInfo.UniversalVal universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_maxWidthUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.maxWidthUniversal = universalVal;
        }
        //maxHeight
        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_maxHeightUniversal,
                false);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.maxHeightUniversal = universalVal;
        }
        //minWidth
        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_minWidthUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.minWidthUniversal = universalVal;
        }
        //minHeight
        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_minHeightUniversal,
                false);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.minHeightUniversal = universalVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setMarginRelatedVal(TypedArray array, UniversalLayoutInfo info)
    {
        //默认margin参考宽度
        UniversalLayoutInfo.UniversalVal universalVal =
                getUniversalVal(array,
                        R.styleable.UniversalLayoutInfo_layout_marginUniversal,
                        true);

        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.leftMarginUniversal = universalVal;
            info.topMarginUniversal = universalVal;
            info.rightMarginUniversal = universalVal;
            info.bottomMarginUniversal = universalVal;
        }

        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginLeftUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value left margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.leftMarginUniversal = universalVal;
        }

        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginTopUniversal, false);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value top margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.topMarginUniversal = universalVal;
        }

        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginRightUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value right margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.rightMarginUniversal = universalVal;
        }

        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginBottomUniversal, false);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value bottom margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.bottomMarginUniversal = universalVal;
        }
        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginStartUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value start margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.startMarginUniversal = universalVal;
        }

        universalVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginEndUniversal, true);
        if (universalVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "value end margin: " + universalVal.value);
            }
            info = checkForInfoExists(info);
            info.endMarginUniversal = universalVal;
        }

        return info;
    }

    /**
     * 设置paddingUniversal相关属性
     *
     * @param array
     * @param info
     */
    private static UniversalLayoutInfo setPaddingRelatedVal(TypedArray array, UniversalLayoutInfo info)
    {
        //默认padding以宽度为标准
        UniversalLayoutInfo.UniversalVal universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftUniversal = universalVal;
            info.paddingRightUniversal = universalVal;
            info.paddingBottomUniversal = universalVal;
            info.paddingTopUniversal = universalVal;
        }


        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingLeftUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftUniversal = universalVal;
        }

        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingRightUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingRightUniversal = universalVal;
        }

        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingTopUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingTopUniversal = universalVal;
        }

        universalVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingBottomUniversal,
                true);
        if (universalVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingBottomUniversal = universalVal;
        }

        return info;
    }


    @NonNull
    private static UniversalLayoutInfo checkForInfoExists(UniversalLayoutInfo info)
    {
        info = (info != null) ? info : new UniversalLayoutInfo();
        return info;
    }

    /**
     * 获取参数
     * @param array
     * @param index
     * @param baseWidth
     * @return
     */
    private static UniversalLayoutInfo.UniversalVal getUniversalVal(TypedArray array, int index, boolean baseWidth)
    {
        String sizeStr = array.getString(index);
        UniversalLayoutInfo.UniversalVal universalVal = getUniversalVal(sizeStr, baseWidth);
        return universalVal;
    }

    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)(%|a)([s]?[wh]?)$";

    /**
     * 解析参数
     * widthStr to UniversalVal
     * <br/>
     * eg: 35%w => new UniversalVal("35%", true)
     *
     * @param percentStr
     * @param isOnWidth
     * @return
     */
    private static UniversalLayoutInfo.UniversalVal getUniversalVal(String percentStr, boolean isOnWidth)
    {
        //valid param
        if (percentStr == null)
        {
            return null;
        }
        Pattern p = Pattern.compile(REGEX_PERCENT);
        Matcher matcher = p.matcher(percentStr);
        if (!matcher.matches())
        {
            throw new RuntimeException("the value of layout_xxxUniversal invalid! ==>" + percentStr);
        }
        int len = percentStr.length();
        //extract the float value
        String floatVal = matcher.group(1);

        float percent = Float.parseFloat(floatVal) / 100f;

        UniversalLayoutInfo.UniversalVal universalVal = new UniversalLayoutInfo.UniversalVal();
        universalVal.value = percent;
        if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SW))
        {
            universalVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SH))
        {
            universalVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_HEIGHT;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AW))
        {
            universalVal.value = Float.parseFloat(floatVal);
            universalVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AH))
        {
            universalVal.value = Float.parseFloat(floatVal);
            universalVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_HEIGHT;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.PERCENT))
        {
            if (isOnWidth)
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_WIDTH;
            } else
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SCREEN))
        {
            if (isOnWidth)
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_WIDTH;
            } else
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AUTO))
        {
            universalVal.value = Float.parseFloat(floatVal);
            if (isOnWidth)
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_WIDTH;
            } else
            {
                universalVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.W))
        {
            universalVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.H))
        {
            universalVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_HEIGHT;
        } else
        {
            throw new IllegalArgumentException("the " + percentStr + " must be endWith [%|%w|%h|%s|%sw|%sh|a|aw|ah]");
        }

        return universalVal;
    }

    public void setHorizontalSwapHeightWidth(boolean swap){

    }

    ///////////// 比较过程 /////////////

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
    public boolean handleMeasuredStateTooSmall()
    {
        boolean needsSecondMeasure = false;
        for (int i = 0, N = mHost.getChildCount(); i < N; i++)
        {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "should handle measured state too small " + view + " " + params);
            }
            if (params instanceof UniversalLayoutParams)
            {
                UniversalLayoutInfo info =
                        ((UniversalLayoutParams) params).getUniversalLayoutInfo();
                if (info != null)
                {
                    if (shouldHandleMeasuredWidthTooSmall(view, info))
                    {
                        needsSecondMeasure = true;
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info))
                    {
                        needsSecondMeasure = true;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
            }
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "should trigger second measure pass: " + needsSecondMeasure);
        }
        return needsSecondMeasure;
    }

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, UniversalLayoutInfo info)
    {
        int state = ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.widthUniversal == null)
        {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.widthUniversal.value >= 0 &&
                info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, UniversalLayoutInfo info)
    {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.heightUniversal == null)
        {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.heightUniversal.value >= 0 &&
                info.mPreservedParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    /**
     * Container for information about percentage dimensions and margins. It acts as an extension
     * for {@code LayoutParams}.
     */
    public static class UniversalLayoutInfo
    {

        public enum BaseMode
        {

            PERCENT_WIDTH, PERCENT_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT, AUTO_WIDTH, AUTO_HEIGHT;

            /**
             * layout_auto
             */
            public static final String AUTO = "a";
            /**
             * layout_screen
             */
            public static final String SCREEN = "s";
            /**
             * layout_parent
             */
            public static final String PERCENT = "%";
            /**
             * width_parent
             */
            public static final String W = "w";
            /**
             * height_parent
             */
            public static final String H = "h";
            /**
             * width_auto
             */
            public static final String AW = "aw";
            /**
             * height_auto
             */
            public static final String AH = "ah";
            /**
             * width_screen
             */
            public static final String SW = "sw";
            /**
             * height_screen
             */
            public static final String SH = "sh";
        }

        public static class UniversalVal
        {

            public float value = -1;
            public BaseMode basemode;

            public UniversalVal()
            {
            }

            public UniversalVal(float value, BaseMode baseMode)
            {
                this.value = value;
                this.basemode = baseMode;
            }

            @Override
            public String toString()
            {
                return "UniversalVal{" +
                        "value=" + value +
                        ", basemode=" + basemode.name() +
                        '}';
            }
        }

        public float widthDesign;
        public float heightDesign;
        public UniversalVal widthUniversal;
        public UniversalVal heightUniversal;

        public UniversalVal leftMarginUniversal;
        public UniversalVal topMarginUniversal;
        public UniversalVal rightMarginUniversal;
        public UniversalVal bottomMarginUniversal;
        public UniversalVal startMarginUniversal;
        public UniversalVal endMarginUniversal;

        public UniversalVal textSizeUniversal;

        //1.0.4 those attr for some views' setMax/min Height/Width method
        public UniversalVal maxWidthUniversal;
        public UniversalVal maxHeightUniversal;
        public UniversalVal minWidthUniversal;
        public UniversalVal minHeightUniversal;

        //1.0.6 add padding supprot
        public UniversalVal paddingLeftUniversal;
        public UniversalVal paddingRightUniversal;
        public UniversalVal paddingTopUniversal;
        public UniversalVal paddingBottomUniversal;


        /* package */
        ViewGroup.MarginLayoutParams mPreservedParams;


        public UniversalLayoutInfo()
        {
            mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
        }

        public int getWidthScreen() {
            return mWidthScreen;
        }

        public int getHeightScreen() {
            return mHeightScreen;
        }

        public int getUniversalSize(UniversalLayoutInfo info, UniversalVal val){
            return getUniversalSize(0, 0, info, val);
        }

        public int getUniversalSize(int widthPercent, int heightPercent, UniversalLayoutInfo info, UniversalVal val){
            return getUniversalSize(widthPercent, heightPercent, mWidthScreen, mHeightScreen, info, val);
        }

        public int getUniversalSize(int widthPercent, int heightPercent, int widthScreen, int heightScreen,
                                    UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal val){
            float base = getUniversalBase(widthPercent, heightPercent, widthScreen, heightScreen, info, val);
            return (int) (base * val.value);
        }

        public static float getUniversalBase(UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal val)
        {
            return getUniversalBase(0, 0, mWidthScreen, mHeightScreen, info, val);
        }

        public static float getUniversalBase(int widthPercent, int heightPercent, UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal val)
        {
            return getUniversalBase(widthPercent, heightPercent, mWidthScreen, mHeightScreen, info, val);
        }

        /**
         * 获取不同模式基础值
         * @param widthPercent
         * @param heightPercent
         * @param widthScreen
         * @param heightScreen
         * @param info
         * @param val
         * @return
         */
        public static float getUniversalBase(int widthPercent, int heightPercent, int widthScreen, int heightScreen,
                                             UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal val)
        {
            switch (val.basemode)
            {
                case PERCENT_WIDTH:
                    return widthPercent;
                case PERCENT_HEIGHT:
                    return heightPercent;
                case SCREEN_WIDTH:
                    return widthScreen;
                case SCREEN_HEIGHT:
                    return heightScreen;
                case AUTO_WIDTH:
                    return widthScreen / info.widthDesign;
                case AUTO_HEIGHT:
                    return heightScreen / info.heightDesign;
            }
            return 0;
        }

        public static AttributeSet getAttributeSet(Context context, @LayoutRes int layoutRes){
            XmlResourceParser parser = context.getResources().getLayout(layoutRes);
            AttributeSet attrs;
            try {
                attrs = Xml.asAttributeSet(parser);
            } finally {
                parser.close();
            }
            return attrs;
        }

        @Override
        public String toString()
        {
            return "UniversalLayoutInfo{" +
                    "widthUniversal=" + widthUniversal +
                    ", heightUniversal=" + heightUniversal +
                    ", widthDesign=" + widthDesign +
                    ", heightDesign=" + heightDesign +
                    ", leftMarginUniversal=" + leftMarginUniversal +
                    ", topMarginUniversal=" + topMarginUniversal +
                    ", rightMarginUniversal=" + rightMarginUniversal +
                    ", bottomMarginUniversal=" + bottomMarginUniversal +
                    ", startMarginUniversal=" + startMarginUniversal +
                    ", endMarginUniversal=" + endMarginUniversal +
                    ", textSizeUniversal=" + textSizeUniversal +
                    ", maxWidthUniversal=" + maxWidthUniversal +
                    ", maxHeightUniversal=" + maxHeightUniversal +
                    ", minWidthUniversal=" + minWidthUniversal +
                    ", minHeightUniversal=" + minHeightUniversal +
                    ", paddingLeftUniversal=" + paddingLeftUniversal +
                    ", paddingRightUniversal=" + paddingRightUniversal +
                    ", paddingTopUniversal=" + paddingTopUniversal +
                    ", paddingBottomUniversal=" + paddingBottomUniversal +
                    ", mPreservedParams=" + mPreservedParams +
                    '}';
        }
    }

    /**
     * If a layout wants to support percentage based dimensions and use this helper class, its
     * {@code LayoutParams} subclass must implement this interface.
     * <p/>
     * Your {@code LayoutParams} subclass should contain an instance of {@code UniversalLayoutInfo}
     * and the implementation of this interface should be a simple accessor.
     */
    public interface UniversalLayoutParams
    {
        UniversalLayoutInfo getUniversalLayoutInfo();
    }
}
