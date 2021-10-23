package test.next.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import test.next.R;
import test.next.constant.ScheduleDay;
import test.next.databinding.FragmentShiftsBinding;
import test.next.databinding.StatMonthViewBinding;
import test.next.ui.create.ShiftsAdapter;
import test.next.ui.home.HomeFragment;
import test.next.ui.shifts.ShiftsViewModel;

public class StatMonthViewFragment extends Fragment {

    StatMonthViewBinding binding;
    StatisticsRecAdapter adapter;
    ArrayList<ScheduleDay> days;
    int month, year;

    public StatMonthViewFragment(int month, int year)
    {
        days = HomeFragment.scheduls.get(HomeFragment.current_schedule).getMonth(month, year);
        this.month = month;
        this.year = year;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = StatMonthViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.textShift.setText("Смена");
        binding.textCountDay.setText("Дней");
        binding.textCountHour.setText("Часов");
        binding.textFinalDay.setText("12");
        binding.textFinalHour.setText("144");

        binding.textShift.setEnabled(false);
        binding.textCountDay.setEnabled(false);
        binding.textCountHour.setEnabled(false);
        binding.textFinalDay.setEnabled(false);
        binding.textFinalHour.setEnabled(false);

        try {
            adapter = new StatisticsRecAdapter(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        binding.statShiftContent.setLayoutManager(linearLayout);
        binding.statShiftContent.setAdapter(adapter);

        int count_days = 0;
        for(StatisticsRecAdapter.Maps maps : adapter.shifts)
        {
            if(!maps.name.isOffday())
                count_days += maps.shifts.size();
        }

        int sum = 0;
        for(StatisticsRecAdapter.Maps num : adapter.shifts) {
            try {
                sum += num.getHours();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        binding.textFinalHour.setText(String.valueOf(sum));
        binding.textFinalDay.setText(String.valueOf(count_days));

        return root;
    }
}
