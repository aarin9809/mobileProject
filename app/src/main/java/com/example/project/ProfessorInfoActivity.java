package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfessorInfoActivity extends AppCompatActivity {

    private Button viewMessagesButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_info);

        // ViewPager2와 TabLayout 설정
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ProfessorPagerAdapter adapter = new ProfessorPagerAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "이름 검색" : "학과 검색")
        ).attach();

        // SharedPreferences에서 사용자 유형을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userType = sharedPreferences.getString("user_type", "");

        // 교수님인 경우 메시지 확인 버튼 추가
        viewMessagesButton = findViewById(R.id.viewMessagesButton);
        if ("professor".equals(userType)) {
            viewMessagesButton.setVisibility(View.VISIBLE);
            viewMessagesButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfessorInfoActivity.this, ReceiveMessagesActivity.class);
                startActivity(intent);
            });
        } else {
            viewMessagesButton.setVisibility(View.GONE);
        }
    }
}
