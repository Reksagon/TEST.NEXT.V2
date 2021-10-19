package test.next.ui.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import test.next.R;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.databinding.CalendarFragmentBinding;
import test.next.ui.home.HomeFragment;

public class CalendarKD extends Fragment {
    private CalendarViewModel mViewModel;
    CalendarFragmentBinding binding;
    TextView[] days, shifts;
    Calendar calendar;
    public int m, y;
    ArrayList<ScheduleDay> dayArrayList = null;

    public static CalendarKD newInstance(Date date, ArrayList<ScheduleDay> dayArrayList) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, date.getMonth());
        calendar.set(Calendar.YEAR, date.getYear());
        return new CalendarKD(calendar, dayArrayList);
    }

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
    public CalendarKD(Calendar calendar, ArrayList<ScheduleDay> dayArrayList)
    {
        this.calendar = calendar;
        this.dayArrayList = dayArrayList;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding = CalendarFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findDays();
        findShifts();
        try {
            setDays();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    void findShifts()
    {
        TextView[] shifts = {binding.dayShift1, binding.dayShift2, binding.dayShift3, binding.dayShift4,
                binding.dayShift5, binding.dayShift6, binding.dayShift7, binding.dayShift8, binding.dayShift9,
                binding.dayShift10, binding.dayShift11, binding.dayShift12, binding.dayShift13, binding.dayShift14,
                binding.dayShift15, binding.dayShift16, binding.dayShift17, binding.dayShift18, binding.dayShift19,
                binding.dayShift20, binding.dayShift21, binding.dayShift22, binding.dayShift23, binding.dayShift24,
                binding.dayShift25, binding.dayShift26, binding.dayShift27, binding.dayShift28, binding.dayShift29,
                binding.dayShift30, binding.dayShift31, binding.dayShift32, binding.dayShift33,binding.dayShift34,
                binding.dayShift35, binding.dayShift36, binding.dayShift37, binding.dayShift38, binding.dayShift39,
                binding.dayShift40, binding.dayShift41, binding.dayShift42 };
        this.shifts = shifts;
    }
    public void setDays() throws InterruptedException {
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

    void setCalendar(int day, int day_of_month) throws InterruptedException {
        int i = day;

        Calendar tmp = Calendar.getInstance();

        for(int dd = 1, sh = 0; dd <= day_of_month; i++, dd++)
        {
            days[i-1].setText(String.valueOf(dd));
            if(dd == tmp.get(Calendar.DAY_OF_MONTH)
                    && tmp.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                    && tmp.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
            {
                days[i-1].setBackgroundResource(R.drawable.textview_desidgn);
                days[i-1].setTextColor(Color.WHITE);
            }
            if(dayArrayList != null && sh < dayArrayList.size())
            {
                if(dayArrayList.get(sh).getDay() == dd) {
                    shifts[i - 1].setVisibility(View.VISIBLE);
                    shifts[i - 1].setText(dayArrayList.get(sh).getShift().getName());
                    shifts[i-1].setBackgroundColor(Color.parseColor(dayArrayList.get(sh).getShift().getColor()));
                    sh++;
                }
            }
        }

        Calendar calendar_tmp = calendar;
        calendar_tmp.set(Calendar.MONTH, calendar_tmp.get(Calendar.MONTH) + 1);
        ArrayList<ScheduleDay> schedule_plus = null;
        if(HomeFragment.scheduls.size() > 0) {
            schedule_plus = HomeFragment.scheduls.get(0).getMonth(calendar_tmp.get(Calendar.MONTH), calendar_tmp.get(Calendar.YEAR));
        }

        calendar_tmp.set(Calendar.MONTH, calendar_tmp.get(Calendar.MONTH) - 2);
        for(int j = i, dd = 1; j < days.length+1; j++, dd++)
        {
            days[j-1].setText(String.valueOf(dd));
            days[j-1].setAlpha(0.3f);

            if(schedule_plus != null  && dd <= schedule_plus.size()) {
                if (schedule_plus.get(dd-1).getDay() == dd) {
                    shifts[j - 1].setVisibility(View.VISIBLE);
                    shifts[j - 1].setText(schedule_plus.get(dd-1).getShift().getName());
                    shifts[j - 1].setBackgroundColor(Color.parseColor(schedule_plus.get(dd-1).getShift().getColor()));
                    shifts[j - 1].setAlpha(0.3f);
                }
            }
        }

        ArrayList<ScheduleDay> schedule_minus = null;
        if(HomeFragment.scheduls.size() > 0) {
            schedule_minus = HomeFragment.scheduls.get(0).getMonth(calendar_tmp.get(Calendar.MONTH), calendar_tmp.get(Calendar.YEAR));
        }

        for(int ii = day-2, prev_month_day = calendar_tmp.getActualMaximum(Calendar.DAY_OF_MONTH); ii >= 0; ii--, prev_month_day--)
        {
            days[ii].setText(String.valueOf(prev_month_day));
            days[ii].setAlpha(0.5f);

            if(schedule_minus != null && prev_month_day <= schedule_minus.size()) {
                if (schedule_minus.get(prev_month_day-1).getDay() == prev_month_day) {
                    shifts[ii].setVisibility(View.VISIBLE);
                    shifts[ii].setText(schedule_minus.get(prev_month_day-1).getShift().getName());
                    shifts[ii].setBackgroundColor(Color.parseColor(schedule_minus.get(prev_month_day-1).getShift().getColor()));
                    shifts[ii].setAlpha(0.3f);
                }
            }
        }

    }
}
