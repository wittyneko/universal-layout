package silicar.tutu.universal.helper.base;

import silicar.tutu.universal.helper.UniversalLayoutHelper;

/**
 * 获取参数样本
 * Created by Brady on 2016/5/4.
 */
public class SampleModel implements BaseModel{

    protected int obj;
    protected int mode;
    protected boolean isWidth = true;
    protected float baseWidth = 0f;
    protected float baseHeight = 0f;
    protected float designWidth = 0f;
    protected float designHeight = 0f;

    public SampleModel(int mode, int object, boolean isWidth) {
        this.mode = mode;
        this.obj = object;
        this.isWidth = isWidth;
    }
    public SampleModel(){
        this(modeAuto,objScreen, true);
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
    public boolean isWidth() {
        return isWidth;
    }

    @Override
    public float getBaseValue() {
        if (mode == BaseModel.modeAuto){
            if (isWidth){
                return baseWidth / designWidth;
            }else {
                return baseHeight / designHeight;
            }
        } else if (mode == BaseModel.modePercent){
            if (isWidth){
                return baseWidth;
            }else {
                return baseHeight;
            }
        }
        return 1f;
    }

    @Override
    public float getBaseWidth() {
        return baseWidth;
    }

    @Override
    public float getBaseHeight() {
        return baseHeight;
    }

    public float getDesignWidth() {
        return designWidth;
    }

    public float getDesignHeight() {
        return designHeight;
    }

    public SampleModel setObj(int obj) {
        this.obj = obj;
        return this;
    }

    public SampleModel setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public SampleModel defWidth(boolean isWidth) {
        this.isWidth = isWidth;
        return this;
    }

    public SampleModel setBaseWidth(float baseWidth) {
        this.baseWidth = baseWidth;
        return this;
    }

    public SampleModel setBaseHeight(float baseHeight) {
        this.baseHeight = baseHeight;
        return this;
    }

    public SampleModel setDesignWidth(float designWidth) {
        this.designWidth = designWidth;
        return this;
    }

    public SampleModel setDesignHeight(float designHeight) {
        this.designHeight = designHeight;
        return this;
    }

    public SampleModel getDefaultDesign(){
        designWidth = BaseDisplay.getInstance().getDisplayWidth();
        designHeight = BaseDisplay.getInstance().getDisplayHeight();
        return this;
    }
}
