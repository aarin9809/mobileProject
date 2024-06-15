package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.databinding.ActivitySendMessageBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class SendMessageActivity extends AppCompatActivity {
    private ActivitySendMessageBinding binding;
    private FirebaseFirestore db;
    private String professorId;
    private String professorName;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // SharedPreferences에서 사용자 ID와 이름 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);
        userName = sharedPreferences.getString("user_name", null);

        if (userId == null || userName == null) {
            // 사용자가 로그인되지 않았으므로 LoginActivity로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Get professor details from intent
        professorId = getIntent().getStringExtra("professor_id");
        professorName = getIntent().getStringExtra("professor_name");

        if (professorId == null || professorId.isEmpty()) {
            Toast.makeText(this, "수신자 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.professorNameTextView.setText(professorName);

        binding.sendButton.setOnClickListener(v -> {
            String messageContent = binding.messageContentEditText.getText().toString().trim();
            if (validateInputs(professorId, messageContent)) {
                sendMessage(professorId, messageContent, userId, userName);
            }
        });
    }

    private boolean validateInputs(String receiverId, String messageContent) {
        if (receiverId == null || receiverId.isEmpty()) {
            Toast.makeText(this, "수신자 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (messageContent.isEmpty()) {
            binding.messageContentEditText.setError("메시지를 입력하세요");
            return false;
        }

        return true;
    }

    private void sendMessage(String receiverId, String messageContent, String senderId, String senderName) {
        Message message = new Message(senderId, receiverId, messageContent, senderName);
        message.setTimestamp(new java.util.Date());
        message.setStatus("sent");

        db.collection("Messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SendMessageActivity.this, "메시지 전송 성공", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SendMessageActivity.this, "메시지 전송 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
