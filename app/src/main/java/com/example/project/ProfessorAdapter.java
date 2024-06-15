package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ProfessorViewHolder> {
    private List<Professor> professorList;
    private Context context;

    public ProfessorAdapter(List<Professor> professorList, Context context) {
        this.professorList = professorList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfessorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.professor_item, parent, false);
        return new ProfessorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorViewHolder holder, int position) {
        Professor professor = professorList.get(position);
        holder.nameTextView.setText(professor.getName());
        holder.departmentTextView.setText(professor.getDepartment());
        holder.contactTextView.setText(professor.getPhone());
        holder.officeTextView.setText(professor.getOffice());
        holder.emailTextView.setText(professor.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SendMessageActivity.class);
            intent.putExtra("professor_id", professor.getId());
            intent.putExtra("professor_name", professor.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return professorList.size();
    }

    public void updateData(List<Professor> newProfessorList) {
        this.professorList = newProfessorList;
        notifyDataSetChanged();
    }

    public static class ProfessorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, departmentTextView, contactTextView, officeTextView, emailTextView;

        public ProfessorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            officeTextView = itemView.findViewById(R.id.officeTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }
    }
}
