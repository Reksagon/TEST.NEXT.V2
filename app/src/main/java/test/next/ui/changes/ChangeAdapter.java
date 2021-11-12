package test.next.ui.changes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awesomedialog.AwesomeDialog;
import com.github.nikartm.button.FitButton;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.ui.home.HomeFragment;

public class ChangeAdapter extends RecyclerView.Adapter<ChangeAdapter.ChangeAdapterViewHolder>{

    ArrayList<String> data;
    Activity activity;
    ArrayList<FitButton> fitButtons = new ArrayList<>();
    private boolean delete_b = false;
    public ChangeAdapter(Activity activity, ArrayList<String> strings, boolean delete)
    {
        this.activity = activity;
        data = strings;
        delete_b = delete;
    }

    public void deleteId(int id)
    {
        data.remove(id-1);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ChangeAdapter.ChangeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_view_layout, parent, false);
        return new ChangeAdapter.ChangeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeAdapter.ChangeAdapterViewHolder holder, int position) {
        if(position == HomeFragment.current_schedule)
            holder.bind(data.get(position), true, position);
        else
            holder.bind(data.get(position), false, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ChangeAdapterViewHolder extends RecyclerView.ViewHolder
    {
        FitButton fitButton, detelte;

        public ChangeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            fitButton = itemView.findViewById(R.id.button_change);
            fitButtons.add(fitButton);
            detelte = itemView.findViewById(R.id.button_delete);
        }

        public void bind(String string, boolean b, int num)
        {
            if(b)
                fitButton.setIcon(activity.getResources().getDrawable(R.drawable.check));
            fitButton.setText(string);
            fitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeFragment.current_schedule = num;
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    byte[] data = SerializationUtils.serialize(HomeFragment.scheduls.get(HomeFragment.current_schedule));
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    editor.putString("Schedule", base64);
                    editor.apply();

                    try {
                        FirebaseDatabase
                                .getInstance(new String(Base64.decode(activity.getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                .getReference()
                                .child("Users/" + AccountConst.account.getUid() + "/Settings/CurrentScheduls").setValue(HomeFragment.current_schedule);
                        fitButton.setIcon(activity.getResources().getDrawable(R.drawable.check));
                        for (int i = 0; i < fitButtons.size(); i++) {
                            if (i != num)
                                fitButtons.get(i).setIcon(activity.getResources().getDrawable(R.drawable.unchecked));
                        }
                        Toasty.success(activity, fitButton.getText() + ", "
                                + activity.getResources().getString(R.string.schedule_succes), Toast.LENGTH_SHORT).show();
                    }catch (Exception ex)
                    {
                        Toasty.error(activity, activity.getResources().getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                    }

                }
            });
            if(delete_b) {
                detelte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogDelete.Companion.getDialog(activity, string, HomeFragment.scheduls.get(num).getId(), ChangeAdapter.this);
                    }
                });
            }
            else
            {
                detelte.setVisibility(View.GONE);
            }
        }
    }
}
