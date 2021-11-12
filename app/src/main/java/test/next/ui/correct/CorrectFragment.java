package test.next.ui.correct;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ozcanalasalvar.library.utils.DateUtils;
import com.ozcanalasalvar.library.view.datePicker.DatePicker;
import com.ozcanalasalvar.library.view.popup.DatePickerPopup;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.databinding.FragmentCorrectBinding;
import test.next.databinding.FragmentCreateBinding;
import test.next.ui.create.ShiftsAdapter;
import test.next.ui.home.HomeFragment;

public class CorrectFragment extends Fragment {

    private FragmentCorrectBinding binding;
    private boolean exist = false;
    private ArrayList<Shifts> shifts;
    private ShiftsAdapter adapter;
    private ArrayList<Integer> count_shifts = new ArrayList<>(), count_days = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCorrectBinding.inflate(inflater, container, false);
        int start_year, start_month, start_day, end_year, end_month, end_day;
        ArrayList<ScheduleDay> arrayList = HomeFragment.scheduls.get(HomeFragment.current_schedule).getScheduleDayArrayList();

        start_year = arrayList.get(0).getYear();
        start_month = arrayList.get(0).getMonth();
        start_day = arrayList.get(0).getDay();

        end_year = arrayList.get(arrayList.size()-1).getYear();
        end_month = arrayList.get(arrayList.size()-1).getMonth();
        end_day = arrayList.get(arrayList.size()-1).getDay();

        sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getInt("Count_Shifts", 0) == 0) {
            editor.putInt("Count_Shifts", 1);
            editor.putInt("Shift1", -1);
            editor.putInt("ShiftDay1", -1);
            editor.commit();
        }
        AccountConst.ShowAd();

        binding.correctDateStart.setInputType(InputType.TYPE_NULL);
        binding.correctDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] split = binding.correctDateStart.getText().split("/");
                Calendar calendar = Calendar.getInstance();
                if(split[0].equals(""))
                {
                    split = new String[3];
                    split[0] = String.valueOf(calendar.get(Calendar.YEAR));
                    split[1] = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                    split[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                }
                DatePickerPopup datePickerPopup = new DatePickerPopup.Builder()
                        .from(getActivity())
                        .offset(3)
                        .pickerMode(DatePicker.MONTH_ON_FIRST)
                        .textSize(19)
                        .startDate(DateUtils.getTimeMiles(start_year, start_month, 1))
                        .endDate(DateUtils.getTimeMiles(end_year, end_month, end_day))
                        .currentDate(DateUtils.getTimeMiles(Integer.parseInt(split[0]), Integer.parseInt(split[1])-1, Integer.parseInt(split[2])))
                        .listener(new DatePickerPopup.OnDateSelectListener() {
                            @Override
                            public void onDateSelected(DatePicker dp, long date, int day, int month, int year) {
                                int m = month + 1;
                                if(start_year == year && start_month == month && day < start_day) {
                                    Toasty.error(getActivity(), getActivity().getResources().getString(R.string.error_date_correct), Toasty.LENGTH_SHORT).show();
                                    return;
                                }
                                binding.correctDateStart.setText(year + "/" + m + "/" + day);
                            }
                        })
                        .build();
                datePickerPopup.show();
            }
        });

        binding.correctDateEnd.setInputType(InputType.TYPE_NULL);
        binding.correctDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] split = binding.correctDateStart.getText().split("/");
                String[] split2 = null;
                if(split[0].equals(""))
                {
                    Toasty.info(getActivity(), getActivity().getResources().getString(R.string.warn_correct_date2), Toasty.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    split2 = binding.correctDateEnd.getText().split("/");
                    if(split2[0].equals(""))
                    {
                        split2 = new String[3];
                        split2[0] = split[0];
                        split2[1] = split[1];
                        split2[2] = split[2];
                    }
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(end_year, end_month, end_day);
                DatePickerPopup datePickerPopup = new DatePickerPopup.Builder()
                        .from(getActivity())
                        .offset(3)
                        .pickerMode(DatePicker.MONTH_ON_FIRST)
                        .textSize(19)
                        .startDate(DateUtils.getTimeMiles(Integer.parseInt(split[0]), Integer.parseInt(split[1])-1, 1))
                        .endDate(DateUtils.getTimeMiles(end_year, 11, 31))
                        .currentDate(DateUtils.getTimeMiles(Integer.parseInt(split2[0]), Integer.parseInt(split2[1])-1, Integer.parseInt(split2[2])))
                        .listener(new DatePickerPopup.OnDateSelectListener() {
                            @Override
                            public void onDateSelected(DatePicker dp, long date, int day, int month, int year) {
                                int m = month + 1;
                                if(end_year == year && ((end_month < month && day > end_day) || (end_month <= month && day > end_day) )) {
                                    Toasty.error(getActivity(), getActivity().getResources().getString(R.string.error_date_correct), Toasty.LENGTH_SHORT).show();
                                    return;
                                }
                                binding.correctDateEnd.setText(year + "/" + m + "/" + day);
                            }
                        })
                        .build();
                datePickerPopup.show();
            }
        });

        shifts = AccountConst.shiftsArrayList;
        for (int i = 1; i <= sharedPreferences.getInt("Count_Shifts", 0); i++) {
            count_shifts.add(sharedPreferences.getInt("Shift" + String.valueOf(i), -1));
            count_days.add(sharedPreferences.getInt("ShiftDay" + String.valueOf(i), -1));
        }
        adapter = new ShiftsAdapter(shifts, count_shifts, count_days);
        adapter.setActivity(getActivity());
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
                adapter.setCount(adapter.getCount() + 1);
            }
        });
        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() != 1) {
                    adapter.RemoveShift();
                    adapter.RemoveDay();
                    adapter.setCount(adapter.getCount() - 1);
                }
            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HomeFragment.scheduls.size() > 0) {
                    ArrayList<Shifts> shifts_send = new ArrayList<>();
                    ArrayList<Integer> days_send = new ArrayList<>();

                    for (PowerSpinnerView view1 : adapter.getShifts_schedule()) {
                        if (view1.getSelectedIndex() != -1)
                            shifts_send.add(shifts.get(view1.getSelectedIndex()));
                    }

                    for (PowerSpinnerView view1 : adapter.getDays_schedule()) {
                        if (view1.getSelectedIndex() != -1)
                            days_send.add(view1.getSelectedIndex() + 1);
                    }

                    if (shifts_send.size() < 2 || days_send.size() < 2) {
                        Toasty.warning(getActivity(), getActivity().getResources().getString(R.string.info_shifts), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (binding.correctDateStart.getText().equals("") || binding.correctDateEnd.getText().equals("")) {
                        Toasty.warning(getActivity(), getActivity().getResources().getString(R.string.info_name_scheduke), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    editor.putInt("Count_Shifts", adapter.getSelected_shift().size());
                    for (int i = 1; i <= adapter.getSelected_shift().size(); i++) {
                        editor.putInt("Shift" + String.valueOf(i), adapter.getSelected_shift().get(i - 1));
                        editor.putInt("ShiftDay" + String.valueOf(i), adapter.getSelected_day().get(i - 1));
                        editor.commit();
                    }

                    editor.commit();

                    String[] split_start = binding.correctDateStart.getText().split("/");
                    Calendar calendar_start = Calendar.getInstance();
                    calendar_start.set(Calendar.YEAR, Integer.parseInt(split_start[0]));
                    calendar_start.set(Calendar.MONTH, Integer.parseInt(split_start[1]) - 1);
                    calendar_start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split_start[2]));

                    String[] split_end = binding.correctDateEnd.getText().split("/");
                    Calendar calendar_end = Calendar.getInstance();
                    calendar_end.set(Calendar.YEAR, Integer.parseInt(split_end[0]));
                    calendar_end.set(Calendar.MONTH, Integer.parseInt(split_end[1]) - 1);
                    calendar_end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split_end[2]));


                    ArrayList<ScheduleDay> scheduleDayArrayList = HomeFragment.scheduls.get(HomeFragment.current_schedule).getScheduleDayArrayList();
                    ArrayList<ScheduleDay> tmp = AccountConst.newSchedule(shifts_send, days_send, calendar_start, calendar_end);

                    for (int i1 = 0, i2 = 0; i1 < scheduleDayArrayList.size(); i1++) {
                        ScheduleDay scheduleDay = scheduleDayArrayList.get(i1);
                        if (scheduleDay.getDay() == tmp.get(i2).getDay()
                                && scheduleDay.getMonth() == tmp.get(i2).getMonth()
                                && scheduleDay.getYear() == tmp.get(i2).getYear()) {
                            scheduleDayArrayList.get(i1).setShift(tmp.get(i2).getShift());
                            i2++;
                        }

                        if (i2 == tmp.size())
                            break;
                    }

                    HomeFragment.scheduls.get(HomeFragment.current_schedule).setScheduleDayArrayList(scheduleDayArrayList);


                    byte[] data = SerializationUtils.serialize(HomeFragment.scheduls.get(HomeFragment.current_schedule));
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                    ScheduleFB scheduleFB_new = new ScheduleFB(String.valueOf(HomeFragment.scheduls.get(HomeFragment.current_schedule).getId()), base64);


                    Task<DataSnapshot> dataSnapshotTask = FirebaseDatabase
                            .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                            .getReference()
                            .child("Users/" + AccountConst.account.getUid() + "/Scheduls").get();
                    dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            try {

                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                    ScheduleFB scheduleFB = dataSnapshot.getValue(ScheduleFB.class);
                                    if (scheduleFB.getId().equals(scheduleFB_new.getId())) {
                                        dataSnapshot.getRef().setValue(scheduleFB_new);
                                    }
                                    Toasty.success(getActivity(), getActivity().getResources().getString(R.string.correct_success), Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                                    navController.navigate(R.id.nav_home);
                                }
                            } catch (Exception ex)
                            {
                                Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            @MainThread
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);

        return binding.getRoot();
    }
}