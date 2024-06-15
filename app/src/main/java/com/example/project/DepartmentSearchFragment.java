package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfessorAdapter adapter;
    private FirebaseFirestore db;
    private Spinner departmentSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_search, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.professorRecyclerView);
        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        MaterialButton searchButton = view.findViewById(R.id.searchButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfessorAdapter(new ArrayList<Professor>(), getContext());
        recyclerView.setAdapter(adapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.departments, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(spinnerAdapter);

        searchButton.setOnClickListener(v -> searchProfessors());

        return view;
    }

    private void searchProfessors() {
        String selectedDepartment = departmentSpinner.getSelectedItem().toString();
        db.collection("Professor")
                .whereEqualTo("department", selectedDepartment)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<Professor> professorList = querySnapshot.toObjects(Professor.class);
                        adapter.updateData(professorList);
                    }
                });
    }
}
