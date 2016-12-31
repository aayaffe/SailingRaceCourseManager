package com.aayaffe.sailingracecoursemanager.dialogs;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 13/07/2016.
 */
public class CourseTypeDialog extends Dialog implements CourseTypeRV.OnRecyclerItemClickListener{


    private Context context;
    private OnMyDialogResult mDialogResult;
    private List<RaceCourseDescriptor> coursesList;

    public CourseTypeDialog(Context context, List<RaceCourseDescriptor> coursesList){
        super(context);
        this.context=context;
        this.coursesList=coursesList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setCancelable(true);

        super.setContentView(R.layout.course_type_dialog);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.course_type_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CourseTypeRV adapter = new CourseTypeRV(coursesList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRecyclerItemClick(RaceCourseDescriptor raceCourseDescriptor){
        CourseTypeSecondDialog dialog = new CourseTypeSecondDialog(context, raceCourseDescriptor);
        dialog.show();
        dialog.setDialogResult(new CourseTypeSecondDialog.OnMyDialogResult() {
            public void finish(Map<String, String> result , List<Double> factorResult) {
                mDialogResult.finish(result, factorResult);
            }
        });
        dismiss();
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }


    public interface OnMyDialogResult{
        void finish(Map<String, String> result , List<Double> factorResult);
    }

}
