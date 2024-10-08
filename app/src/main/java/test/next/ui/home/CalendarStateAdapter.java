package test.next.ui.home;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import test.next.constant.ScheduleDay;
import test.next.constant.Shifts;
import test.next.ui.calendar.CalendarKD;
import org.apache.commons.codec.binary.Base64;

public class CalendarStateAdapter extends FragmentStateAdapter {
    public List<Date> feedsList;
    private ArrayList<Shifts> shifts;

    public void setShifts(ArrayList<Shifts> shifts) {
        this.shifts = shifts;
    }

    public CalendarStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CalendarStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CalendarStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    public void AddEnd(Date date)
    {
        feedsList.add(date);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 100);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(HomeFragment.scheduls.size() > 0)
        {

            ArrayList<ScheduleDay> days = HomeFragment.scheduls.get(HomeFragment.current_schedule).getMonth(feedsList.get(position).getMonth(), feedsList.get(position).getYear());
            return CalendarKD.newInstance(feedsList.get(position),days, shifts);
        }
        return CalendarKD.newInstance(feedsList.get(position));
    }


    @Override
    public int getItemCount() {
        return feedsList.size();
    }
}
