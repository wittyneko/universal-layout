package silicar.tutu.universal.helper.base;

import android.support.annotation.Nullable;
import android.view.ViewGroup;

/**
 * 屏幕百分比基础值
 * 两种计算模式，指定父控件和未指定父控件
 * Created by Brady on 2016/5/3.
 */
public class PercentParentBase extends AbsPercentBase {

    ///////// 单例模式 //////////

    private static class Holder{
        private static PercentParentBase instance = new PercentParentBase(null, true);
    }

    public static PercentParentBase syncInstance(){
        synchronized (PercentParentBase.class) {
            Holder.instance.widthValue = 0f;
            Holder.instance.heightValue = 0f;
            Holder.instance.isWidth = true;
            return Holder.instance;
        }
    }

    public static PercentParentBase getInstance(){
        Holder.instance.widthValue = 0f;
        Holder.instance.heightValue = 0f;
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static PercentParentBase getInstance(@Nullable ViewGroup parent, boolean isWidth){
        Holder.instance.widthValue = 0f;
        Holder.instance.heightValue = 0f;
        Holder.instance.isWidth = isWidth;
        Holder.instance.setParent(parent);
        return Holder.instance;
    }

    ///////// 类定义 //////////

    private ViewGroup parent;

    public PercentParentBase(@Nullable ViewGroup parent, boolean isWidth) {
        this(0, 0, isWidth);
        setParent(parent);
    }

    public PercentParentBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objParent;
        return obj;
    }

    public PercentParentBase setParent(@Nullable ViewGroup parent) {
        this.parent = parent;
        if (parent != null){
            widthValue = parent.getMeasuredWidth();
            heightValue = parent.getMeasuredHeight();
        }
        return this;
    }
}
