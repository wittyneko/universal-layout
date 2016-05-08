package silicar.tutu.universal.helper.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

/**
 * 屏幕自适应基础值
 * Created by Brady on 2016/5/2.
 */
public class AutoBase extends AbsAutoBase {

    ///////// 单例模式 //////////

    private static class Holder{
        private static AutoBase instance = new AutoBase(null,
                DisplayBase.getInstance().displayWidth, DisplayBase.getInstance().displayHeight, true);
    }

    public static AutoBase syncInstance(){
        synchronized (AutoBase.class) {
            Holder.instance.designWidth = 0f;
            Holder.instance.designHeight = 0f;
            Holder.instance.isWidth = true;
            return Holder.instance;
        }
    }

    public static AutoBase getInstance(){
        Holder.instance.designWidth = 0f;
        Holder.instance.designHeight = 0f;
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static AutoBase getInstance(float designWidth, float designHeight, boolean isWidth){
        Holder.instance.designWidth = designWidth;
        Holder.instance.designHeight = designHeight;
        Holder.instance.isWidth = isWidth;
        return Holder.instance;
    }

    public static AutoBase getInstance(@Nullable Resources resources, float designWidth, float designHeight, boolean isWidth){
        Holder.instance.designWidth = designWidth;
        Holder.instance.designHeight = designHeight;
        Holder.instance.isWidth = isWidth;
        Holder.instance.setResource(resources);
        return Holder.instance;
    }

    ///////// 类定义 //////////

    //private Context context;
    private Resources resources;

    public AutoBase(@Nullable Resources resources, float designWidth, float designHeight, boolean isWidth){
        this(designWidth, designHeight, 0, 0, isWidth);
        setResource(resources);
    }

    public AutoBase(float designWidth, float designHeight, float widthValue, float heightValue, boolean isWidth) {
        super(designWidth, designHeight, widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objScreen;
        return obj;
    }

    public AutoBase setResource(@Nullable Resources resources) {
        this.resources = resources;
        DisplayBase displayBase = DisplayBase.getInstance(resources);
        widthValue = displayBase.displayWidth;
        heightValue = displayBase.displayHeight;
        return this;
    }
}
