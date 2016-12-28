package com.aayaffe.sailingracecoursemanager.dialogs;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.initializinglayer.CourseType;
import com.aayaffe.sailingracecoursemanager.R;

import java.util.List;

/**
 * Created by Jonathan on 13/07/2016.
 */
public class CourseTypeRV extends RecyclerView.Adapter<CourseTypeRV.AdapterViewHolder> {
    public List<CourseType> infoList;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public CourseTypeRV(List<CourseType> infoList ,OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.infoList=infoList;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(CourseType courseTypeOptions);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView courseName;
        public ImageView courseImage;
        public CourseType courseTypeOptions;
        private OnRecyclerItemClickListener onRecyclerItemClickListener;
        public AdapterViewHolder(View itemView,OnRecyclerItemClickListener onRecyclerItemClickListener){
            super(itemView);
            courseName= (TextView)itemView.findViewById(R.id.course_type_name);
            courseImage= (ImageView)itemView.findViewById(R.id.course_type_image);
            this.onRecyclerItemClickListener = onRecyclerItemClickListener;
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            onRecyclerItemClickListener.onRecyclerItemClick(courseTypeOptions);
        }
    }
    public void changeList(List<CourseType> infoList){
        this.infoList.clear();
        this.infoList.addAll(infoList);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_type_card, viewGroup, false);
        return new AdapterViewHolder(v, onRecyclerItemClickListener);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder avh, int i) {        //on each message template
        avh.courseTypeOptions = infoList.get(i);
        avh.courseName.setText(infoList.get(i).getName());
        avh.courseImage.setBackgroundResource(infoList.get(i).getImageID());
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}
