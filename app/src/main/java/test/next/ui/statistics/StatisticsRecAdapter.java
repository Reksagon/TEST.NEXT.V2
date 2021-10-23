package test.next.ui.statistics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import test.next.R;
import test.next.constant.ScheduleDay;
import test.next.constant.Shifts;

public class StatisticsRecAdapter extends RecyclerView.Adapter<StatisticsRecAdapter.StatisticsRecAdapterVH> {

    ArrayList<ScheduleDay> scheduleDays;
    ArrayList<Maps> shifts;
    ArrayList<Integer> hours_send;

    public StatisticsRecAdapter(ArrayList<ScheduleDay> scheduleDays) throws ParseException {
        this.scheduleDays = scheduleDays;
        shifts = new ArrayList<>();
        hours_send = new ArrayList<>();

        for(ScheduleDay day : scheduleDays)
        {
            boolean exist = false;
            for(Maps maps : shifts)
            {
                if(maps.getShift().getId() == day.getShift().getId())
                {
                    exist = true;
                    break;
                }
            }
            if(!exist)
            {
                shifts.add(new Maps(day.getShifts()));
                for(Maps m : shifts)
                {
                    if(m.getShift().getId() == day.getShift().getId()) {
                        m.Add(day.getShift());
                        break;
                    }
                }
            }
            else
            {
                for(Maps m : shifts)
                {
                    if(m.getShift().getId() == day.getShift().getId()) {
                        m.Add(day.getShift());
                        break;
                    }
                }
            }
        }


    }

    @NonNull
    @Override
    public StatisticsRecAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stat_day_view, parent, false);
        return new StatisticsRecAdapter.StatisticsRecAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsRecAdapterVH holder, int position) {
        holder.setIsRecyclable(false);
        try {
            holder.bind(shifts.get(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public class StatisticsRecAdapterVH extends RecyclerView.ViewHolder {

        ExpandableHintText text,day, hour;

        public StatisticsRecAdapterVH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.name_shift);
            day = itemView.findViewById(R.id.count_days);
            hour = itemView.findViewById(R.id.count_hours);

        }
        public void bind(Maps maps) throws ParseException {
            text.setText(maps.getShift().getName());
            day.setText(String.valueOf(maps.shifts.size()));
            hour.setText("22");
            text.setEnabled(false);
            day.setEnabled(false);
            hour.setEnabled(false);



            if(maps.name.isOffday())
                hour.setText("--:--");
            else
            {
                hour.setText(String.valueOf(maps.getHours()));
            }

        }
    }

    public class Maps
    {
        Shifts name;
        ArrayList<Shifts> shifts;

        public Maps(Shifts name) {
            this.name = name;
            shifts = new ArrayList<>();
        }
        public void Add(Shifts shift)
        {
            shifts.add(shift);
        }

        public Shifts getShift() {
            return name;
        }

        public void setId(Shifts id) {
            this.name = id;
        }

        public ArrayList<Shifts> getShifts() {
            return shifts;
        }

        public void setShifts(ArrayList<Shifts> shifts) {
            this.shifts = shifts;
        }

        public int getHours() throws ParseException {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm");
            int hours = 0;
            int minuts = 0;
            int hours_lanch = 0, minuts_lanch = 0;

            for (Shifts shifts : shifts) {
                String[] start_split, end_split;
                start_split = shifts.getStart().split(":");
                end_split = shifts.getEnd().split(":");


                Date date1 = simpleDateFormat.parse("01/01/2020 " + shifts.getStart());
                date1.setHours(Integer.parseInt(start_split[0]));
                date1.setMinutes(Integer.parseInt(start_split[1]));
                Date date2;
                if (Integer.parseInt(start_split[0]) > Integer.parseInt(end_split[0])) {
                    date2 = simpleDateFormat.parse("02/01/2020 " + shifts.getEnd());
                    date2.setHours(Integer.parseInt(end_split[0]));
                    date2.setMinutes(Integer.parseInt(end_split[1]));
                } else {
                    date2 = simpleDateFormat.parse("01/01/2020 " + shifts.getEnd());
                    date2.setHours(Integer.parseInt(end_split[0]));
                    date2.setMinutes(Integer.parseInt(end_split[1]));
                }


                long different = date2.getTime() - date1.getTime();

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;
                long daysInMilli = hoursInMilli * 24;

                long elapsedDays = different / daysInMilli;
                different = different % daysInMilli;

                long elapsedHours = different / hoursInMilli;
                different = different % hoursInMilli;

                long elapsedMinutes = different / minutesInMilli;
                different = different % minutesInMilli;

                hours += elapsedHours;
                minuts += elapsedMinutes;
                if (minuts >= 60) {
                    hours++;
                    minuts -= 60;
                }

                String[] start_split_lanch, end_split_lanch;
                start_split_lanch = shifts.getStart_lanch().split(":");
                end_split_lanch = shifts.getEnd_lanch().split(":");

                Date date1_lanch = simpleDateFormat.parse("01/01/2020 " + shifts.getStart_lanch());
                date1_lanch.setHours(Integer.parseInt(start_split_lanch[0]));
                date1_lanch.setMinutes(Integer.parseInt(start_split_lanch[1]));

                Date date2_lanch= simpleDateFormat.parse("01/01/2020 " + shifts.getEnd_lanch());
                date2_lanch.setHours(Integer.parseInt(end_split_lanch[0]));
                date2_lanch.setMinutes(Integer.parseInt(end_split_lanch[1]));

                long different_lanch = date2_lanch.getTime() - date1_lanch.getTime();

                long secondsInMilli_lanch = 1000;
                long minutesInMilli_lanch = secondsInMilli_lanch * 60;
                long hoursInMilli_lanch = minutesInMilli_lanch * 60;
                long daysInMilli_lanch = hoursInMilli_lanch * 24;

                long elapsedDays_lanch = different_lanch / daysInMilli_lanch;
                different_lanch = different_lanch % daysInMilli_lanch;

                long elapsedHours_lanch = different_lanch / hoursInMilli_lanch;
                different_lanch = different_lanch % hoursInMilli_lanch;

                long elapsedMinutes_lanch = different_lanch / minutesInMilli_lanch;
                different_lanch = different_lanch % minutesInMilli_lanch;

                hours_lanch += elapsedHours_lanch;
                minuts_lanch += elapsedMinutes_lanch;
                if (minuts_lanch >= 60) {
                    hours_lanch++;
                    minuts_lanch -= 60;
                }


            }

            hours -= hours_lanch;

            return hours;

        }
    }
}
