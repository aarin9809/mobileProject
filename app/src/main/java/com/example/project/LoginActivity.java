package com.example.project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseFirestore db;
    private int captchaResult;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // Initialize Captcha
        initializeCaptcha();

        // Set click listener for captcha button as confirm
        binding.Buttoncaptcha.setOnClickListener(v -> {
            if (validateCaptcha()) {
                binding.loginButton.setVisibility(View.VISIBLE);
            } else {
                binding.loginButton.setVisibility(View.GONE);
            }
        });

        // Set click listener for login button
        binding.loginButton.setOnClickListener(v -> {
            String userId = binding.studentIdEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (validateInputs(userId, password)) {
                performLogin(userId, password);
            }
        });
    }

    private void initializeCaptcha() {
        // Initial captcha generation
        generateCaptcha();
    }

    private void generateCaptcha() {
        Random random = new Random();
        int num1 = random.nextInt(10); // generate a number between 0 and 9
        int num2 = random.nextInt(10); // generate a number between 0 and 9
        captchaResult = num1 + num2;

        binding.captchaTextView.setText("Solve: " + num1 + " + " + num2);
        binding.loginButton.setVisibility(View.GONE); // Hide login button until captcha is solved
    }

    private boolean validateCaptcha() {
        String captchaInput = binding.captchaEditText.getText().toString().trim();
        if (captchaInput.isEmpty()) {
            binding.captchaEditText.setError("Please solve the captcha");
            return false;
        }

        int captchaAnswer;
        try {
            captchaAnswer = Integer.parseInt(captchaInput);
        } catch (NumberFormatException e) {
            binding.captchaEditText.setError("Invalid captcha answer");
            return false;
        }

        if (captchaAnswer == captchaResult) {
            binding.captchaEditText.setError(null);
            return true;
        } else {
            binding.captchaEditText.setError("Incorrect captcha answer");
            return false;
        }
    }

    private boolean validateInputs(String userId, String password) {
        if (userId.isEmpty()) {
            binding.studentIdEditText.setError("ID를 입력하세요");
            return false;
        }

        if (password.isEmpty()) {
            binding.passwordEditText.setError("비밀번호를 입력하세요");
            return false;
        }

        return true;
    }

    private void performLogin(String userId, String password) {
        db.collection("Students")
                .whereEqualTo("stu_id", userId)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // 학생 로그인 성공
                        Log.d(TAG, "Student login successful");
                        Toast.makeText(LoginActivity.this, "학생 로그인 성공", Toast.LENGTH_SHORT).show();

                        // SharedPreferences에 사용자 ID 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id", userId);
                        editor.putString("user_type", "student");
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, ProfessorInfoActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 학생 로그인 실패 -> 교수님 로그인 시도
                        Log.d(TAG, "Student login failed, trying professor login");
                        db.collection("Professor")
                                .whereEqualTo("id", userId)
                                .whereEqualTo("password", password)
                                .get()
                                .addOnCompleteListener(professorTask -> {
                                    if (professorTask.isSuccessful()) {
                                        boolean found = false;
                                        for (QueryDocumentSnapshot document : professorTask.getResult()) {
                                            Log.d(TAG, "Found document: " + document.getData()); // 추가 로그
                                            found = true;
                                            // 교수님 로그인 성공
                                            Log.d(TAG, "Professor login successful");
                                            Toast.makeText(LoginActivity.this, "교수님 로그인 성공", Toast.LENGTH_SHORT).show();

                                            // SharedPreferences에 교수님 ID 저장
                                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("user_id", userId);
                                            editor.putString("user_type", "professor");
                                            editor.apply();

                                            Intent intent = new Intent(LoginActivity.this, ProfessorInfoActivity.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        }
                                        if (!found) {
                                            Log.w(TAG, "Professor login failed: No matching document found");
                                            Toast.makeText(LoginActivity.this, "로그인 실패: 유효하지 않은 교수님 ID 또는 비밀번호", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // 로그인 실패
                                        Log.w(TAG, "Professor login failed", professorTask.getException());
                                        Toast.makeText(LoginActivity.this, "로그인 실패: 유효하지 않은 교수님 ID 또는 비밀번호", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}
