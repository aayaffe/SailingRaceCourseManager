package com.aayaffe.sailingracecoursemanager.dialogs;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;

import java.util.List;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 13/07/2016.
 */
public class CourseTypeRV extends RecyclerView.Adapter<CourseTypeRV.AdapterViewHolder> {
    public List<RaceCourseDescriptor> infoList;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public CourseTypeRV(List<RaceCourseDescriptor> infoList , OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.infoList=infoList;
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(RaceCourseDescriptor raceCourseDescriptorOptions);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView courseName;
        public ImageView courseImage;
        public RaceCourseDescriptor raceCourseDescriptorOptions;
        private OnRecyclerItemClickListener onRecyclerItemClickListener;
        public AdapterViewHolder(View itemView,OnRecyclerItemClickListener onRecyclerItemClickListener){
            super(itemView);
            courseName= (TextView)itemView.findViewById(R.id.course_type_name);
            courseImage= (ImageView)itemView.findViewById(R.id.course_type_image);
            this.onRecyclerItemClickListener = onRecyclerItemClickListener;
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            onRecyclerItemClickListener.onRecyclerItemClick(raceCourseDescriptorOptions);
        }
    }
    public void changeList(List<RaceCourseDescriptor> infoList){
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
        avh.raceCourseDescriptorOptions = infoList.get(i);
        avh.courseName.setText(infoList.get(i).getName());
        avh.courseImage.setImageResource(infoList.get(i).getImageID());
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}
