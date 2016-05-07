package silicar.tutu.universal.helper.base;

import android.content.Context;
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

    public static PercentScreenBase getInstance(){
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static PercentScreenBase getInstance(boolean isWidth){
        Holder.instance.isWidth = isWidth;
        return Holder.instance;
    }

    public static PercentScreenBase getInstance(Context context, boolean isWidth){
        Holder.instance.isWidth = isWidth;
        Holder.instance.setContext(context);
        return Holder.instance;
    }

    ///////// 类定义 //////////

    private Context context;

    public PercentScreenBase(Context context, boolean isWidth){
        this(0, 0, isWidth);
        setContext(context);
    }

    public PercentScreenBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objScreen;
        return obj;
    }

    public PercentScreenBase setContext(Context context) {
        this.context = context;
        if (context != null){
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            widthValue = displayMetrics.widthPixels;
            heightValue = displayMetrics.heightPixels;
        }
        return this;
    }
}
