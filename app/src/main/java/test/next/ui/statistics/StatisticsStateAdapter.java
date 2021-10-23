package test.next.ui.statistics;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class StatisticsStateAdapter extends FragmentStateAdapter {

    ArrayList<StatMonthViewFragment> fragments;

    public StatisticsStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public StatisticsStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public StatisticsStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragments = new ArrayList<>();
    }

    public void Add(int month, int year)
    {
        fragments.add(new StatMonthViewFragment(month, year));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
