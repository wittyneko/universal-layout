package silicar.tutu.universal.helper.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

/**
 * 屏幕基础信息
 * Created by Brady on 2016/5/7.
 */
public class DisplayBase {
    ///////// 单例模式 //////////
    private static class Holder{
        private static DisplayBase instance = new DisplayBase(null);
    }

    public static DisplayBase getInstance(){
        //if (Holder.instance.context != null)
        //    return Holder.instance;
        //return null;
        return Holder.instance;
    }

    public static DisplayBase getInstance(@Nullable Resources resources){
        if (Holder.instance.resources == null && resources != null){
            return Holder.instance.setResource(resources);
        }
        return Holder.instance;
    }

    ///////// 类定义 //////////

    //private Context context;
    private Resources resources;
    public float displayWidth;
    public float displayHeight;

    public DisplayBase(@Nullable Resources resources) {
        setResource(resources);
    }

    public DisplayBase setResource(@Nullable Resources resource) {
        this.resources = resource;
        if (resource != null){
            DisplayMetrics displayMetrics = resource.getDisplayMetrics();
            displayWidth = displayMetrics.widthPixels;
            displayHeight = displayMetrics.heightPixels;
        }
        return this;
    }
}
