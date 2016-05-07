package silicar.tutu.universal.helper.base;

/**
 * 基础值抽象类
 * Created by Brady on 2016/5/2.
 */
public abstract class AbsBase implements IBase {

    protected boolean isWidth = true;
    protected int obj;
    protected int mode;
    protected float widthValue = 0f;
    protected float heightValue = 0f;

    public AbsBase(float widthValue, float heightValue, boolean isWidth) {
        this.widthValue = widthValue;
        this.heightValue = heightValue;
        this.isWidth = isWidth;
    }

    public AbsBase(int mode, int object, boolean isWidth) {
        this.mode = mode;
        this.obj = object;
        this.isWidth = isWidth;
    }

    @Override
    public boolean isWidth() {
        return isWidth;
    }

    @Override
    public float getWidthValue() {
        return widthValue;
    }

    @Override
    public float getHeightValue() {
        return heightValue;
    }

    public AbsBase setWidth(boolean isWidth) {
        this.isWidth = isWidth;
        return this;
    }

    public void setObj(int obj) {
        this.obj = obj;
    }

    public AbsBase setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public AbsBase setWidthValue(float widthValue) {
        this.widthValue = widthValue;
        return this;
    }

    public AbsBase setHeightValue(float heightValue) {
        this.heightValue = heightValue;
        return this;
    }
}
