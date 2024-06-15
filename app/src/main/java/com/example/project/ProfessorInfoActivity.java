package com.example.project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfessorInfoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ProfessorAdapter professorAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_info);

        db = FirebaseFirestore.getInstance();

        // ViewPager2와 TabLayout 설정
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ProfessorPagerAdapter adapter = new ProfessorPagerAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "이름 검색" : "학과 검색")
        ).attach();
    }

    public void searchProfessors(String department, String name) {
        Query query = db.collection("Professor");

        if (department != null && !department.isEmpty()) {
            query = query.whereEqualTo("department", department);
        }
        if (name != null && !name.isEmpty()) {
            query = query.orderBy("name").startAt(name).endAt(name + "\uf8ff");
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Professor> professorList = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Professor professor = document.toObject(Professor.class);
                    professorList.add(professor);
                }
                professorAdapter.updateData(professorList);
            } else {
                Toast.makeText(ProfessorInfoActivity.this, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
