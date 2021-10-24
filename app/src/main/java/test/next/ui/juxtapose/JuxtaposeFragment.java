package test.next.ui.juxtapose;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.databinding.FragmentJuxtaposeBinding;
import test.next.databinding.FragmentJuxtaposeDialogBinding;
import test.next.ui.calendar.DayCalendarDialogFragment;
import test.next.ui.changes.ChangeAdapter;
import test.next.ui.home.CalendarStateAdapter;

public class JuxtaposeFragment extends Fragment implements JuxtaposeDialogFragment.DialogListener {

    private FragmentJuxtaposeBinding binding;
    private JuxtaposeStateAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJuxtaposeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        AccountConst.ShowAd();
        AccountConst.loadAdView(binding.adView);
        FragmentManager fm = getFragmentManager();
        JuxtaposeDialogFragment juxtaposeDialogFragment = new JuxtaposeDialogFragment();
        juxtaposeDialogFragment.setTargetFragment(JuxtaposeFragment.this, 300);
        juxtaposeDialogFragment.show(fm, "fragment_edit_name");
        binding.juxtaposePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {


                if (position == adapter.monthFragments.size() - 2)
                    adapter.Add();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(setTitle(adapter.monthFragments.get(position).month,
                        adapter.monthFragments.get(position).year));


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
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);
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
    public void onFinishDialog(ArrayList<Schedule> schedules) {
        adapter = new JuxtaposeStateAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        binding.juxtaposePager.setAdapter(adapter);
        adapter.setShifts(schedules);
    }
}
