package com.example.project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth != null ? mAuth.getCurrentUser() : null;
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Edge-to-Edge 기능 대체 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                insetsController.hide(WindowInsets.Type.systemBars());
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.loginButton.setOnClickListener(v -> {
            String studentId = binding.studentIdEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (validateInputs(studentId, password)) {
                performLogin(studentId, password);
            }
        });
    }

    private boolean validateInputs(String stu_id, String password) {
        if (stu_id.isEmpty()) {
            binding.studentIdEditText.setError("학번을 입력하세요");
            return false;
        }

        if (password.isEmpty()) {
            binding.passwordEditText.setError("비밀번호를 입력하세요");
            return false;
        }

        return true;
    }

    private void performLogin(String studentId, String password) {
        db.collection("Students")
                .whereEqualTo("stu_id", studentId)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // 로그인 성공, 사용자 정보 업데이트
                        Log.d(TAG, "Login successful");
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                        // SharedPreferences에 사용자 ID 저장
                        String userId = task.getResult().getDocuments().get(0).getId();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id", userId);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, ProfessorInfoActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 로그인 실패
                        Log.w(TAG, "Login failed", task.getException());
                        Toast.makeText(LoginActivity.this, "로그인 실패: 유효하지 않은 학번 또는 비밀번호", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
