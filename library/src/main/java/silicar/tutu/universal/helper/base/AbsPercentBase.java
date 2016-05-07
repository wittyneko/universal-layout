package silicar.tutu.universal.helper.base;

/**
 * 百分比基础值抽象类
 * Created by Brady on 2016/5/2.
 */
public abstract class AbsPercentBase extends AbsBase {

    public AbsPercentBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getMode() {
        mode = modePercent;
        return mode;
    }

    @Override
    public float getValue() {
        if (isWidth){
            return widthValue;
        }else {
            return heightValue;
        }
    }
}
