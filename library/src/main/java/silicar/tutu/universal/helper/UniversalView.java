package silicar.tutu.universal.helper;

import android.support.annotation.StyleRes;

import silicar.tutu.universal.helper.base.BaseDisplay;

/**
 * Created by wittytutu on 17-3-1.
 */

public interface UniversalView {
    BaseDisplay getAutoDisplay();

    void setAutoDisplay(BaseDisplay display);

    int getAutoChildStyle();

    void setAutoChildStyle(@StyleRes int style);
}
