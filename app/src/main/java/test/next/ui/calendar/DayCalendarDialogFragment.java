package test.next.ui.calendar;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.ScheduleDay;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.databinding.FragmentDayCalendarDialogBinding;
import test.next.ui.home.HomeFragment;


public class DayCalendarDialogFragment extends DialogFragment {

    private FragmentDayCalendarDialogBinding binding;
    private ScheduleDay scheduleDay;
    private ArrayList<Shifts> shifts;
    private Shifts shift_current;

    public DayCalendarDialogFragment(ScheduleDay scheduleDay, ArrayList<Shifts> shifts)
    {
        this.scheduleDay = scheduleDay;
        this.shifts = shifts;
    }
    public static DayCalendarDialogFragment newInstance(ScheduleDay scheduleDay, ArrayList<Shifts> shifts) {
        DayCalendarDialogFragment fragment = new DayCalendarDialogFragment(scheduleDay, shifts);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayCalendarDialogBinding.inflate(inflater, container, false);

        binding.date.setText(scheduleDay.getYear()+"/" + scheduleDay.getMonth() + "/" + scheduleDay.getDay());
        List<String> strings = new ArrayList<>();
        for(Shifts shift : shifts)
            strings.add(shift.getName());
        binding.spinner3.setItems(strings);

        binding.spinner3.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                shift_current = shifts.get(i1);
                if(shifts.get(i1).isOffday())
                {
                    binding.startLiner.setVisibility(View.GONE);
                    binding.endLiner.setVisibility(View.GONE);
                }
                else
                {
                    binding.startLiner.setVisibility(View.VISIBLE);
                    binding.endLiner.setVisibility(View.VISIBLE);
                }

                binding.shiftStart.setText(shift_current.getStart());
                binding.shiftEnd.setText(shift_current.getEnd());
                binding.shiftStartLanch.setText(shift_current.getStart_lanch());
                binding.shiftEndLanch.setText(shift_current.getEnd_lanch());
                binding.date.setClickable(false);
                binding.date.setEnabled(false);

            }
        });


        binding.spinner3.selectItemByIndex(scheduleDay.getShift().getId()-1);

        binding.shiftStart.setText(scheduleDay.getShift().getStart());
        binding.shiftEnd.setText(scheduleDay.getShift().getEnd());
        binding.shiftStartLanch.setText(scheduleDay.getShift().getStart_lanch());
        binding.shiftEndLanch.setText(scheduleDay.getShift().getEnd_lanch());

        if(scheduleDay.getShift().isOffday())
        {
            binding.startLiner.setVisibility(View.GONE);
            binding.endLiner.setVisibility(View.GONE);
        }

        binding.buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shift_current.setStart(binding.shiftStart.getText());
                shift_current.setEnd(binding.shiftEnd.getText());
                shift_current.setStart_lanch(binding.shiftStartLanch.getText());
                shift_current.setEnd_lanch(binding.shiftEndLanch.getText());
                for(ScheduleDay item : HomeFragment.scheduls.get(HomeFragment.current_schedule).getScheduleDayArrayList())
                {
                    if(scheduleDay.getDay() == item.getDay()
                            && scheduleDay.getMonth() == item.getMonth()
                                && scheduleDay.getYear() == item.getYear())
                    {
                        item.setShift(shift_current);
                        break;
                    }
                }
                Toasty.success(getActivity(), "Success", Toasty.LENGTH_SHORT).show();

                byte[] data = SerializationUtils.serialize(HomeFragment.scheduls.get(HomeFragment.current_schedule));
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                ScheduleFB scheduleFB = new ScheduleFB(String.valueOf(HomeFragment.scheduls.get(HomeFragment.current_schedule).getId()), base64);

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Scheduls").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for(DataSnapshot item : task.getResult().getChildren())
                        {
                            ScheduleFB tmp = item.getValue(ScheduleFB.class);
                            if(tmp.getId().equals(scheduleFB.getId()))
                                item.getRef().setValue(scheduleFB);
                        }

                    }
                });
                dismiss();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}