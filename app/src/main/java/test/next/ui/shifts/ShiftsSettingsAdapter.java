package test.next.ui.shifts;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.nikartm.button.FitButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.ozcanalasalvar.library.view.popup.TimePickerPopup;
import com.tomlonghurst.expandablehinttext.ExpandableHintText;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.igenius.customcheckbox.CustomCheckBox;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.ui.home.HomeFragment;

public class ShiftsSettingsAdapter extends RecyclerView.Adapter<ShiftsSettingsAdapter.ShiftsSettingsAdapterVH> {
    ArrayList<Shifts> shifts;
    Activity activity;
    private static boolean first = false;

    public ShiftsSettingsAdapter(ArrayList<Shifts> shifts, Activity activity) {
        this.shifts = shifts;
        this.activity = activity;
        if(!first) {
            Shifts shifts1 = new Shifts(shifts.size() + 1, activity.getResources().getString(R.string.add_shift), "00:00", "00:00", "#ffffff", false);
            this.shifts.add(shifts1);
            first = true;
        }

    }



    @NonNull
    @Override
    public ShiftsSettingsAdapter.ShiftsSettingsAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shifts_view_layout, parent, false);
        return new ShiftsSettingsAdapter.ShiftsSettingsAdapterVH(view);
    }

    public void AddShift(Shifts shift)
    {
        shifts.remove(shifts.size()-1);
        shifts.add(shift);
        Shifts shifts1 = new Shifts(shifts.size()+1,activity.getResources().getString(R.string.add_shift), "00:00", "00:00", "#ffffff", false);
        shifts.add(shifts1);
        notifyDataSetChanged();
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
        CustomCheckBox checkBox;
        FitButton fbtn, delete;

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
            fbtn = itemView.findViewById(R.id.button_change);
            delete = itemView.findViewById(R.id.button_delete_shift);
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
            start_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(start_work);
                }
            });

            end_work.setClickable(false);
            end_work.setEnabled(false);
            end_work.setText(shift.getEnd());
            end_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(end_work);
                }
            });

            start_lanch.setClickable(false);
            start_lanch.setEnabled(false);
            start_lanch.setText(shift.getStart_lanch());
            start_lanch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(start_lanch);
                }
            });

            end_lanch.setClickable(false);
            end_lanch.setEnabled(false);
            end_lanch.setText(shift.getEnd_lanch());
            end_lanch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(end_lanch);
                }
            });

            color_pick.setClickable(false);
            color_pick.setEnabled(false);
            color_pick.setText(shift.getColor());


            checkBox.setChecked(shift.isOffday());

            fbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFBTask().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            try {
                                for (DataSnapshot child : task.getResult().getChildren()) {
                                    Shifts str1 = child.getValue(Shifts.class);
                                    if (str1.getId() == shift.getId()) {
                                        str1.setName(name.getText());
                                        str1.setStart(start_work.getText());
                                        str1.setEnd(end_work.getText());
                                        str1.setStart_lanch(start_lanch.getText());
                                        str1.setEnd_lanch(end_lanch.getText());
                                        str1.setOffday(checkBox.isChecked());
                                        str1.setColor(color_pick.getText());
                                        child.getRef().setValue(str1);
                                        content.setBackgroundColor(Color.parseColor(color_pick.getText()));
                                        button.setText(str1.getName());

                                        for (int i = 0; i < AccountConst.shiftsArrayList.size(); i++) {
                                            if (AccountConst.shiftsArrayList.get(i).getId() == str1.getId()) {
                                                AccountConst.shiftsArrayList.remove(i);
                                                AccountConst.shiftsArrayList.add(i, str1);
                                            }
                                        }
                                        if (HomeFragment.scheduls.size() > 0) {
                                            for (Schedule schedule : HomeFragment.scheduls) {
                                                schedule.changeShift(str1);
                                                byte[] data = SerializationUtils.serialize(schedule);
                                                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                                ScheduleFB scheduleFB_new = new ScheduleFB(String.valueOf(schedule.getId()), base64);

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
                                                                Toasty.success(activity, activity.getResources().getString(R.string.success_shift), Toast.LENGTH_SHORT, true).show();
                                                            }
                                                        } catch (Exception ex) {
                                                            Toasty.error(activity, activity.getResources().getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }catch (Exception ex)
                            {
                                Toasty.error(activity, activity.getResources().getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            if(shift.getId() > 3) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < AccountConst.shiftsArrayList.size(); i++) {
                            if (AccountConst.shiftsArrayList.get(i).getId() == shift.getId()) {
                                shifts.remove(i);
                                ShiftsSettingsAdapter.this.notifyDataSetChanged();
                                Task<DataSnapshot> dataSnapshotTask = FirebaseDatabase
                                        .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                                        .getReference()
                                        .child("Users/" + AccountConst.account.getUid() + "/Shifts").get();
                                dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        try {
                                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                                Shifts shifts_fb = dataSnapshot.getValue(Shifts.class);
                                                if (shifts_fb.getId() == shift.getId()) {
                                                    dataSnapshot.getRef().setValue(null);
                                                    Toasty.success(activity, activity.getResources().getString(R.string.delete_success_shift), Toast.LENGTH_SHORT, true).show();
                                                    break;
                                                }

                                            }
                                        } catch (Exception ex)
                                        {
                                            Toasty.error(activity, activity.getResources().getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;

                            }
                        }


                    }
                });
            }
            else
            {
                delete.setVisibility(View.GONE);
            }

            color_pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorPickerDialogBuilder
                            .with(activity)
                            .setTitle(activity.getResources().getString(R.string.choose_color))
                            .initialColor(Color.parseColor(color_pick.getText()))
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(new OnColorSelectedListener() {
                                @Override
                                public void onColorSelected(int selectedColor) {
                                }
                            })
                            .setPositiveButton("OK", new ColorPickerClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                    color_pick.setText(String.format("#%06X", (0xFFFFFF & selectedColor)));
                                }
                            })
                            .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .build()
                            .show();
                }
            });
        }

        private Task<DataSnapshot> getFBTask()
        {
            return FirebaseDatabase
                    .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("Users/" + AccountConst.account.getUid() + "/Shifts").get();
        }


        public void setTime(ExpandableHintText textView)
        {
            String[] split = textView.getText().split(":");
            int hour = 0, minute = 0;
            if(split[0] != "")
            {
                hour = Integer.parseInt(split[0]);
                minute = Integer.parseInt(split[1]);
            }
            TimePickerPopup timePickerPopup = new TimePickerPopup.Builder()
                    .from(activity)
                    .offset(3)
                    .textSize(17)
                    .setTime(hour, minute)
                    .listener(new TimePickerPopup.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelected(com.ozcanalasalvar.library.view.timePicker.TimePicker timePicker, int hour, int minute) {
                            String str = "";
                            if(hour < 10)
                                str += "0" + String.valueOf(hour) + ":";
                            else
                                str += String.valueOf(hour) + ":";

                            if(minute < 10)
                                str += "0" + String.valueOf(minute);
                            else
                                str += String.valueOf(minute) + ":";
                            textView.setText(str);
                        }
                    })
                    .build();
            timePickerPopup.show();
        }

        public void bindAdd(Shifts shift)
        {
            fbtn.setIcon(activity.getResources().getDrawable(R.drawable.add_ic));
            fbtn.setText(activity.getResources().getString(R.string.add_shift));
            //expandableLayout.expand();
            delete.setVisibility(View.GONE);
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
            start_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(start_work);
                }
            });

            end_work.setClickable(false);
            end_work.setEnabled(false);
            end_work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(end_work);
                }
            });

            start_lanch.setClickable(false);
            start_lanch.setEnabled(false);
            start_lanch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(start_lanch);
                }
            });

            end_lanch.setClickable(false);
            end_lanch.setEnabled(false);
            end_lanch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTime(end_lanch);
                }
            });

            color_pick.setClickable(false);
            color_pick.setEnabled(false);
            color_pick.setText(shift.getColor());

            fbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFBTask().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            Shifts str1 = new Shifts();
                            str1.setId(shifts.size());
                            str1.setName(name.getText());
                            str1.setStart(start_work.getText());
                            str1.setEnd(end_work.getText());
                            str1.setStart_lanch(start_lanch.getText());
                            str1.setEnd_lanch(end_lanch.getText());
                            str1.setOffday(checkBox.isChecked());
                            str1.setColor(color_pick.getText());

                            task.getResult().getRef().push().setValue(str1);
                            content.setBackgroundColor(Color.parseColor(color_pick.getText()));
                            Toasty.success(activity, activity.getResources().getString(R.string.success_shift), Toast.LENGTH_SHORT, true).show();
                            button.setText(str1.getName());

                            shifts.remove(shifts.size()-1);
                            shifts.add(str1);
                            Shifts shifts1 = new Shifts(shifts.size()+1,activity.getResources().getString(R.string.add_shift), "00:00", "00:00", "#ffffff", false);
                            shifts.add(shifts1);
                            notifyDataSetChanged();

                            fbtn.setIcon(activity.getResources().getDrawable(R.drawable.change));
                            fbtn.setText(activity.getResources().getString(R.string.change));
                        }
                    });
                }
            });

            color_pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorPickerDialogBuilder
                            .with(activity)
                            .setTitle(activity.getResources().getString(R.string.choose_color))
                            .initialColor(Color.parseColor(color_pick.getText()))
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(12)
                            .setOnColorSelectedListener(new OnColorSelectedListener() {
                                @Override
                                public void onColorSelected(int selectedColor) {
                                }
                            })
                            .setPositiveButton("OK", new ColorPickerClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                    color_pick.setText(String.format("#%06X", (0xFFFFFF & selectedColor)));
                                }
                            })
                            .setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .build()
                            .show();
                }
            });

        }
    }

}
