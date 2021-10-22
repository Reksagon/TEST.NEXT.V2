package test.next.ui.juxtapose;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import java.util.ArrayList;
import java.util.Calendar;

import test.next.R;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;

public class JuxtaposeMonthRecyclerAdapter extends RecyclerView.Adapter<JuxtaposeMonthRecyclerAdapter.JuxtaposeMonthViewHolder> {

    ArrayList<Day> days;
    Activity activity;
    Calendar calendar;
    ArrayList<ArrayList<ScheduleDay>> schedules_days;
    ArrayList<String> schedules_name;

    public JuxtaposeMonthRecyclerAdapter(Activity activity, Calendar calendar, ArrayList<Schedule> schedules)
    {
        schedules_days  = new ArrayList<>();
        schedules_name = new ArrayList<>();
        this.activity = activity;
        days = new ArrayList<>();
        this.calendar = calendar;
        for(Schedule schedule : schedules)
        {
            schedules_days.add(schedule.getMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)));
            schedules_name.add(schedule.getName());
        }
        for(int i = 0; i < schedules_days.size(); i++)
        {
            if(schedules_days.get(i).size() < calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            {
                while (schedules_days.get(i).size() < calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                {
                    schedules_days.get(i).add(0, null);
                }
            }
        }

        for(int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
        {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            switch (calendar.get(Calendar.DAY_OF_WEEK))
            {
                case 1:
                    days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.sunday)));
                    break;
                case 2:
                    days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.monday)));
                    break;
                case 3: days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.tuesday)));
                    break;
                case 4:days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.wednesday)));
                    break;
                case 5: days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.thurdsday)));
                    break;
                case 6: days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.friday)));
                    break;
                case 7: days.add(new Day(String.valueOf(i), activity.getResources().getString(R.string.saturday)));
                    break;
            }
        }
    }

    @NonNull
    @Override
    public JuxtaposeMonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.juxtapose_month_view, parent, false);
        return new JuxtaposeMonthRecyclerAdapter.JuxtaposeMonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JuxtaposeMonthViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Calendar tmp = Calendar.getInstance();
        if(calendar.get(Calendar.MONTH) == tmp.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)) {
            if (days.get(position).getDay_of_month().equals(String.valueOf(tmp.get(Calendar.DAY_OF_MONTH)))) {
                holder.selectDayBind(days.get(position));
            }
            else holder.bind(days.get(position));
        }
        else
            holder.bind(days.get(position));


        ArrayList<ScheduleDay> scheduleDays = new ArrayList<>();
        for(int i = 0; i <schedules_days.size(); i++)
        {
            if(schedules_days.get(i).get(position) != null) {
                ScheduleDay scheduleDay = schedules_days.get(i).get(position);
                scheduleDays.add(scheduleDay);
            }
        }
        if(scheduleDays.size() > 0)
            holder.scheduleDayBind(scheduleDays);


    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class JuxtaposeMonthViewHolder extends RecyclerView.ViewHolder {

        TextView day, num;
        FitButton[] scheduls;
        TextView[] shifts;

        public JuxtaposeMonthViewHolder(@NonNull View itemView) {
            super(itemView);
//            day = itemView.findViewById(R.id.juxtapose_day_of_week);
//            num = itemView.findViewById(R.id.juxtapose_num_of_month);
//            scheduls = new FitButton[]{ itemView.findViewById(R.id.schedule1), itemView.findViewById(R.id.schedule2),
//                    itemView.findViewById(R.id.schedule3), itemView.findViewById(R.id.schedule4),
//                    itemView.findViewById(R.id.schedule5) };
//            shifts = new TextView[]{ itemView.findViewById(R.id.shift1_d1), itemView.findViewById(R.id.shift2),
//                    itemView.findViewById(R.id.shift3), itemView.findViewById(R.id.shift4),
//                    itemView.findViewById(R.id.shift5)};

        }

        public void selectDayBind(Day day)
        {
            this.num.setText(day.getDay_of_month());
            this.day.setText(day.getDay_of_week());
            num.setBackgroundResource(R.drawable.textview_desidgn);
            num.setTextColor(Color.WHITE);
        }

        public void scheduleDayBind(ArrayList<ScheduleDay> scheduleDay)
        {
            for(int i = 0; i <schedules_days.size(); i++) {
                scheduls[i].setVisibility(View.VISIBLE);
                scheduls[i].setText(schedules_name.get(i));
                shifts[i].setVisibility(View.VISIBLE);
                shifts[i].setText(scheduleDay.get(i).getShift().getName());
                shifts[i].setBackgroundColor(Color.parseColor(scheduleDay.get(i).getShift().getColor()));
            }
        }

        public void bind(Day day) {
            this.num.setText(day.getDay_of_month());
            this.day.setText(day.getDay_of_week());
        }
    }

    public class Day
    {
        String day_of_week, day_of_month;

        public Day(String day_of_month, String day_of_week) {
            this.day_of_week = day_of_week;
            this.day_of_month = day_of_month;
        }

        public String getDay_of_week() {
            return day_of_week;
        }

        public void setDay_of_week(String day_of_week) {
            this.day_of_week = day_of_week;
        }

        public String getDay_of_month() {
            return day_of_month;
        }

        public void setDay_of_month(String day_of_month) {
            this.day_of_month = day_of_month;
        }
    }
}
