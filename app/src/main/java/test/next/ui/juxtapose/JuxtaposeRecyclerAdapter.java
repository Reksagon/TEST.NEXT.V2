package test.next.ui.juxtapose;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.nikartm.button.FitButton;
import java.util.ArrayList;
import test.next.R;
import test.next.constant.Schedule;
import test.next.ui.home.HomeFragment;

public class JuxtaposeRecyclerAdapter extends RecyclerView.Adapter<JuxtaposeRecyclerAdapter.JuxtaposeViewHolder> {

    private ArrayList<String> data;
    ArrayList<Schedule> schedules;
    private Activity activity;

    public JuxtaposeRecyclerAdapter(Activity activity) {
        data = new ArrayList<>();
        for(Schedule schedule : HomeFragment.scheduls)
            data.add(schedule.getName());
        schedules = new ArrayList<>();
        this.activity = activity;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    @NonNull
    @Override
    public JuxtaposeRecyclerAdapter.JuxtaposeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_view_layout, parent, false);
        return new JuxtaposeRecyclerAdapter.JuxtaposeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JuxtaposeRecyclerAdapter.JuxtaposeViewHolder holder, int position) {
            holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class JuxtaposeViewHolder extends RecyclerView.ViewHolder {

        FitButton fitButton, delete;
        boolean check = false;

        public JuxtaposeViewHolder(@NonNull View itemView) {
            super(itemView);
            fitButton = itemView.findViewById(R.id.button_change);
            delete = itemView.findViewById(R.id.button_delete);
        }

        public void bind(String string, int num) {
            delete.setVisibility(View.GONE);
            fitButton.setText(string);
            fitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(check) {
                        fitButton.setIcon(activity.getResources().getDrawable(R.drawable.unchecked));
                        check = false;
                        int i = 0;
                        for(Schedule schedule : schedules)
                        {
                            if(schedule.getId() == HomeFragment.scheduls.get(num).getId())
                                break;
                            i++;
                        }
                        schedules.remove(i);
                    }
                    else
                    {
                        fitButton.setIcon(activity.getResources().getDrawable(R.drawable.check));
                        check = true;
                        schedules.add(HomeFragment.scheduls.get(num));
                    }
                }
            });
        }
    }
}
