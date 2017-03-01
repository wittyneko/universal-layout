package silicar.tutu.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import silicar.tutu.universal.helper.UniversalDimens;
import silicar.tutu.universal.helper.UniversalLayoutHelper;
import silicar.tutu.universal.helper.UniversalLayoutInfo;
import silicar.tutu.universal.helper.UniversalValue;
import silicar.tutu.universal.helper.base.BaseDisplay;
import silicar.tutu.universal.helper.base.BaseModel;
import silicar.tutu.universal.helper.base.SampleModel;
import silicar.tutu.universal.widget.UniversalLinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private View codeView;
    private View codeView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.auto_screen).setOnClickListener(this);
        findViewById(R.id.percent_parent).setOnClickListener(this);
        codeView = findViewById(R.id.code_set);
        codeView2 = findViewById(R.id.code_set_2);
        codeView.setOnClickListener(this);
        codeView2.setOnClickListener(this);

        getStyleInfo();

        getDisplayInfo();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.auto_screen:
                intent = new Intent(this, AutoScreenActivity.class);
                break;
            case R.id.percent_parent:
                intent = new Intent(this, PercentActivity.class);
                break;
            case R.id.code_set:
                UniversalLinearLayout.LayoutParams params = (UniversalLinearLayout.LayoutParams) codeView.getLayoutParams();
                UniversalLayoutInfo info = params.getUniversalLayoutInfo();
                info.width.value = 0.8f;
                //SampleModel model = (SampleModel) info.width.model;
                //model.setMode(BaseModel.modePercent).setObj(BaseModel.objScreen);
                info.width.model = new SampleModel(BaseModel.modePercent, BaseModel.objScreen, true);
                info.height.value = 50;
                codeView.requestLayout();
                break;
            case R.id.code_set_2:
                UniversalValue universalValue = new UniversalValue(300, new SampleModel().getDefaultDesign());
                codeView2.getLayoutParams().width = (int) UniversalDimens.getUniversalDimens(universalValue, BaseDisplay.getInstance());
                codeView2.getLayoutParams().height = (int) UniversalDimens.getUniversalDimens(universalValue, BaseDisplay.getInstance());
                codeView2.requestLayout();
                break;
            default:
                return;
        }
        if (intent != null)
            startActivity(intent);
    }

    /**
     * 获取样式参数
     */
    private void getStyleInfo() {

        TypedArray array = obtainStyledAttributes(R.style.Design320x568, R.styleable.UniversalLayoutInfo);
        float widthDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_widthDesign, 0);
        float heightDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_heightDesign, 0);
        Log.e("Design320x568", "width:" + widthDesign + "; height:" + heightDesign);
        array.recycle();
        array = obtainStyledAttributes(R.style.Design640x1136, R.styleable.UniversalLayoutInfo);
        widthDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_widthDesign, 0);
        heightDesign = array.getDimension(R.styleable.UniversalLayoutInfo_layout_heightDesign, 0);
        Log.e("Design640x1136", "width:" + widthDesign + "; height:" + heightDesign);
        array.recycle();
    }

    /**
     * 获取屏幕信息
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void getDisplayInfo() {

        /**
         * <supports-screens
         * android:smallScreens="true"
         * android:normalScreens="true"
         * android:largeScreens="true"
         * android:resizeable="true"
         * android:anyDensity="true" />
         * <uses-sdk android:minSdkVersion="3" android:targetSdkVersion="8" />
         * 使Android程序支持了多种分辨率
         */
        int mWidthScreen, mHeightScreen;

        Display display = getWindowManager().getDefaultDisplay();
        mWidthScreen = display.getWidth();
        mHeightScreen = display.getHeight();
        Log.e("display", "Width：" + display.getWidth());
        Log.e("display", "Height：" + display.getHeight());

        Log.e("display", "-\n");

        Point point = new Point();
        display.getSize(point);
        Log.e("display", "Width：" + point.x);
        Log.e("display", "Height：" + point.y);

        Log.e("display", "-\n");

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidthScreen = outMetrics.widthPixels;
        mHeightScreen = outMetrics.heightPixels;
        Log.e("display", "Width：" + outMetrics.widthPixels);
        Log.e("display", "Height：" + outMetrics.heightPixels);
        Log.e("display", "xdpi：" + outMetrics.xdpi);
        Log.e("display", "ydpi：" + outMetrics.ydpi);
        Log.e("display", "densityDpi：" + outMetrics.densityDpi);
        Log.e("display", "density：" + outMetrics.density);
        Log.e("display", "scaledDensity：" + outMetrics.scaledDensity);
        Log.e("display", "spWidth：" + (outMetrics.widthPixels / outMetrics.scaledDensity + 0.5f));
        Log.e("display", "spHeight：" + (outMetrics.heightPixels / outMetrics.scaledDensity + 0.5f));
        Log.e("display", "dpWidth：" + (outMetrics.widthPixels / outMetrics.density + 0.5f));
        Log.e("display", "dpHeight：" + (outMetrics.heightPixels / outMetrics.density + 0.5f));

        Log.e("display", "-\n");

        DisplayMetrics resMetrics = getResources().getDisplayMetrics();
        mHeightScreen = resMetrics.heightPixels;
        mWidthScreen = resMetrics.widthPixels;
        Log.e("display", "Width：" + resMetrics.widthPixels);
        Log.e("display", "Height：" + resMetrics.heightPixels);
        Log.e("display", "xdpi：" + resMetrics.xdpi);
        Log.e("display", "ydpi：" + resMetrics.ydpi);
        Log.e("display", "densityDpi：" + resMetrics.densityDpi);
        Log.e("display", "density：" + resMetrics.density);
        Log.e("display", "scaledDensity：" + resMetrics.scaledDensity);
        Log.e("display", "spWidth：" + (int) (resMetrics.widthPixels / resMetrics.scaledDensity + 0.5f));
        Log.e("display", "spHeight：" + (int) (resMetrics.heightPixels / resMetrics.scaledDensity + 0.5f));
        Log.e("display", "dpWidth：" + (resMetrics.widthPixels / resMetrics.density + 0.5f));
        Log.e("display", "dpHeight：" + (resMetrics.heightPixels / resMetrics.density + 0.5f));

        Log.e("display", "-\n");

        resMetrics = Resources.getSystem().getDisplayMetrics();
        mHeightScreen = resMetrics.heightPixels;
        mWidthScreen = resMetrics.widthPixels;
        Log.e("display", "Width：" + resMetrics.widthPixels);
        Log.e("display", "Height：" + resMetrics.heightPixels);
        Log.e("display", "xdpi：" + resMetrics.xdpi);
        Log.e("display", "ydpi：" + resMetrics.ydpi);
        Log.e("display", "densityDpi：" + resMetrics.densityDpi);
        Log.e("display", "density：" + resMetrics.density);
        Log.e("display", "scaledDensity：" + resMetrics.scaledDensity);
        Log.e("display", "spWidth：" + (int) (resMetrics.widthPixels / resMetrics.scaledDensity + 0.5f));
        Log.e("display", "spHeight：" + (int) (resMetrics.heightPixels / resMetrics.scaledDensity + 0.5f));
        Log.e("display", "dpWidth：" + (resMetrics.widthPixels / resMetrics.density + 0.5f));
        Log.e("display", "dpHeight：" + (resMetrics.heightPixels / resMetrics.density + 0.5f));
    }
}
