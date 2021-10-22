package test.next.ui.juxtapose;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import test.next.R;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.databinding.JuxtaposeMonthViewBinding;
import test.next.ui.calendar.CalendarKD;

public class JuxtaposeMonthFragment extends Fragment {

    private JuxtaposeMonthViewBinding binding;
    private LinearLayout[] days;
    private TextView[] nums;
    ArrayList<ArrayList<ScheduleDay>> arrayList;
    ArrayList<String> names;
    public int month, year;
    public JuxtaposeMonthFragment(ArrayList<ArrayList<ScheduleDay>> arrayList, ArrayList<String> names)
    {
        this.arrayList = arrayList;
        this.names =names;
        if(arrayList.size() > 0 && arrayList.get(0).size() > 0) {
            month = arrayList.get(0).get(0).getMonth();
            year = arrayList.get(0).get(0).getYear();
        }
    }

    public static JuxtaposeMonthFragment newInstance(ArrayList<ArrayList<ScheduleDay>> arrayList, ArrayList<String> names) {
        return new JuxtaposeMonthFragment(arrayList, names);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = JuxtaposeMonthViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findDays();

        for(int i = 0; i < 7; i++)
        {
            for(int j = 0; j < arrayList.size(); j++)
            {
                if(arrayList.size() > 0 && arrayList.get(j).size() >= 7) {
                    days[i].addView(getImage());
                    days[i].addView(getTextViewNameSh(names.get(j)));
                    ScheduleDay scheduleDay = arrayList.get(j).get(i);
                    days[i].addView(getTextViewShift(scheduleDay.getShifts().getName(), scheduleDay.getShifts().getColor()));
                    nums[i].setText(String.valueOf(scheduleDay.getDay()));
                }
            }
        }


        return root;
    }

    private void findDays() {
        LinearLayout[] days = new LinearLayout[]{
                binding.juxtaposeDay1, binding.juxtaposeDay2, binding.juxtaposeDay3, binding.juxtaposeDay4,
                binding.juxtaposeDay5, binding.juxtaposeDay6, binding.juxtaposeDay7
        };
        this.days = days;
        TextView[] nums = new TextView[] {
                binding.juxtaposeNumOfMonth1, binding.juxtaposeNumOfMonth2, binding.juxtaposeNumOfMonth3,
                binding.juxtaposeNumOfMonth4, binding.juxtaposeNumOfMonth5,binding.juxtaposeNumOfMonth6,
                binding.juxtaposeNumOfMonth7
        };
        this.nums = nums;
    }

    ImageView getImage()
    {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.schedule_icon));
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = 75;
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    TextView getTextViewNameSh(String name)
    {
        TextView textView = new TextView(getActivity());
        textView.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryVariant));
        LinearLayout.LayoutParams layoutParams_t1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        layoutParams_t1.gravity = Gravity.CENTER;
        layoutParams_t1.setMargins(5,5,5,5);
        textView.setTextSize(12);
        textView.setBackground(getActivity().getResources().getDrawable(R.drawable.textlines_sch));
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams_t1);
        return textView;
    }

    TextView getTextViewShift(String name, String color)
    {
        TextView textView = new TextView(getActivity());
        textView.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryVariant));
        LinearLayout.LayoutParams layoutParams_t1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        layoutParams_t1.gravity = Gravity.CENTER;
        layoutParams_t1.setMargins(5,5,5,5);
        textView.setTextSize(12);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        textView.setText(name);
        textView.setBackgroundColor(Color.parseColor(color));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams_t1);

        return textView;
    }
}
