package test.next.ui.changes;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.databinding.FragmentChangeBinding;
import test.next.databinding.FragmentShiftsBinding;
import test.next.ui.home.HomeFragment;
import test.next.ui.shifts.ShiftsViewModel;

public class ChangeFragment extends Fragment {

    private ChangeViewModel mViewModel;
    private FragmentChangeBinding binding;
    private ChangeAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                new ViewModelProvider(this).get(ChangeViewModel.class);

        binding = FragmentChangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        AccountConst.ShowAd();
        AccountConst.loadAdView(binding.adView6);
        if(HomeFragment.scheduls.size() == 0)
            Toasty.info(getActivity(), getActivity().getResources().getString(R.string.warning_info), Toasty.LENGTH_SHORT).show();
        ArrayList<String> strings = new ArrayList<>();
        for(Schedule schedule : HomeFragment.scheduls)
        {
            strings.add(schedule.getName());
        }

        adapter = new ChangeAdapter(getActivity(), strings, true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        binding.changeContent.setLayoutManager(linearLayout);
        binding.changeContent.setAdapter(adapter);

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

}