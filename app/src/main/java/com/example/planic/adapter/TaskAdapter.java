package com.example.planic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
}
