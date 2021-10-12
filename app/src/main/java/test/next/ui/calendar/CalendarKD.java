package test.next.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;

import test.next.databinding.CalendarFragmentBinding;

public class CalendarKD extends Fragment {
    private CalendarViewModel mViewModel;
    CalendarFragmentBinding binding;
    TextView[] days;
    Calendar calendar;
    public int m, y;

    public static CalendarKD newInstance(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.YEAR, date.getYear());
        return new CalendarKD(calendar);
    }

    public static CalendarKD newInstance(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.add(Calendar.MONTH, month);
        return new CalendarKD(calendar);
    }

    public CalendarKD(Calendar calendar)
    {
        this.calendar = calendar;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding = CalendarFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findDays();
        setDays();
        m = calendar.get(Calendar.MONTH);
        y = calendar.get(Calendar.YEAR);
        return root;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    void findDays()
    {
        TextView[] days = {binding.day1Text, binding.day2Text, binding.day3Text, binding.day4Text,
                binding.day5Text, binding.day6Text, binding.day7Text, binding.day8Text, binding.day9Text,
                binding.day10Text, binding.day11Text, binding.day12Text, binding.day13Text, binding.day14Text,
                binding.day15Text, binding.day16Text, binding.day17Text, binding.day18Text, binding.day19Text,
                binding.day20Text, binding.day21Text, binding.day22Text, binding.day23Text, binding.day24Text,
                binding.day25Text, binding.day26Text, binding.day27Text, binding.day28Text, binding.day29Text,
                binding.day30Text, binding.day31Text, binding.day32Text, binding.day33Text,binding.day34Text,
                binding.day35Text, binding.day36Text, binding.day37Text, binding.day38Text, binding.day39Text,
                binding.day40Text, binding.day41Text, binding.day42Text };
        this.days = days;
    }
    public void setDays()
    {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        switch(calendar.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
                setCalendar(7 , calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 2:
                setCalendar(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 3: setCalendar(2, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 4:setCalendar(3, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 5: setCalendar(4, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 6: setCalendar(5, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 7: setCalendar(6, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;

        }

    }

    void setCalendar(int day, int day_of_month)
    {
        int i = day;

        for(int dd = 1; dd <= day_of_month; i++, dd++)
        {
            days[i-1].setText(String.valueOf(dd));
        }
        for(int j = i, dd = 1; j < days.length+1; j++, dd++)
        {
            days[j-1].setText(String.valueOf(dd));
            days[j-1].setAlpha(0.5f);
        }

        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);

        for(int ii = day-2, prev_month_day = temp.getActualMaximum(Calendar.DAY_OF_MONTH); ii >= 0; ii--, prev_month_day--)
        {
            days[ii].setText(String.valueOf(prev_month_day));
            days[ii].setAlpha(0.5f);
        }

    }
}
