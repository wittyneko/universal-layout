package silicar.tutu.universal.helper.base;

import android.content.Context;
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

    public static DisplayBase getInstance(Context context){
        if (Holder.instance.context != null)
            return Holder.instance;
        else if (context != null){
            return Holder.instance.setContext(context);
        }
        return null;
    }

    ///////// 类定义 //////////

    private Context context;
    public float displayWidth;
    public float displayHeight;

    public DisplayBase(Context context) {
        setContext(context);
    }

    public DisplayBase setContext(Context context) {
        this.context = context;
        if (context != null){
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            displayWidth = displayMetrics.widthPixels;
            displayHeight = displayMetrics.heightPixels;
        }
        return this;
    }
}
