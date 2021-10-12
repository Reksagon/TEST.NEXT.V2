package test.next.ui.home;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Date;
import java.util.List;

import test.next.ui.calendar.CalendarKD;

public class CalendarStateAdapter extends FragmentStateAdapter {
    public List<Date> feedsList;

    public CalendarStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CalendarStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public CalendarStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void AddBegin(Date date)
    {
        feedsList.add(0, date);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 100);
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
        return CalendarKD.newInstance(feedsList.get(position));
    }


    @Override
    public int getItemCount() {
        return feedsList.size();
    }
}
