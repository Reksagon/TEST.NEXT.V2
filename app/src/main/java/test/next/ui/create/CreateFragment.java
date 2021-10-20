package test.next.ui.create;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.databinding.FragmentCreateBinding;
import test.next.ui.home.HomeFragment;

public class CreateFragment extends Fragment {

    private CreateViewModel createViewModel;
    private FragmentCreateBinding binding;
    private boolean exist = false;
    private ArrayList<Shifts> shifts;
    private ShiftsAdapter adapter;
    private ArrayList<Integer> count_shifts = new ArrayList<>(), count_days = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel =
                new ViewModelProvider(this).get(CreateViewModel.class);
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        createViewModel.setActivity(getActivity(), binding);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getInt("Count_Shifts", 0) == 0) {
            editor.putInt("Count_Shifts", 1);
            editor.putInt("Shift1", -1);
            editor.putInt("ShiftDay1", -1);
            editor.commit();
        }

        binding.date.setClickable(false);
        binding.date.setEnabled(false);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        String formatted = format1.format(cal.getTime());
        binding.date.setText(formatted);

        createViewModel.CreateView();
        startSet();

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);

        createViewModel.getmDateClick().observe(getViewLifecycleOwner(), new Observer<View.OnClickListener>() {
            @Override
            public void onChanged(View.OnClickListener onClickListener) {
                binding.date.setOnClickListener(onClickListener);
            }
        });


        Task<DataSnapshot> dataSnapshotTask =  FirebaseDatabase
                .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                .getReference()
                .child("Users/").get();
        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot child : task.getResult().getChildren()) {
                    if (child.getKey().toString().equals(AccountConst.account.getId()))
                        exist = true;
                }
                if (exist) {
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
                            for(int i = 1; i <= sharedPreferences.getInt("Count_Shifts", 0); i++)
                            {
                                count_shifts.add(sharedPreferences.getInt("Shift" + String.valueOf(i), -1));
                                count_days.add(sharedPreferences.getInt("ShiftDay" + String.valueOf(i), -1));
                            }
                            if(!sharedPreferences.getString("ShiftCalendar", "none").equals("none"))
                            {
                                binding.date.setText(sharedPreferences.getString("ShiftCalendar", "none"));
                            }
                            adapter = new ShiftsAdapter(shifts, count_shifts, count_days);
                            adapter.setCount(sharedPreferences.getInt("Count_Shifts", 0));
                            LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
                            linearLayout.setOrientation(RecyclerView.VERTICAL);
                            binding.shiftsView.setLayoutManager(linearLayout);
                            binding.shiftsView.setAdapter(adapter);

                            binding.plus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter.AddShift(-1);
                                    adapter.AddDay(-1);
                                    adapter.setCount(adapter.getCount()+1);
                                }
                            });

                            binding.minus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(adapter.getCount() != 1) {
                                        adapter.RemoveShift();
                                        adapter.RemoveDay();
                                        adapter.setCount(adapter.getCount() - 1);
                                    }
                                }
                            });
                        }
                    });

                }

            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Shifts> shifts_send = new ArrayList<>();
                ArrayList<Integer> days_send = new ArrayList<>();

                for(PowerSpinnerView view1 : adapter.getShifts_schedule())
                {
                    if(view1.getSelectedIndex() != -1)
                        shifts_send.add(shifts.get(view1.getSelectedIndex()));
                }

                for(PowerSpinnerView view1 : adapter.getDays_schedule())
                {
                    if(view1.getSelectedIndex() != -1)
                        days_send.add(view1.getSelectedIndex() + 1);
                }

                if(shifts_send.size() < 2 || days_send.size() < 2)
                {
                    Toasty.warning(getActivity(), getActivity().getResources().getString(R.string.info_shifts), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.nameSchedule.getText().equals("") || binding.nameSchedule.getText() == null)
                {
                    Toasty.warning(getActivity(), getActivity().getResources().getString(R.string.info_name_scheduke), Toast.LENGTH_SHORT).show();
                    return;
                }

                editor.putInt("Count_Shifts", adapter.selected_shift.size());
                for(int i = 1; i <= adapter.selected_shift.size(); i++)
                {
                    editor.putInt("Shift" + String.valueOf(i), adapter.selected_shift.get(i-1));
                    editor.putInt("ShiftDay" + String.valueOf(i), adapter.selected_day.get(i-1));
                    editor.commit();
                }

                editor.putString("ShiftCalendar", binding.date.getText());
                editor.commit();

                String[] split = binding.date.getText().split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(split[1])-1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[2]));

                Schedule schedule = new Schedule(HomeFragment.scheduls.size() + 1, shifts_send, days_send,calendar, binding.nameSchedule.getText());
                HomeFragment.scheduls.add(schedule);
                HomeFragment.current_schedule = HomeFragment.scheduls.size() - 1;

                byte[] data = SerializationUtils.serialize(schedule);
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                ScheduleFB scheduleFB = new ScheduleFB(String.valueOf(HomeFragment.scheduls.get(HomeFragment.current_schedule).getId()), base64);

                DatabaseReference databaseReference = FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Scheduls");
                databaseReference.push()
                        .setValue(scheduleFB);

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Settings/CurrentScheduls").setValue(HomeFragment.scheduls.size()-1);

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
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

    private void startSet() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}