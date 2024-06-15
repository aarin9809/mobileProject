package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NameSearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfessorAdapter adapter;
    private FirebaseFirestore db;
    private TextInputEditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name_search, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.professorRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        MaterialButton searchButton = view.findViewById(R.id.searchButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfessorAdapter(new ArrayList<Professor>(), getContext());
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> searchProfessors());

        return view;
    }

    private void searchProfessors() {
        String searchName = searchEditText.getText().toString();
        db.collection("Professor")
                .whereGreaterThanOrEqualTo("name", searchName)
                .whereLessThanOrEqualTo("name", searchName + "\uf8ff")
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
