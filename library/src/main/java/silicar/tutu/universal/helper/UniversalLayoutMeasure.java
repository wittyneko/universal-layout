package silicar.tutu.universal.helper;

import android.support.v4.view.MarginLayoutParamsCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import silicar.tutu.universal.helper.base.BaseDisplay;
import silicar.tutu.universal.helper.base.BaseModel;
import silicar.tutu.universal.helper.base.SampleModel;

/**
 * 测量计算，重置参数
 * Created by Brady on 2016/5/8.
 */
public class UniversalLayoutMeasure {

    private ViewGroup mHost;

    public UniversalLayoutMeasure(ViewGroup host) {
        mHost = host;
    }

    public void fillLayout(int widthHint, int heightHint, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof UniversalLayoutParams) {
            UniversalLayoutInfo info = ((UniversalLayoutParams) params).getUniversalLayoutInfo();
            if (info != null) {
                //填充指定控件
                fillTextView(widthHint, heightHint, view, info);
                //填充Padding
                fillPadding(widthHint, heightHint, view, info);

                if (params instanceof ViewGroup.MarginLayoutParams) {
                    //填充MarginLayoutParams
                    fillMarginLayoutParams(widthHint, heightHint, view, (ViewGroup.MarginLayoutParams) params, info);
                } else {
                    //填充LayoutParams
                    fillLayoutParams(widthHint, heightHint, view, params, info);
                }
                //填充MinMax
                fillMinMax(widthHint, heightHint, view, params, info);
            }
        }
    }

    public void fillTextView(int widthHint, int heightHint, View view, UniversalLayoutInfo info) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (info.textSize != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, measureUniversalValue(widthHint, heightHint, view, info, info.textSize));
            }
        }
    }

    public void fillPadding(int widthHint, int heightHint, View view, UniversalLayoutInfo info) {
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), top = view.getPaddingTop(), bottom = view.getPaddingBottom();
        if (info.paddingLeft != null) {
            left = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.paddingLeft) + 0.5f);
        }
        if (info.paddingRight != null) {
            right = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.paddingRight) + 0.5f);
        }
        if (info.paddingTop != null) {
            top = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.paddingTop) + 0.5f);
        }
        if (info.paddingBottom != null) {
            bottom = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.paddingBottom) + 0.5f);
        }
        view.setPadding(left, top, right, bottom);
    }

    public void fillLayoutParams(int widthHint, int heightHint, View view, ViewGroup.LayoutParams params, UniversalLayoutInfo info) {
        info.mPreservedParams.width = params.width;
        info.mPreservedParams.height = params.height;

        if (info.width != null)
        {
            params.width = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.width) + 0.5f);
        }
        if (info.height != null)
        {
            params.height = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.height) + 0.5f);
        }
    }

    public void fillMarginLayoutParams(int widthHint, int heightHint, View view, ViewGroup.MarginLayoutParams params, UniversalLayoutInfo info) {
        info.mPreservedParams.leftMargin = params.leftMargin;
        info.mPreservedParams.topMargin = params.topMargin;
        info.mPreservedParams.rightMargin = params.rightMargin;
        info.mPreservedParams.bottomMargin = params.bottomMargin;
        MarginLayoutParamsCompat.setMarginStart(info.mPreservedParams,
                MarginLayoutParamsCompat.getMarginStart(params));
        MarginLayoutParamsCompat.setMarginEnd(info.mPreservedParams,
                MarginLayoutParamsCompat.getMarginEnd(params));

        if (info.leftMargin != null)
        {
            params.leftMargin = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.leftMargin) + 0.5f);
        }
        if (info.rightMargin != null)
        {
            params.rightMargin = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.rightMargin) + 0.5f);
        }
        if (info.topMargin != null)
        {
            params.topMargin = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.topMargin) + 0.5f);
        }
        if (info.bottomMargin != null)
        {
            params.bottomMargin = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.bottomMargin) + 0.5f);
        }
        if (info.startMargin != null)
        {
            MarginLayoutParamsCompat.setMarginStart(params,
                    (int) (measureUniversalValue(widthHint, heightHint, view, info, info.startMargin) + 0.5f));
        }
        if (info.endMargin != null)
        {
            MarginLayoutParamsCompat.setMarginEnd(params,
                    (int) (measureUniversalValue(widthHint, heightHint, view, info, info.endMargin) + 0.5f));
        }

        fillLayoutParams(widthHint, heightHint, view, params, info);
    }

    public void fillMinMax(int widthHint, int heightHint, View view, ViewGroup.LayoutParams params, UniversalLayoutInfo info) {
        if (info.minWidth != null)
        {
            int minWidth = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.minWidth) + 0.5f);
            if (params.width < minWidth)
                params.width = minWidth;
        }
        if (info.minHeight != null)
        {
            int minHeight = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.minHeight) + 0.5f);
            if (params.height < minHeight)
                params.height = minHeight;
        }
        if (info.maxWidth != null)
        {
            int maxWidth = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.maxWidth) + 0.5f);
            if (params.width > maxWidth)
                params.width = maxWidth;
        }
        if (info.maxHeight != null)
        {
            int maxHeight = (int) (measureUniversalValue(widthHint, heightHint, view, info, info.maxHeight) + 0.5f);
            if (params.height > maxHeight)
                params.height = maxHeight;
        }
    }

    /**
     * 计算实际尺寸
     *
     * @param widthHint
     * @param heightHint
     * @param universalValue
     * @return
     */
    public float measureUniversalValue(int widthHint, int heightHint, View view, UniversalLayoutInfo info, UniversalValue universalValue) {
        SampleModel model = (SampleModel) universalValue.model;
        if (model.getDesignWidth() == 0f)
            model.setDesignWidth(info.widthDesign);
        if (model.getDesignHeight() == 0f)
            model.setDesignHeight(info.heightDesign);
        if (model.getMode() == BaseModel.modeAuto) {
            return UniversalDimens.getUniversalDimens(universalValue, getDisplay());
        } else if (model.getMode() == BaseModel.modePercent) {
            switch (model.getObject()) {
                case BaseModel.objScreen:
                    return UniversalDimens.getUniversalDimens(universalValue, getDisplay());
                case BaseModel.objParent:
                    return UniversalDimens.getUniversalDimens(universalValue, widthHint, heightHint);
                case BaseModel.objOwn:
                    return UniversalDimens.getUniversalDimens(universalValue, view);
            }
        }
        return 0;
    }

    public BaseDisplay getDisplay() {
        if (mHost instanceof UniversalView){
            UniversalView view = (UniversalView) mHost;
            return view.getAutoDisplay();
        }
        return BaseDisplay.getInstance();
    }
}
