package com.example.project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfessorPagerAdapter extends FragmentStateAdapter {

    public ProfessorPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NameSearchFragment();
            case 1:
                return new DepartmentSearchFragment();
            default:
                return new NameSearchFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
