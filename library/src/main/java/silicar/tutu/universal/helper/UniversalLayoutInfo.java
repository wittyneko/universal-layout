package silicar.tutu.universal.helper;

import android.view.ViewGroup;

/**
 * 布局信息
 * Created by Brady on 2016/5/2.
 */
public class UniversalLayoutInfo {

    public float widthDesign;
    public float heightDesign;
    public boolean isWidth;
    public boolean usePadding;

    public UniversalValue width;
    public UniversalValue height;

    public UniversalValue leftMargin;
    public UniversalValue topMargin;
    public UniversalValue rightMargin;
    public UniversalValue bottomMargin;
    public UniversalValue startMargin;
    public UniversalValue endMargin;

    public UniversalValue maxWidth;
    public UniversalValue maxHeight;
    public UniversalValue minWidth;
    public UniversalValue minHeight;

    public UniversalValue paddingLeft;
    public UniversalValue paddingRight;
    public UniversalValue paddingTop;
    public UniversalValue paddingBottom;

    public UniversalValue textSize;

    /* package */
    ViewGroup.MarginLayoutParams mPreservedParams;


    public UniversalLayoutInfo()
    {
        clear();
        mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
    }

    public void clear(){
        widthDesign = 0;
        heightDesign = 0;
        isWidth = false;
        usePadding = false;

        width = null;
        height = null;

        leftMargin = null;
        topMargin = null;
        rightMargin = null;
        bottomMargin = null;
        startMargin = null;
        endMargin = null;

        maxWidth = null;
        maxHeight = null;
        minWidth = null;
        minHeight = null;

        paddingLeft = null;
        paddingRight = null;
        paddingTop = null;
        paddingBottom = null;

        textSize = null;
    }
}
