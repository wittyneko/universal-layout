package silicar.tutu.universal.helper.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

/**
 * 屏幕基础信息
 * Created by Brady on 2016/5/7.
 */
public class BaseDisplay {
    ///////// 单例模式 //////////
    private static class Holder{
        private static BaseDisplay instance = new BaseDisplay(null);
    }

    public static BaseDisplay getInstance(){
        //if (Holder.instance.context != null)
        //    return Holder.instance;
        //return null;
        return Holder.instance;
    }

    public static BaseDisplay getInstance(@Nullable Context context){
        return Holder.instance.setResource(context);
    }

    ///////// 类定义 //////////

    //private Context context;
    private Resources mResources;
    private int displayWidth;
    private int displayHeight;

    public BaseDisplay(@Nullable Context context) {
        setResource(context);
    }

    public BaseDisplay setResource(@Nullable Context context) {

        if (context != null){
            mResources = context.getResources();
            return this;
        }

        mResources = Resources.getSystem();
        return this;
    }

    public int getDisplayWidth() {
        displayWidth = mResources.getDisplayMetrics().widthPixels;
        return displayWidth;
    }

    public int getDisplayHeight() {
        displayHeight = mResources.getDisplayMetrics().heightPixels;
        return displayHeight;
    }
}
