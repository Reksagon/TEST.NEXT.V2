package test.next.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import test.next.MainActivity;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CalendarStateAdapter calendarStateAdapter;
    public static ArrayList<Schedule> scheduls = new ArrayList<>();
    public static int current_schedule = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        String data = null;
        byte[] data2 = null;
        if(AccountConst.offline)
        {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
            data = sharedPreferences.getString("Schedule", null);
            data2 = Base64.decode(data, Base64.DEFAULT);
            Schedule schedule = SerializationUtils.deserialize(data2);
            HomeFragment.scheduls.add(schedule);
            HomeFragment.current_schedule = 0;
            AccountConst.board = sharedPreferences.getBoolean("Board", true);
            AccountConst.days_other = sharedPreferences.getBoolean("DaysOther", true);
            AccountConst.text_color_calendar = sharedPreferences.getString("TextColorCalendar", "#FF000000");
            AccountConst.text_color_shift = sharedPreferences.getString("TextColorShift", "#FFFFFFFF");
            AccountConst.color_Border = sharedPreferences.getString("ColorBorder", "#FFDDDDDD");
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        calendarStateAdapter = new CalendarStateAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        calendarStateAdapter.feedsList = new ArrayList<>();
        calendarStateAdapter.setShifts(AccountConst.shiftsArrayList);
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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });

        if(MainActivity.binding.appBarMain.toolbar.getVisibility() == View.GONE)
            MainActivity.binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
        if(AccountConst.background != null)
            binding.backgroundHome.setImageBitmap(AccountConst.background);

        AccountConst.loadAdView(binding.adView);
        AccountConst.ShowAd();

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
        for(int i = 1; i <= 24; i++)
        {
            setFeedListMonth(new Date(date.getYear(), date.getMonth() + i, 1));
        }

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
        if(!AccountConst.offline)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);
        else
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    String setTitle(int month, int year)
    {
        switch (month)
        {
            case 0: return getActivity().getResources().getString(R.string.jan) + " " + String.valueOf(year);
            case 1: return getActivity().getResources().getString(R.string.feb) + " " + String.valueOf(year);
            case 2: return getActivity().getResources().getString(R.string.march) + " " + String.valueOf(year);
            case 3: return getActivity().getResources().getString(R.string.april) + " " + String.valueOf(year);
            case 4: return getActivity().getResources().getString(R.string.may) + " " + String.valueOf(year);
            case 5: return getActivity().getResources().getString(R.string.june) + " " + String.valueOf(year);
            case 6: return getActivity().getResources().getString(R.string.july) + " " + String.valueOf(year);
            case 7: return getActivity().getResources().getString(R.string.august) + " " + String.valueOf(year);
            case 8: return getActivity().getResources().getString(R.string.sep) + " " + String.valueOf(year);
            case 9: return getActivity().getResources().getString(R.string.oct) + " " + String.valueOf(year);
            case 10: return getActivity().getResources().getString(R.string.nov) + " " + String.valueOf(year);
            case 11: return getActivity().getResources().getString(R.string.dec) + " " + String.valueOf(year);
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}