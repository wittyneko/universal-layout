package silicar.tutu.universal.helper.base;

/**
 * 自身百分比基础值
 * 自身百分比要进行两次计算
 * Created by Brady on 2016/5/3.
 */
public class PercentOwnBase extends AbsPercentBase {

    ///////// 单例模式 //////////

    private static class Holder {
        private static PercentOwnBase instance = new PercentOwnBase(true);
    }

    public static PercentOwnBase syncInstance() {
        synchronized (PercentOwnBase.class) {
            Holder.instance.widthValue = 0f;
            Holder.instance.heightValue = 0f;
            Holder.instance.isWidth = true;
            return Holder.instance;
        }
    }

    public static PercentOwnBase getInstance() {
        Holder.instance.widthValue = 0f;
        Holder.instance.heightValue = 0f;
        Holder.instance.isWidth = true;
        return Holder.instance;
    }

    public static PercentOwnBase getInstance(boolean isWidth) {
        Holder.instance.widthValue = 0f;
        Holder.instance.heightValue = 0f;
        Holder.instance.isWidth = isWidth;
        return Holder.instance;
    }

    ///////// 类定义 //////////

    public PercentOwnBase(boolean isWidth) {
        this(0f, 0f, isWidth);
    }

    public PercentOwnBase(float widthValue, float heightValue, boolean isWidth) {
        super(widthValue, heightValue, isWidth);
    }

    @Override
    public int getObject() {
        obj = objOwn;
        return obj;
    }
}
