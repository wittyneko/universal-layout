package silicar.tutu.sample;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import silicar.tutu.universal.helper.UniversalDimens;
import silicar.tutu.universal.helper.UniversalLayoutHelper;
import silicar.tutu.universal.helper.UniversalLayoutInfo;
import silicar.tutu.universal.helper.UniversalValue;
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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
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
                codeView2.getLayoutParams().width = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
                codeView2.getLayoutParams().height = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
                codeView2.requestLayout();
                break;
            default:
                return;
        }
        if (intent != null)
            startActivity(intent);
    }
}
