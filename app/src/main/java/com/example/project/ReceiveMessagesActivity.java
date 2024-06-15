package com.example.project;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.project.databinding.ActivityReceiveMessagesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ReceiveMessagesActivity extends AppCompatActivity {
    private ActivityReceiveMessagesBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MessagesAdapter adapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        messageList = new ArrayList<>();

        adapter = new MessagesAdapter(messageList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        loadMessages();
    }

    private void loadMessages() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        db.collection("Messages")
                .whereEqualTo("receiverId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
