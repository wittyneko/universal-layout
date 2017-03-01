package silicar.tutu.universal.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import silicar.tutu.universal.R;
import silicar.tutu.universal.value.IMeasureModel;
import silicar.tutu.universal.value.MeasureModel;
import silicar.tutu.universal.value.ReferDisplay;
import silicar.tutu.universal.value.UniversalLayoutInfo;
import silicar.tutu.universal.value.UniversalValue;

/**
 * 参数读取解析
 * Created by Brady on 2016/5/2.
 */
public class UniversalAttrs {

    private static final String REGEX_UNIVERSAL = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)((%|a)?(s|p|o)?(w|h)?)$";
    //private static final String REGEX_VALUE = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)";
    //private static final String REGEX_BASE = "([%a]?[spo]?[wh]?)$";
    private Context mContext;
    private TypedArray mTypedArray;
    private ReferDisplay mDisplay;

    public UniversalAttrs(Context context, ReferDisplay display) {
        mContext = context;
        mDisplay = display;
    }

    public UniversalAttrs(Context context, ReferDisplay display, @StyleRes int resId) {
        mContext = context;
        mDisplay = display;
        //mTypedArray = context.obtainStyledAttributes(resId, R.styleable.UniversalLayoutInfo);
        obtainStyledAttributes(resId);
    }

    public UniversalAttrs(Context context, ReferDisplay display, AttributeSet set,// @StyleableRes int[] attrs,
                          @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        mContext = context;
        mDisplay = display;
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
        getDefault(mTypedArray, layoutInfo);
        getWidthHeightAttr(mTypedArray, layoutInfo);
        getMarginAttr(mTypedArray, layoutInfo);
        getPaddingAttr(mTypedArray, layoutInfo);
        getMinMaxAttr(mTypedArray, layoutInfo);
        getTextViewAttr(mTypedArray, layoutInfo);
        recycle();
        return layoutInfo;
    }

    ///////// 获取基础属性 /////////

    /**
     * 获取设计稿尺寸
     *
     * @param array
     * @param info
     * @return
     */
    public UniversalLayoutInfo getDesignSizeAttr(TypedArray array, UniversalLayoutInfo info) {
        return getDesignSizeAttr(array, info, mDisplay.getDisplayWidth(), mDisplay.getDisplayHeight());
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
     * 获取默认参数
     *
     * @param array
     * @param info
     * @return
     */
    public UniversalLayoutInfo getDefault(TypedArray array, UniversalLayoutInfo info) {
        //是否以宽度为参照值
        info.isWidth = array.getBoolean(R.styleable.UniversalLayoutInfo_layout_defWidth, true);
        //是否使用父控件padding值
        info.usePadding = array.getBoolean(R.styleable.UniversalLayoutInfo_layout_usePadding, false);
        return info;
    }

    ///////// 获取公共属性 /////////

    public UniversalLayoutInfo getWidthHeightAttr(TypedArray array, UniversalLayoutInfo info) {
        info.width = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_widthExt, info.isWidth);
        info.height = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_heightExt, info.isWidth);
        return info;
    }

    public UniversalLayoutInfo getMarginAttr(TypedArray array, UniversalLayoutInfo info) {
        //先解读公共margin
        info.leftMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginExt, info.isWidth);
        info.rightMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginExt, info.isWidth);
        info.topMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginExt, info.isWidth);
        info.bottomMargin = getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginExt, info.isWidth);
        //存在个别属性，使用新属性
        info.leftMargin = getUniversalValue(info.leftMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginLeftExt, info.isWidth));
        info.rightMargin = getUniversalValue(info.rightMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginRightExt, info.isWidth));
        info.topMargin = getUniversalValue(info.topMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginTopExt, info.isWidth));
        info.bottomMargin = getUniversalValue(info.bottomMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginBottomExt, info.isWidth));
        info.startMargin = getUniversalValue(info.startMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginStartExt, info.isWidth));
        info.endMargin = getUniversalValue(info.endMargin, getUniversalValue(array, R.styleable.UniversalLayoutInfo_layout_marginEndExt, info.isWidth));
        return info;
    }

    public UniversalLayoutInfo getPaddingAttr(TypedArray array, UniversalLayoutInfo info) {
        //先读取公共padding
        info.paddingLeft = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingExt, info.isWidth);
        info.paddingRight = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingExt, info.isWidth);
        info.paddingTop = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingExt, info.isWidth);
        info.paddingBottom = getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingExt, info.isWidth);
        //存在个别属性，使用新属性
        info.paddingLeft = getUniversalValue(info.paddingLeft, getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingLeftExt, info.isWidth));
        info.paddingRight = getUniversalValue(info.paddingRight, getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingRightExt, info.isWidth));
        info.paddingTop = getUniversalValue(info.paddingTop, getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingTopExt, info.isWidth));
        info.paddingBottom = getUniversalValue(info.paddingBottom, getUniversalValue(array, R.styleable.UniversalLayoutInfo_paddingBottomExt, info.isWidth));
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

    public UniversalValue getUniversalValue(UniversalValue oldValue, UniversalValue newValue){
        if (newValue != null)
            return newValue;
        else
            return oldValue;
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
        //String model = matcher.group(7);
        String baseModeStr = matcher.group(8);
        String baseObjStr = matcher.group(9);
        String baseIsWidthStr = matcher.group(10);

        UniversalValue universalVal = new UniversalValue();
        MeasureModel measureModel = new MeasureModel();
        float value = Float.parseFloat(valueStr);

        // 计算模式
        if (baseModeStr == null || baseModeStr.equals(IMeasureModel.MODE_AUTO)) {
            measureModel.setMode(IMeasureModel.modeAuto);
        } else {
            measureModel.setMode(IMeasureModel.modePercent);
            value /= 100f;
        }

        // 参照对象
        if (baseObjStr == null || baseObjStr.equals(IMeasureModel.REF_SCREEN)) {
            measureModel.setRefObject(IMeasureModel.refScreen);
        } else if (baseObjStr.equals(IMeasureModel.REF_PARENT)) {
            measureModel.setRefObject(IMeasureModel.refParent);
        } else {
            measureModel.setRefObject(IMeasureModel.refOwn);
        }

        // 参照值
        if (baseIsWidthStr == null) {
            measureModel.defWidth(isWidth);
        } else {
            if (baseIsWidthStr.equals(IMeasureModel.WIDTH))
                measureModel.defWidth(true);
            else
                measureModel.defWidth(false);
        }
        universalVal.value = value;
        universalVal.model = measureModel;

        return universalVal;
    }
}
