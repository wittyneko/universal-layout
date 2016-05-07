package silicar.tutu.universal.helper;

import silicar.tutu.universal.helper.base.IBase;

/**
 * 参数信息
 * Created by Brady on 2016/5/2.
 */
public class UniversalValue {
    public float value;
    public IBase base;

    public UniversalValue() {
    }

    public UniversalValue(float value, IBase base) {
        this.value = value;
        this.base = base;
    }
}
