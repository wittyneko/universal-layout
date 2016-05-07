package silicar.tutu.universal.helper.base;

/**
 * 获取参数样本
 * Created by Brady on 2016/5/4.
 */
public class SampleBase extends AbsBase {
    public SampleBase(){
        this(IBase.modeAuto,objScreen, true);
    }

    public SampleBase(int mode, int object, boolean isWidth) {
        super(mode, object, isWidth);
    }

    @Override

    public int getObject() {
        return obj;
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public float getValue() {
        return 0;
    }
}
