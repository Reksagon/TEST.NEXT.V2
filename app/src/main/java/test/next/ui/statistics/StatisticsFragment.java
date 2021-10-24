package test.next.ui.statistics;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.Shifts;
import test.next.databinding.FragmentStatisticsBinding;
import test.next.databinding.StatMonthViewBinding;
import test.next.ui.home.HomeFragment;
import test.next.ui.juxtapose.JuxtaposeStateAdapter;
import test.next.ui.shifts.ShiftsSettingsAdapter;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel mViewModel;
    private FragmentStatisticsBinding binding;
    public static ArrayList<Shifts> shifts;
    StatisticsStateAdapter adapter;
    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        AccountConst.ShowAd();
        // binding.statShiftContent;
        AccountConst.loadAdView(binding.adView3);
        if(HomeFragment.current_schedule >= 0) {
            binding.scheduleStat.setText(HomeFragment.scheduls.get(HomeFragment.current_schedule).getName());
            Task<DataSnapshot> getShiftsTask = FirebaseDatabase
                    .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                    .getReference()
                    .child("Users/" + AccountConst.account.getId() + "/Shifts").get();
            getShiftsTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    shifts = new ArrayList<>();
                    for (DataSnapshot child : task.getResult().getChildren()) {
                        Shifts str1 = child.getValue(Shifts.class);
                        shifts.add(str1);
                    }

                    adapter = new StatisticsStateAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
                    binding.statContent.setAdapter(adapter);

                    Calendar calendar_start = Calendar.getInstance();
                    Calendar calendar_end = Calendar.getInstance();

                    int current_month = calendar_end.get(Calendar.MONTH);
                    int currnet_year = calendar_end.get(Calendar.YEAR);


                    Schedule schedule = HomeFragment.scheduls.get(HomeFragment.current_schedule);
                    int start_month = schedule.getScheduleDayArrayList().get(0).getMonth();
                    int start_year = schedule.getScheduleDayArrayList().get(0).getYear();

                    int end_month = schedule.getScheduleDayArrayList().get(schedule.getScheduleDayArrayList().size() - 1).getMonth();
                    int end_year = schedule.getScheduleDayArrayList().get(schedule.getScheduleDayArrayList().size() - 1).getYear();

                    calendar_start.set(Calendar.MONTH, start_month);
                    calendar_start.set(Calendar.YEAR, start_year);

                    calendar_end.set(Calendar.MONTH, end_month);
                    calendar_end.set(Calendar.YEAR, end_year);

                    int i = 0, j = 0;
                    while (calendar_start.before(calendar_end)) {
                        adapter.Add(calendar_start.get(Calendar.MONTH), calendar_start.get(Calendar.YEAR));
                        if (calendar_start.get(Calendar.MONTH) == current_month
                                && calendar_start.get(Calendar.YEAR) == currnet_year) {
                            i = j;
                        }
                        calendar_start.set(Calendar.MONTH, calendar_start.get(Calendar.MONTH) + 1);
                        j++;
                    }
                    binding.statContent.setCurrentItem(i, false);

                }

            });
        }
        else
        {
            Toasty.info(getActivity(), getActivity().getResources().getString(R.string.warning_info), Toasty.LENGTH_SHORT).show();
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });

        binding.statContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                StatMonthViewFragment fragment = adapter.fragments.get(position);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(setTitle(fragment.month, fragment.year));


            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        return root;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);
    }
}