package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.project.databinding.ActivityReceiveMessagesBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ReceiveMessagesActivity extends AppCompatActivity {
    private ActivityReceiveMessagesBinding binding;
    private FirebaseFirestore db;
    private MessagesAdapter adapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        messageList = new ArrayList<>();

        adapter = new MessagesAdapter(messageList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        loadMessages();
    }

    private void loadMessages() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String professorId = sharedPreferences.getString("user_id", null);

        if (professorId == null || professorId.isEmpty()) {
            Toast.makeText(this, "교수님 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("Messages")
                .whereEqualTo("receiverId", professorId)
                .orderBy("timestamp", Query.Direction.DESCENDING) // 시간순으로 정렬
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageList.clear(); // 중복 추가를 방지하기 위해 리스트를 먼저 클리어
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            messageList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ReceiveMessagesActivity.this, "메시지 로드 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
