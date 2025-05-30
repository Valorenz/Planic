package com.example.planic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter(List<Task> tasks) {
        this.taskList = tasks;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public ImageView taskImage;
        public TextView taskTitle, taskDescription, taskTime;

        public TaskViewHolder(View view, OnItemClickListener listener, List<Task> tasks) {
            super(view);
            taskImage = view.findViewById(R.id.taskImage);
            taskTitle = view.findViewById(R.id.taskTitle);
            taskDescription = view.findViewById(R.id.taskDescription);
            taskTime = view.findViewById(R.id.taskTime);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(tasks.get(position));
                }
            });
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView, listener, taskList);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.title);

        String description = task.description;
        if (description.length() > 60) {
            description = description.substring(0, 60) + "...";
        }
        holder.taskDescription.setText(description);

        holder.taskTime.setText("Deadline: " + task.deadline);
        holder.taskImage.setImageResource(R.drawable.gambar1);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
