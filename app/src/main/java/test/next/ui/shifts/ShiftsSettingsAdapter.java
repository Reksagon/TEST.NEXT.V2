package test.next.ui.shifts;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.powerspinner.PowerSpinnerView;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import test.next.R;
import test.next.constant.Shifts;
import test.next.ui.create.ShiftsAdapter;

public class ShiftsSettingsAdapter extends RecyclerView.Adapter<ShiftsSettingsAdapter.ShiftsSettingsAdapterVH> {
    ArrayList<Shifts> shifts;
    Activity activity;

    public ShiftsSettingsAdapter(ArrayList<Shifts> shifts, Activity activity) {
        this.shifts = shifts;
        this.activity = activity;
        Shifts shifts1 = new Shifts(shifts.size()+1,activity.getResources().getString(R.string.add_shift), "00:00", "00:00", "#ffffff", false);
        shifts.add(shifts1);
    }



    @NonNull
    @Override
    public ShiftsSettingsAdapter.ShiftsSettingsAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shifts_view_layout, parent, false);
        return new ShiftsSettingsAdapter.ShiftsSettingsAdapterVH(view);
    }


    @Override
    public int getItemCount() {
        return shifts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftsSettingsAdapterVH holder, int position) {
        if(position == shifts.size()-1)
            holder.bindAdd(shifts.get(position));
        else
            holder.bind(shifts.get(position));
    }


    public class ShiftsSettingsAdapterVH extends RecyclerView.ViewHolder {

        ExpandableLayout expandableLayout;
        ExpandableHintText name, start_work, end_work, start_lanch, end_lanch, color_pick;
        TextView button;
        LinearLayout content;
        CheckBox checkBox;

        public ShiftsSettingsAdapterVH(@NonNull View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            name = itemView.findViewById(R.id.shift_name);
            start_work = itemView.findViewById(R.id.shift_start);
            end_work = itemView.findViewById(R.id.shift_end);
            start_lanch = itemView.findViewById(R.id.shift_start_lanch);
            end_lanch = itemView.findViewById(R.id.shift_end_lanch);
            color_pick = itemView.findViewById(R.id.shift_color);
            content = itemView.findViewById(R.id.content_shifts_set);
            button = itemView.findViewById(R.id.expand_button);
            checkBox = itemView.findViewById(R.id.day_off);
        }

        public void bind(Shifts shift) {
            content.setBackgroundColor(Color.parseColor(shift.getColor()));
            button.setText(shift.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expandableLayout.isExpanded())
                        expandableLayout.collapse();
                    else
                        expandableLayout.expand();
                }
            });
            name.setText(shift.getName());

            start_work.setClickable(false);
            start_work.setEnabled(false);
            start_work.setText(shift.getStart());

            end_work.setClickable(false);
            end_work.setEnabled(false);
            end_work.setText(shift.getEnd());

            start_lanch.setClickable(false);
            start_lanch.setEnabled(false);
            start_lanch.setText(shift.getStart_lanch());

            end_lanch.setClickable(false);
            end_lanch.setEnabled(false);
            end_lanch.setText(shift.getEnd_lanch());

            color_pick.setClickable(false);
            color_pick.setEnabled(false);
            color_pick.setText(shift.getColor());

            checkBox.setChecked(shift.isOffday());
        }

        public void bindAdd(Shifts shift)
        {
            expandableLayout.expand();
            content.setBackgroundColor(activity.getResources().getColor(R.color.colorText));
            button.setText(shift.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expandableLayout.isExpanded())
                        expandableLayout.collapse();
                    else
                        expandableLayout.expand();
                }
            });


            start_work.setClickable(false);
            start_work.setEnabled(false);

            end_work.setClickable(false);
            end_work.setEnabled(false);

            start_lanch.setClickable(false);
            start_lanch.setEnabled(false);

            end_lanch.setClickable(false);
            end_lanch.setEnabled(false);

            color_pick.setClickable(false);
            color_pick.setEnabled(false);
            color_pick.setText(shift.getColor());

        }
    }

}
