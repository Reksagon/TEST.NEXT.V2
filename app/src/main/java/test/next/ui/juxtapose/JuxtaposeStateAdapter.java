package test.next.ui.juxtapose;

import android.annotation.SuppressLint;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.constant.Shifts;
import test.next.ui.calendar.CalendarKD;
import test.next.ui.home.HomeFragment;

public class JuxtaposeStateAdapter extends FragmentStateAdapter {
    private ArrayList<Schedule> arrayList;
    private ArrayList<String> strings;
    public ArrayList<JuxtaposeMonthFragment> monthFragments;
    Calendar calendar = Calendar.getInstance();

    public void setShifts(ArrayList<Schedule> arrayList) {
        this.arrayList = arrayList;

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        switch(calendar.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
                day -= 6;
                break;
            case 3: day -= 1;
                break;
            case 4:day -= 2;
                break;
            case 5: day -= 3;
                break;
            case 6: day -= 4;
                break;
            case 7: day -= 5;
                break;

        }

        ArrayList<ArrayList<ScheduleDay>> arrayListArrayList = new ArrayList<>();
        strings = new ArrayList<>();
        for(Schedule schedule : arrayList)
        {
            int day_day = day;
            ArrayList<ScheduleDay> scheduleDays = new ArrayList<>();
            for(ScheduleDay scheduleDay : schedule.getMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)))
            {
                if(scheduleDay.getDay() == day_day) {
                    scheduleDays.add(scheduleDay);
                    day_day++;
                }
                if(day_day == day+7)
                    break;
            }
            arrayListArrayList.add(scheduleDays);
            strings.add(schedule.getName());
        }

        monthFragments.add(JuxtaposeMonthFragment.newInstance(arrayListArrayList, strings));

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
        for(int i = 0;  i < 10; i++)
        {
            addFragment(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
        }

    }

    public void Add()
    {
        addFragment(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 100);
    }

    private void addFragment(int day_start, int month, int year)
    {
        ArrayList<ArrayList<ScheduleDay>> arrayListArrayList = new ArrayList<>();
        for(Schedule schedule : arrayList)
        {
            int day_day = day_start;
            ArrayList<ScheduleDay> scheduleDays = new ArrayList<>();
            for(ScheduleDay scheduleDay : schedule.getMonth(month, year))
            {
                if(scheduleDay.getDay() == day_day) {
                    scheduleDays.add(scheduleDay);
                    day_day++;
                }
                if(day_day == day_start+7)
                    break;
            }

            if(scheduleDays.size() < 7)
            {
                int mon = month, ye = year;
                if(month == 11)
                {
                    mon = 0;
                    ye++;
                }
                else
                    mon++;
                for(ScheduleDay scheduleDay : schedule.getMonth(mon, ye)) {

                    scheduleDays.add(scheduleDay);

                    if (scheduleDays.size() >= 7)
                        break;
                }
            }


            arrayListArrayList.add(scheduleDays);
        }

        if(arrayListArrayList.get(0).size() == 0)
            return;
        monthFragments.add(JuxtaposeMonthFragment.newInstance(arrayListArrayList, strings));
    }

    public JuxtaposeStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public JuxtaposeStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public JuxtaposeStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        monthFragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return monthFragments.get(position);
    }


    @Override
    public int getItemCount() {
        return monthFragments.size();
    }
}
