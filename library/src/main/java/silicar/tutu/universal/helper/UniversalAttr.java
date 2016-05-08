package silicar.tutu.universal.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import silicar.tutu.universal.R;
import silicar.tutu.universal.helper.base.DisplayBase;
import silicar.tutu.universal.helper.base.IBase;
import silicar.tutu.universal.helper.base.SampleBase;

/**
 * 参数读取解析
 * Created by Brady on 2016/5/2.
 */
public class UniversalAttr {

    private static final String REGEX_UNIVERSAL = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)((%|a)?(s|p|o)?(w|h)?)$";
    //private static final String REGEX_VALUE = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)";
    //private static final String REGEX_BASE = "([%a]?[spo]?[wh]?)$";
    private Context mContext;
    private TypedArray mTypedArray;
    public DisplayBase displayBase;

    public UniversalAttr(Context context) {
        mContext = context;
        displayBase = DisplayBase.getInstance(context.getResources());
    }

    public UniversalAttr(Context context, @StyleRes int resId) {
        mContext = context;
        displayBase = DisplayBase.getInstance(context.getResources());
        //mTypedArray = context.obtainStyledAttributes(resId, R.styleable.UniversalLayoutInfo);
        obtainStyledAttributes(resId);
    }

    public UniversalAttr(Context context, AttributeSet set,// @StyleableRes int[] attrs,
                         @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        mContext = context;
        displayBase = DisplayBase.getInstance(context.getResources());
        //mTypedArray = context.obtainStyledAttributes(set, R.styleable.UniversalLayoutInfo, defStyleAttr, defStyleRes);
        obtainStyledAttributes(set, defStyleAttr, defStyleRes);
    }

    public void obtainStyledAttributes(@StyleRes int resId) {
        if (mTypedArray != null)
            recycle();
        mTypedArray = mContext.obtainStyledAttributes(resId, R.styleable.UniversalLayoutInfo);
    }

    public void obtainStyledAttributes(AttributeSet set,// @StyleableRes int[] attrs,
                                       @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        if (mTypedArray != null)
            recycle();
        mTypedArray = mContext.obtainStyledAttributes(set, R.styleable.UniversalLayoutInfo, defStyleAttr, defStyleRes);
    }

    public void recycle() {
        mTypedArray.recycle();
        mTypedArray = null;
    }

    public UniversalLayoutInfo getUniversalLayoutInfo() {
        UniversalLayoutInfo layoutInfo = new UniversalLayoutInfo();
        getDesignSizeAttr(mTypedArray, layoutInfo);
        isWidth(mTypedArray, layoutInfo);
        getWidthHeightAttr(mTypedArray, layoutInfo);
        getMarginAttr(mTypedArray, layoutInfo);
        getPaddingAttr(mTypedArray, layoutInfo);
        getMinMaxAttr(mTypedArray, layoutInfo);
        getTextViewAttr(mTypedArray, layoutInfo);
        recycle();
        return layoutInfo;
    }

    /**
     * 获取设计稿尺寸
     *
     * @param array
     * @param info
     * @return
     */
    public UniversalLayoutInfo getDesignSizeAttr(TypedArray array, UniversalLayoutInfo info) {
        return getDesignSizeAttr(array, info, displayBase.displayWidth, displayBase.displayHeight);
    }

    /**
     * 获取设计稿尺寸
     *
     * @param array
     * @param info
     * @param defWidth
     * @param defHeight
     * @return
     */
    public UniversalLayoutInfo getDesignSizeAttr(TypedArray array, UniversalLayoutInfo info, float defWidth, float defHeight) {
        info.widthDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_widthDesign, defWidth);
        info.heightDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_heightDesign, defHeight);
        return info;
    }

    /**
     * 是否以宽度为参照值
     *
     * @param array
     * @param info
     * @return
     */
    public UniversalLayoutInfo isWidth(TypedArray array, UniversalLayoutInfo info) {
        info.isWidth = array.getBoolean(R.styleable.UniversalLayoutInfo_layout_defWidth, true);
        return info;
    }

    ///////// 获取公共属性 /////////

    public UniversalLayoutInfo getWidthHeightAttr(TypedArray array, UniversalLayoutInfo info) {
        info.width = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_widthExt, info.isWidth);
        info.height = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_heightExt, info.isWidth);
        return info;
    }

    public UniversalLayoutInfo getMarginAttr(TypedArray array, UniversalLayoutInfo info) {
        info.leftMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginLeftExt, info.isWidth);
        info.rightMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginRightExt, info.isWidth);
        info.topMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginTopExt, info.isWidth);
        info.bottomMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginBottomExt, info.isWidth);
        info.startMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginStartExt, info.isWidth);
        info.endMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginEndExt, info.isWidth);
        return info;
    }

    public UniversalLayoutInfo getPaddingAttr(TypedArray array, UniversalLayoutInfo info) {
        info.paddingLeft = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingLeftExt, info.isWidth);
        info.paddingRight = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingRightExt, info.isWidth);
        info.paddingTop = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingTopExt, info.isWidth);
        info.paddingBottom = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingBottomExt, info.isWidth);
        return info;
    }

    public UniversalLayoutInfo getMinMaxAttr(TypedArray array, UniversalLayoutInfo info) {
        info.maxWidth = getUniversalValue(array, R.styleable.UniversalLayoutInfo_maxWidthExt, info.isWidth);
        info.maxHeight = getUniversalValue(array, R.styleable.UniversalLayoutInfo_maxWidthExt, info.isWidth);
        info.minWidth = getUniversalValue(array, R.styleable.UniversalLayoutInfo_minWidthExt, info.isWidth);
        info.minHeight = getUniversalValue(array, R.styleable.UniversalLayoutInfo_minHeightExt, info.isWidth);
        return info;
    }

    ///////// 获取指定控件属性 /////////

    public UniversalLayoutInfo getTextViewAttr(TypedArray array, UniversalLayoutInfo info) {
        info.textSize = getUniversalValue(array, R.styleable.UniversalLayoutInfo_textSizeExt, info.isWidth);
        return info;
    }

    /**
     * 获取参数
     *
     * @param array
     * @param index
     * @param isWidth
     * @return
     */
    public UniversalValue getUniversalValue(TypedArray array, int index, boolean isWidth) {
        String sizeStr = array.getString(index);
        UniversalValue value = getUniversalValue(sizeStr, isWidth);
        return value;
    }

    /**
     * 解析参数
     * StringValue to UniversalValue
     *
     * @param universalStr
     * @param isWidth
     * @return
     */
    private UniversalValue getUniversalValue(String universalStr, boolean isWidth) {
        //valid param
        if (universalStr == null) {
            return null;
        }
        Pattern p = Pattern.compile(REGEX_UNIVERSAL);
        Matcher matcher = p.matcher(universalStr);
        if (!matcher.matches()) {
            //无效格式
            throw new RuntimeException("the value of UniversalValue invalid! ==>" + universalStr);
        }
        int len = universalStr.length();
        //extract the float value
        String valueStr = matcher.group(1);
        //String base = matcher.group(7);
        String baseModeStr = matcher.group(8);
        String baseObjStr = matcher.group(9);
        String baseIsWidthStr = matcher.group(10);

        UniversalValue universalVal = new UniversalValue();
        SampleBase sampleBase = new SampleBase();
        float value = Float.parseFloat(valueStr);

        // 计算模式
        if (baseModeStr == null || baseModeStr.equals(IBase.MODE_AUTO)) {
            sampleBase.setMode(IBase.modeAuto);
        } else {
            sampleBase.setMode(IBase.modePercent);
            value /= 100f;
        }

        // 参照对象
        if (baseObjStr == null || baseObjStr.equals(IBase.OBJ_SCREEN)) {
            sampleBase.setObj(IBase.objScreen);
        } else if (baseObjStr.equals(IBase.OBJ_PARENT)) {
            sampleBase.setObj(IBase.objParent);
        } else {
            sampleBase.setObj(IBase.objOwn);
        }

        // 参照值
        if (baseIsWidthStr == null || baseIsWidthStr.equals(IBase.WIDTH) || isWidth) {
            sampleBase.setWidth(true);
        } else {
            sampleBase.setWidth(false);
        }
        universalVal.value = value;
        universalVal.base = sampleBase;

        return universalVal;
    }
}
