package silicar.tutu.universal.value;

/**
 * 参数信息
 * Created by Brady on 2016/5/2.
 */
public class UniversalValue {
    public float value;
    public IMeasureModel model;

    public UniversalValue() {
    }

    public UniversalValue(float value, IMeasureModel model) {
        this.value = value;
        this.model = model;
    }

    public float getMeasureValue() {
        return value * model.getBaseValue();
    }
}
