package silicar.tutu.universal.value;

/**
 * 获取参数样本
 * Created by Brady on 2016/5/4.
 */
public class MeasureModel implements IMeasureModel {

    protected int obj;
    protected int mode;
    protected boolean isWidth = true;
    protected float baseWidth = 0f;
    protected float baseHeight = 0f;
    protected float designWidth = 0f;
    protected float designHeight = 0f;

    public MeasureModel(int mode, int object, boolean isWidth) {
        this.mode = mode;
        this.obj = object;
        this.isWidth = isWidth;
    }
    public MeasureModel(){
        this(modeAuto, refScreen, true);
    }

    @Override

    public int getReferObject() {
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
        if (mode == IMeasureModel.modeAuto){
            if (isWidth){
                return baseWidth / designWidth;
            }else {
                return baseHeight / designHeight;
            }
        } else if (mode == IMeasureModel.modePercent){
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

    public MeasureModel setRefObject(int obj) {
        this.obj = obj;
        return this;
    }

    public MeasureModel setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public MeasureModel defWidth(boolean isWidth) {
        this.isWidth = isWidth;
        return this;
    }

    public MeasureModel setBaseWidth(float baseWidth) {
        this.baseWidth = baseWidth;
        return this;
    }

    public MeasureModel setBaseHeight(float baseHeight) {
        this.baseHeight = baseHeight;
        return this;
    }

    public MeasureModel setDesignWidth(float designWidth) {
        this.designWidth = designWidth;
        return this;
    }

    public MeasureModel setDesignHeight(float designHeight) {
        this.designHeight = designHeight;
        return this;
    }

    public MeasureModel getDefaultDesign(){
        designWidth = ReferDisplay.getInstance().getDisplayWidth();
        designHeight = ReferDisplay.getInstance().getDisplayHeight();
        return this;
    }
}
