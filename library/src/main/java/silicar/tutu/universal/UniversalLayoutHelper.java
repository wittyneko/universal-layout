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
import android.support.annotation.NonNull;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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
        //textsize percent support

        UniversalLayoutInfo.UniversalVal textSizeUniversal = info.textSizeUniversal;
        if (textSizeUniversal == null) return;

        float base = getBaseByModeAndVal(widthHint, heightHint, info, textSizeUniversal.basemode);
        float textSize = (int) (base * textSizeUniversal.percent);

        //Button 和 EditText 是TextView的子类
        if (view instanceof TextView)
        {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    public void fillPadding(int widthHint, int heightHint, View view, UniversalLayoutInfo info)
    {
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), top = view.getPaddingTop(), bottom = view.getPaddingBottom();
        UniversalLayoutInfo.UniversalVal percentVal = info.paddingLeftUniversal;
        if (percentVal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, percentVal.basemode);
            left = (int) (base * percentVal.percent);
        }
        percentVal = info.paddingRightUniversal;
        if (percentVal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, percentVal.basemode);
            right = (int) (base * percentVal.percent);
        }

        percentVal = info.paddingTopUniversal;
        if (percentVal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, percentVal.basemode);
            top = (int) (base * percentVal.percent);
        }

        percentVal = info.paddingBottomUniversal;
        if (percentVal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, percentVal.basemode);
            bottom = (int) (base * percentVal.percent);
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

    private void invokeMethod(String methodName, int widthHint, int heightHint, View view, Class clazz, UniversalLayoutInfo info, UniversalLayoutInfo.UniversalVal percentVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        //if (Log.isLoggable(TAG, Log.DEBUG))
        //    Log.d(TAG, methodName + " ==> " + percentVal);
        if (percentVal != null)
        {
            Method setMaxWidthMethod = clazz.getMethod(methodName, int.class);
            setMaxWidthMethod.setAccessible(true);
            float base = getBaseByModeAndVal(widthHint, heightHint, info, percentVal.basemode);
            setMaxWidthMethod.invoke(view, (int) (base * percentVal.percent));
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
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.leftMarginUniversal.basemode);
            params.leftMargin = (int) (base * info.leftMarginUniversal.percent);
        }
        if (info.topMarginUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.topMarginUniversal.basemode);
            params.topMargin = (int) (base * info.topMarginUniversal.percent);
        }
        if (info.rightMarginUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.rightMarginUniversal.basemode);
            params.rightMargin = (int) (base * info.rightMarginUniversal.percent);
        }
        if (info.bottomMarginUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.bottomMarginUniversal.basemode);
            params.bottomMargin = (int) (base * info.bottomMarginUniversal.percent);
        }
        if (info.startMarginUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.startMarginUniversal.basemode);
            MarginLayoutParamsCompat.setMarginStart(params,
                    (int) (base * info.startMarginUniversal.percent));
        }
        if (info.endMarginUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.endMarginUniversal.basemode);
            MarginLayoutParamsCompat.setMarginEnd(params,
                    (int) (base * info.endMarginUniversal.percent));
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
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.widthUniversal.basemode);
            params.width = (int) (base * info.widthUniversal.percent);
        }
        if (info.heightUniversal != null)
        {
            float base = getBaseByModeAndVal(widthHint, heightHint, info, info.heightUniversal.basemode);
            params.height = (int) (base * info.heightUniversal.percent);
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
        UniversalLayoutInfo.UniversalVal percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_widthUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent width: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.widthUniversal = percentVal;
        }
        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_heightUniversal, false);

        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent height: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.heightUniversal = percentVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setTextSizeSupportVal(TypedArray array, UniversalLayoutInfo info)
    {
        //textSizeUniversal 默认以宽度作为基准
        UniversalLayoutInfo.UniversalVal percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_textSizeUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent text size: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.textSizeUniversal = percentVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray array, UniversalLayoutInfo info)
    {
        //maxWidth
        UniversalLayoutInfo.UniversalVal percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_maxWidthUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.maxWidthUniversal = percentVal;
        }
        //maxHeight
        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_maxHeightUniversal,
                false);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.maxHeightUniversal = percentVal;
        }
        //minWidth
        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_minWidthUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.minWidthUniversal = percentVal;
        }
        //minHeight
        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_minHeightUniversal,
                false);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.minHeightUniversal = percentVal;
        }

        return info;
    }

    private static UniversalLayoutInfo setMarginRelatedVal(TypedArray array, UniversalLayoutInfo info)
    {
        //默认margin参考宽度
        UniversalLayoutInfo.UniversalVal percentVal =
                getUniversalVal(array,
                        R.styleable.UniversalLayoutInfo_layout_marginUniversal,
                        true);

        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginUniversal = percentVal;
            info.topMarginUniversal = percentVal;
            info.rightMarginUniversal = percentVal;
            info.bottomMarginUniversal = percentVal;
        }

        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginLeftUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent left margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginUniversal = percentVal;
        }

        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginTopUniversal, false);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent top margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.topMarginUniversal = percentVal;
        }

        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginRightUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent right margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.rightMarginUniversal = percentVal;
        }

        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginBottomUniversal, false);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent bottom margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.bottomMarginUniversal = percentVal;
        }
        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginStartUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent start margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.startMarginUniversal = percentVal;
        }

        percentVal = getUniversalVal(array, R.styleable.UniversalLayoutInfo_layout_marginEndUniversal, true);
        if (percentVal != null)
        {
            if (Log.isLoggable(TAG, Log.VERBOSE))
            {
                Log.v(TAG, "percent end margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.endMarginUniversal = percentVal;
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
        UniversalLayoutInfo.UniversalVal percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftUniversal = percentVal;
            info.paddingRightUniversal = percentVal;
            info.paddingBottomUniversal = percentVal;
            info.paddingTopUniversal = percentVal;
        }


        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingLeftUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingLeftUniversal = percentVal;
        }

        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingRightUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingRightUniversal = percentVal;
        }

        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingTopUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingTopUniversal = percentVal;
        }

        percentVal = getUniversalVal(array,
                R.styleable.UniversalLayoutInfo_layout_paddingBottomUniversal,
                true);
        if (percentVal != null)
        {
            info = checkForInfoExists(info);
            info.paddingBottomUniversal = percentVal;
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
        UniversalLayoutInfo.UniversalVal percentVal = getUniversalVal(sizeStr, baseWidth);
        return percentVal;
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

        UniversalLayoutInfo.UniversalVal percentVal = new UniversalLayoutInfo.UniversalVal();
        percentVal.percent = percent;
        if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SW))
        {
            percentVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SH))
        {
            percentVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_HEIGHT;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AW))
        {
            percentVal.percent = Float.parseFloat(floatVal);
            percentVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AH))
        {
            percentVal.percent = Float.parseFloat(floatVal);
            percentVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_HEIGHT;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.PERCENT))
        {
            if (isOnWidth)
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_WIDTH;
            } else
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.SCREEN))
        {
            if (isOnWidth)
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_WIDTH;
            } else
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.SCREEN_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.AUTO))
        {
            percentVal.percent = Float.parseFloat(floatVal);
            if (isOnWidth)
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_WIDTH;
            } else
            {
                percentVal.basemode = UniversalLayoutInfo.BaseMode.AUTO_HEIGHT;
            }
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.W))
        {
            percentVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_WIDTH;
        } else if (percentStr.endsWith(UniversalLayoutInfo.BaseMode.H))
        {
            percentVal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_HEIGHT;
        } else
        {
            throw new IllegalArgumentException("the " + percentStr + " must be endWith [%|%w|%h|%s|%sw|%sh|a|aw|ah]");
        }

        return percentVal;
    }

    /**
     * 获取不同模式基础值
     * @param widthHint
     * @param heightHint
     * @param info
     * @param baseMode
     * @return
     */
    private static float getBaseByModeAndVal(int widthHint, int heightHint, UniversalLayoutInfo info, UniversalLayoutInfo.BaseMode baseMode)
    {
        switch (baseMode)
        {
            case PERCENT_HEIGHT:
                return heightHint;
            case PERCENT_WIDTH:
                return widthHint;
            case SCREEN_WIDTH:
                return mWidthScreen;
            case SCREEN_HEIGHT:
                return mHeightScreen;
            case AUTO_HEIGHT:
                return mHeightScreen / info.heightDesign;
            case AUTO_WIDTH:
                return mWidthScreen / info.widthDesign;
        }
        return 0;
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
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.widthUniversal.percent >= 0 &&
                info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, UniversalLayoutInfo info)
    {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.heightUniversal == null)
        {
            return false;
        }
        return state == ViewCompat.MEASURED_STATE_TOO_SMALL && info.heightUniversal.percent >= 0 &&
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

            public float percent = -1;
            public BaseMode basemode;

            public UniversalVal()
            {
            }

            public UniversalVal(float percent, BaseMode baseMode)
            {
                this.percent = percent;
                this.basemode = baseMode;
            }

            @Override
            public String toString()
            {
                return "UniversalVal{" +
                        "percent=" + percent +
                        ", basemode=" + basemode.name() +
                        '}';
            }
        }

        public UniversalVal widthUniversal;
        public UniversalVal heightUniversal;
        public float widthDesign;
        public float heightDesign;

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

        public int getUniversalSize(int parentWidth, int parentHeight, UniversalLayoutInfo info, UniversalVal val){
            float base = getBaseByModeAndVal(parentWidth, parentHeight, info, val.basemode);
            return (int) (base * val.percent);
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
