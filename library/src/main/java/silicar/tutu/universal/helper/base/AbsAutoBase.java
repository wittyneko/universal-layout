package silicar.tutu.universal.helper.base;

/**
 * 自适应基础值抽象类
 * Created by Brady on 2016/5/2.
 */
public abstract class AbsAutoBase extends AbsBase {

    protected float designWidth;
    protected float designHeight;

    public AbsAutoBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
        this.designWidth = designWidth;
        this.designHeight = designHeight;
    }

    public AbsAutoBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getMode() {
        mode = modeAuto;
        return mode;
    }

    @Override
    public float getValue() {
        if (isWidth){
            return widthValue / designWidth;
        }else {
            return heightValue / designHeight;
        }
    }

    public float getDesignWidth() {
        return designWidth;
    }

    public float getDesignHeight() {
        return designHeight;
    }

    public AbsAutoBase setDesignWidth(float designWidth) {
        this.designWidth = designWidth;
        return this;
    }

    public AbsAutoBase setDesignHeight(float designHeight) {
        this.designHeight = designHeight;
        return this;
    }
}
