package silicar.tutu.universal.helper;

import android.view.View;

import silicar.tutu.universal.helper.base.BaseDisplay;
import silicar.tutu.universal.helper.base.BaseModel;
import silicar.tutu.universal.helper.base.SampleModel;

/**
 * 单位换算
 * Created by Tutu on 2016/7/26.
 */
public class UniversalDimens {

    //Auto | PercentScreen 获取方式
    public static float getUniversalDimens(UniversalValue value, BaseDisplay display){
        return getUniversalDimens(value, display.getDisplayWidth(), display.getDisplayHeight());
    }

    public static float getUniversalDimens(UniversalValue value, View view){
        return getUniversalDimens(value, view.getLayoutParams().width, view.getLayoutParams().height);
    }

    public static float getUniversalDimens(UniversalValue value, int widthHint, int heightHint) {
        SampleModel model = (SampleModel) value.model;
        model.setBaseWidth(widthHint);
        model.setBaseHeight(heightHint);
        return value.value * model.getBaseValue();
    }

}
