package silicar.tutu.universal.helper.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

/**
 * 屏幕百分比基础值
 * Created by Brady on 2016/5/3.
 */
public class PercentScreenBase extends AbsPercentBase {

    ///////// 单例模式 //////////

    private static class Holder{
        private static PercentScreenBase instance = new PercentScreenBase(null, true);
    }

    public static PercentScreenBase syncInstance(){
        synchronized (PercentScreenBase.class) {
            Holder.instance.isWidth = true;
            return Holder.instance;
        }
    }

    public static PercentScreenBase getInstance(){
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static PercentScreenBase getInstance(boolean isWidth){
        Holder.instance.isWidth = isWidth;
        return Holder.instance;
    }

    public static PercentScreenBase getInstance(@Nullable Resources resources, boolean isWidth){
        Holder.instance.isWidth = isWidth;
        Holder.instance.setResource(resources);
        return Holder.instance;
    }

    ///////// 类定义 //////////

    //private Context context;
    private Resources resources;

    public PercentScreenBase(@Nullable Resources resources, boolean isWidth){
        this(0, 0, isWidth);
        setResource(resources);
    }

    public PercentScreenBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objScreen;
        return obj;
    }

    public PercentScreenBase setResource(@Nullable Resources resources) {
        this.resources = resources;
        DisplayBase displayBase = DisplayBase.getInstance(resources);
        widthValue = displayBase.displayWidth;
        heightValue = displayBase.displayHeight;
        return this;
    }
}
