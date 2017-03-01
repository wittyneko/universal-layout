package silicar.tutu.universal.helper;

import android.view.View;
import android.view.ViewGroup;

import silicar.tutu.universal.value.IMeasureModel;
import silicar.tutu.universal.value.MeasureModel;
import silicar.tutu.universal.value.ReferDisplay;
import silicar.tutu.universal.value.UniversalLayoutInfo;
import silicar.tutu.universal.value.UniversalValue;

/**
 * 单位换算
 * Created by Tutu on 2016/7/26.
 */
public class UniversalDimens {

    public static float getMeasureDimens(UniversalValue value, View view) {
        if (view.getLayoutParams() instanceof UniversalLayoutParams) {
            UniversalLayoutParams layoutParams = (UniversalLayoutParams) view.getLayoutParams();
            UniversalLayoutInfo info = layoutParams.getUniversalLayoutInfo();
            ViewGroup parent = (ViewGroup) view.getParent();
            ReferDisplay display = ((UniversalView) view.getParent()).getAutoDisplay();

            MeasureModel model = (MeasureModel) value.model;
            if (model.getDesignWidth() == 0f)
                model.setDesignWidth(info.widthDesign);
            if (model.getDesignHeight() == 0f)
                model.setDesignHeight(info.heightDesign);
            if (model.getMode() == IMeasureModel.modeAuto) {
                return UniversalDimens.getUniversalDimens(value, display);
            } else if (model.getMode() == IMeasureModel.modePercent) {
                switch (model.getReferObject()) {
                    case IMeasureModel.refScreen:
                        return UniversalDimens.getUniversalDimens(value, display);
                    case IMeasureModel.refParent:
                        return UniversalDimens.getUniversalDimens(value, parent.getMeasuredWidth(), parent.getMeasuredHeight());
                    case IMeasureModel.refOwn:
                        return UniversalDimens.getUniversalDimens(value, view);
                }
            }
        }
        return 0;
    }

    //Auto | PercentScreen 获取方式
    public static float getUniversalDimens(UniversalValue value, ReferDisplay display) {
        return getUniversalDimens(value, display.getDisplayWidth(), display.getDisplayHeight());
    }

    // Own 获取方式
    public static float getUniversalDimens(UniversalValue value, View view) {
        //return getUniversalDimens(value, view.getMeasuredWidth(), view.getMeasuredHeight());
        return getUniversalDimens(value, view.getLayoutParams().width, view.getLayoutParams().height);
    }

    // 通用 获取方式
    public static float getUniversalDimens(UniversalValue value, int widthHint, int heightHint) {
        MeasureModel model = (MeasureModel) value.model;
        model.setBaseWidth(widthHint);
        model.setBaseHeight(heightHint);
        return value.value * model.getBaseValue();
    }

}
