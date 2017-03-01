package silicar.tutu.universal.helper;

import android.support.annotation.StyleRes;

import silicar.tutu.universal.value.ReferDisplay;

/**
 * Created by wittytutu on 17-3-1.
 */

public interface UniversalView {
    ReferDisplay getAutoDisplay();

    void setAutoDisplay(ReferDisplay display);

    int getAutoChildStyle();

    void setAutoChildStyle(@StyleRes int style);
}
