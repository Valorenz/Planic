package com.example.planic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planic.EditEventActivity;
import com.example.planic.model.EventModel;
import com.example.planic.R;
import com.example.planic.model.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskModel> taskList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TaskModel task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.taskTitle.setText(task.title);
        holder.taskDescription.setText(task.description);
        holder.taskTime.setText("Deadline: " + task.deadline);

        Glide.with(holder.itemView.getContext())
                .load(task.imageUrl)
                .placeholder(R.drawable.gambar1)
                .into(holder.taskImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskTime;
        ImageView taskImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskImage = itemView.findViewById(R.id.taskImage);
        }
    }

    public static class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

        public interface OnEventListener {
            void delete(String docId);
            void update(String docId, EventModel event);
        }

        private final Context ctx;
        private final List<EventModel> list;
        private final OnEventListener listener;

        public EventAdapter(Context ctx, List<EventModel> list, OnEventListener listener) {
            this.ctx = ctx;
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ctx).inflate(R.layout.item_event, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            EventModel e = list.get(pos);
            h.tvName.setText(e.getTitle());
            h.tvCategory.setText(e.getCategory());
            h.tvDateTime.setText(e.getDate() + " • " + e.getStart() + "–" + e.getEnd());
            h.tvDesc.setText(e.getDesc());
            Glide.with(ctx).load(e.getImgUrl()).into(h.imgBg);

            h.btnDelete.setOnClickListener(v -> listener.delete(e.getId()));
            h.btnUpdate.setOnClickListener(v -> listener.update(e.getId(), e));

            h.itemView.setOnClickListener(v -> {
                Intent i = new Intent(ctx, EditEventActivity.class);
                i.putExtra("id", e.getId());
                i.putExtra("title", e.getTitle());
                i.putExtra("category", e.getCategory());
                i.putExtra("date", e.getDate());
                i.putExtra("start", e.getStart());
                i.putExtra("end", e.getEnd());
                i.putExtra("desc", e.getDesc());
                i.putExtra("imgUrl", e.getImgUrl());
                ctx.startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvCategory, tvDateTime, tvDesc;
            ImageView imgBg;
            ImageButton btnDelete, btnUpdate;

            public VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_event_name);
                tvCategory = itemView.findViewById(R.id.tv_event_category);
                tvDateTime = itemView.findViewById(R.id.tv_event_datetime);
                tvDesc = itemView.findViewById(R.id.tv_event_desc);
                imgBg = itemView.findViewById(R.id.img_bg_event);
                btnDelete = itemView.findViewById(R.id.btn_delete);
                btnUpdate = itemView.findViewById(R.id.btn_update);
            }
        }
    }
}
