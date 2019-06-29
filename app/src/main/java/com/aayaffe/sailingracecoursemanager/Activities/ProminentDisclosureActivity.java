package com.aayaffe.sailingracecoursemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;

public class ProminentDisclosureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prominent_disclosure);
        TextView t = findViewById(R.id.textView);
        t.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onContinueButtonClick(View v){
        Intent i = new Intent();
        Switch s = findViewById(R.id.agree_switch);
        if (s!=null && s.isChecked()) {
            setResult(RESULT_OK, i);
        }else{
            setResult(RESULT_CANCELED, i);
        }
        finish();

    }
}
