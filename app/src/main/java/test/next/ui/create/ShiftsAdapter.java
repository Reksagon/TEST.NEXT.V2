package test.next.ui.create;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.Shifts;

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ShiftsAdapterVH>{

    List<Shifts> shifts;
    private int count = 1;
    List<Integer> selected_shift;
    List<Integer> selected_day;
    List<PowerSpinnerView> shifts_schedule = new ArrayList<>();
    List<PowerSpinnerView> days_schedule = new ArrayList<>();
    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ShiftsAdapter(List<Shifts> shifts, List<Integer> selected_shift, List<Integer> selected_day ) {
        this.shifts = shifts;
        this.selected_shift = selected_shift;
        this.selected_day = selected_day;
    }

    public List<Integer> getSelected_shift() {
        return selected_shift;
    }

    public List<Integer> getSelected_day() {
        return selected_day;
    }

    public List<PowerSpinnerView> getShifts_schedule() {
        return shifts_schedule;
    }

    public void setShifts_schedule(List<PowerSpinnerView> shifts_schedule) {
        this.shifts_schedule = shifts_schedule;
    }

    public List<PowerSpinnerView> getDays_schedule() {
        return days_schedule;
    }

    public void setDays_schedule(List<PowerSpinnerView> days_schedule) {
        this.days_schedule = days_schedule;
    }

    @NonNull
    @Override
    public ShiftsAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_layout, parent, false);
        return new ShiftsAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftsAdapterVH holder, int position) {
        holder.bind(shifts, selected_shift.get(position), selected_day.get(position), position);
    }

    public void AddShift(Integer num)
    {
        selected_shift.add(num);
        notifyDataSetChanged();
    }

    public void AddDay(Integer num)
    {
        selected_day.add(num);
        notifyDataSetChanged();
    }

    public void RemoveShift()
    {
        try {
            selected_shift.remove(selected_shift.size() - 1);
            shifts_schedule.remove(shifts_schedule.size() - 1);
            notifyDataSetChanged();
        }
        catch (Exception ex)
        {

        }
    }

    public void RemoveDay()
    {
        try {
            selected_day.remove(selected_day.size() - 1);
            days_schedule.remove(days_schedule.size() - 1);
            notifyDataSetChanged();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    public class ShiftsAdapterVH extends RecyclerView.ViewHolder {

        PowerSpinnerView spinner_shift, spinner_day;

        public ShiftsAdapterVH(@NonNull View itemView) {
            super(itemView);
            spinner_shift = itemView.findViewById(R.id.spinner);
            spinner_day = itemView.findViewById(R.id.spinner2);
            shifts_schedule.add(spinner_shift);
            days_schedule.add(spinner_day);
        }
        public void bind(List<Shifts> shifts, int select_shift, int select_day, int position)
        {
            List<String> strings = new ArrayList<>();
            for(Shifts shift : shifts)
                strings.add(shift.getName());
            spinner_shift.setItems(strings);
            ArrayList<String> days = new ArrayList<>();
            days.add("1");
            days.add("2");
            days.add("3");
            days.add("4");
            days.add("5");
            days.add("6");
            days.add("7");
            days.add("8");
            days.add("9");
            days.add("10");

            spinner_day.setItems(days);

            if(select_shift != -1)
                spinner_shift.selectItemByIndex(select_shift);

            if(select_day != -1)
                spinner_day.selectItemByIndex(select_day);



            spinner_shift.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                @Override
                public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                    int ss = 0;
                    for(PowerSpinnerView powerSpinnerView : shifts_schedule)
                    {
                        if(powerSpinnerView.getSelectedIndex() == i1)
                            ss++;
                    }
                    if(ss < 2) {
                        selected_shift.remove(position);
                        selected_shift.add(position, i1);
                    }
                    else
                    {
                        Toasty.warning(activity, activity.getResources().getString(R.string.create_warning), Toasty.LENGTH_SHORT).show();
                    }
                }
            });

            spinner_day.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
                @Override
                public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                    selected_day.remove(position);
                    selected_day.add(position, i1);
                }
            });
        }
    }

}
