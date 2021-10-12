package test.next.ui.home;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import test.next.R;
import test.next.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CalendarStateAdapter calendarStateAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarStateAdapter = new CalendarStateAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        calendarStateAdapter.feedsList = new ArrayList<>();

        setFeedList();


        binding.viewPager.setAdapter(calendarStateAdapter);
        binding.viewPager.setCurrentItem(24, false);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                Date date = calendarStateAdapter.feedsList.get(position);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(setTitle(date.getMonth(), date.getYear()));
                if (position == calendarStateAdapter.feedsList.size() - 2) {

                    calendarStateAdapter.AddEnd(new Date(date.getYear(), date.getMonth() + 2, 1));
                }


            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });





        return root;
    }

    void setFeedList()
    {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        for(int i = 24; i >= 1; i--)
        {
            setFeedListMonth(new Date(date.getYear(), date.getMonth() - i, 1));
        }
        setFeedListMonth(new Date(date.getYear(), date.getMonth(), 1));
        setFeedListMonth(new Date(date.getYear(), date.getMonth() + 1, 1));
        setFeedListMonth(new Date(date.getYear(), date.getMonth() + 2, 1));
        setFeedListMonth(new Date(date.getYear(), date.getMonth() + 3, 1));

    }

    private void setFeedListMonth(Date date)
    {
        calendarStateAdapter.feedsList.add(date);
        calendarStateAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Date date = calendarStateAdapter.feedsList.get(24);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(setTitle(date.getMonth(), date.getYear()));
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    String setTitle(int month, int year)
    {
        switch (month)
        {
            case 0: return "Январь " + String.valueOf(year);
            case 1: return "Февраль " + String.valueOf(year);
            case 2: return "Март " + String.valueOf(year);
            case 3: return "Апрель " + String.valueOf(year);
            case 4: return "Май " + String.valueOf(year);
            case 5: return "Июнь " + String.valueOf(year);
            case 6: return "Июль " + String.valueOf(year);
            case 7: return "Август " + String.valueOf(year);
            case 8: return "Сентябрь " + String.valueOf(year);
            case 9: return "Октябрь " + String.valueOf(year);
            case 10: return "Нобярь " + String.valueOf(year);
            case 11: return "Декабрь " + String.valueOf(year);
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}