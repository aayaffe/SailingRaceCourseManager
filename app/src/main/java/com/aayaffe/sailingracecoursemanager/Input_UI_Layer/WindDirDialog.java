package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;

/**
 * Created by Jonathan on 04/11/2016.
 */
public class WindDirDialog extends Dialog {

    private Context context;
    private OnMyDialogResult myDialogResult;
    private EditText windSelector;

    public WindDirDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.wind_dir_dialog);


        TextView titleV=(TextView) findViewById(R.id.wind_dir_dialog_title);   //set dialog title
        titleV.setText("Wind Direction");
        titleV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleV.setGravity(Gravity.CENTER);

        windSelector = (EditText) findViewById(R.id.wind_dialogs_picker);

        Button applyB = (Button)findViewById(R.id.apply_wind_dialog_button);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogResult.finish(Double.parseDouble(windSelector.getText().toString()));
                dismiss();
            }
        });

    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        myDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(double windDir);
    }
}
