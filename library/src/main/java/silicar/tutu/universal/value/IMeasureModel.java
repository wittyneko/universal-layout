package silicar.tutu.universal.value;

/**
 * 基础值接口
 * Created by Brady on 2016/5/2.
 */
public interface IMeasureModel {

    public final static int refScreen = 0;
    public final static int refParent = 1;
    public final static int refOwn = 2;
    public final static int modeAuto = 0;
    public final static int modePercent= 1;

    // 参照对象
    public final static String REF_SCREEN = "s";    //屏幕
    public final static String REF_PARENT = "p";    //父控件
    public final static String REF_OWN = "o";       //自身

    // 计算模式
    public final static String MODE_AUTO = "a";
    public final static String MODE_PERCENT = "%";

    //参照值
    public final static String WIDTH = "w";
    public final static String HEIGHT = "h";

    int getReferObject();
    int getMode();
    boolean isWidth();
    float getBaseValue();
    float getBaseWidth();
    float getBaseHeight();
}
