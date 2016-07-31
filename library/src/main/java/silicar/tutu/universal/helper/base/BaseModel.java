package silicar.tutu.universal.helper.base;

/**
 * 基础值接口
 * Created by Brady on 2016/5/2.
 */
public interface BaseModel {

    int objScreen = 0;
    int objParent = 1;
    int objOwn = 2;
    int modeAuto = 0;
    int modePercent= 1;

    // 参照对象
    String OBJ_SCREEN = "s";    //屏幕
    String OBJ_PARENT = "p";    //父控件
    String OBJ_OWN = "o";       //自身

    // 计算模式
    String MODE_AUTO = "a";
    String MODE_PERCENT = "%";

    //参照值
    String WIDTH = "w";
    String HEIGHT = "h";

    int getObject();
    int getMode();
    boolean isWidth();
    float getBaseValue();
    float getBaseWidth();
    float getBaseHeight();
}
