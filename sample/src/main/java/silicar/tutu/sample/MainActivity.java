package silicar.tutu.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import silicar.tutu.universal.UniversalLayoutHelper.UniversalLayoutInfo;
import silicar.tutu.universal.UniversalLinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private View codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.auto_screen).setOnClickListener(this);
        findViewById(R.id.percent_parent).setOnClickListener(this);
        findViewById(R.id.percent_screen).setOnClickListener(this);
        codeView = findViewById(R.id.code_set);
        codeView.setOnClickListener(this);
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
            case R.id.percent_screen:
                intent = new Intent(this, PercentScreenActivity.class);
                break;
            case R.id.code_set:
                UniversalLinearLayout.LayoutParams params = (UniversalLinearLayout.LayoutParams) codeView.getLayoutParams();
                UniversalLayoutInfo info = params.getUniversalLayoutInfo();
                info.widthUniversal.percent = 0.8f;
                info.widthUniversal.basemode = UniversalLayoutInfo.BaseMode.PERCENT_WIDTH;
                info.heightUniversal.percent = 50;
                codeView.requestLayout();
                break;
            default:
                return;
        }
        if (intent != null)
            startActivity(intent);
    }
}
