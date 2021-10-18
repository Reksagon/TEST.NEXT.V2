package test.next.ui.create;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleDay;
import test.next.constant.Shifts;
import test.next.databinding.FragmentCreateBinding;
import test.next.ui.home.HomeFragment;

public class CreateFragment extends Fragment {

    private CreateViewModel createViewModel;
    private FragmentCreateBinding binding;
    private boolean exist = false;
    private ArrayList<Shifts> shifts;
    private ShiftsAdapter adapter;
    private ArrayList<Integer> count_shifts = new ArrayList<>(), count_days = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel =
                new ViewModelProvider(this).get(CreateViewModel.class);
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        createViewModel.setActivity(getActivity(), binding);
        View root = binding.getRoot();

        binding.date.setClickable(false);
        binding.date.setEnabled(false);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
        String formatted = format1.format(cal.getTime());
        binding.date.setText(formatted);

        createViewModel.CreateView();


        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);

        createViewModel.getmDateClick().observe(getViewLifecycleOwner(), new Observer<View.OnClickListener>() {
            @Override
            public void onChanged(View.OnClickListener onClickListener) {
                binding.date.setOnClickListener(onClickListener);
            }
        });

//        DatabaseReference databaseReference = FirebaseDatabase
//                .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
//                .getReference()
//                .child("Users/" + AccountConst.account.getId() + "/Shifts");
        //databaseReference.push().setValue(new Shifts("TestName1", "TestStart1", "TestEnd1", "TestColor1"));
        //databaseReference.child(AccountConst.account.getId()).setValue(new Shifts("1","2", "3", "4"));
//        databaseReference.child(AccountConst.account.getId()).child("Shifts").child("TestName1").
//                setValue(new Shifts("TestName1", "TestStart1", "TestEnd1", "TestColor1"));
//        databaseReference.child(AccountConst.account.getId()).child("Shifts").child("TestName2").
//                setValue(new Shifts("TestName2", "TestStart2", "TestEnd2", "TestColor2"));

        Task<DataSnapshot> dataSnapshotTask =  FirebaseDatabase
                .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                .getReference()
                .child("Users/").get();
        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot child : task.getResult().getChildren())
                {
                    if(child.getKey().toString().equals(AccountConst.account.getId()))
                        exist = true;
                }
                if(!exist)
                {
                    DatabaseReference databaseReference = FirebaseDatabase
                            .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                            .getReference()
                            .child("Users/" + AccountConst.account.getId() + "/Shifts");
                    databaseReference.push()
                            .setValue(new Shifts("TestName1", "TestStart1", "TestEnd1", "#eb3434"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName2", "TestStart2", "TestEnd2", "#ebc634"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName3", "TestStart3", "TestEnd3", "#96eb34"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName4", "TestStart4", "TestEnd4", "#34ebba"));
                }
                else
                {
                    Task<DataSnapshot> getShiftsTask = FirebaseDatabase
                            .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                            .getReference()
                            .child("Users/" + AccountConst.account.getId() + "/Shifts").get();
                    getShiftsTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            shifts = new ArrayList<>();
                            for(DataSnapshot child : task.getResult().getChildren())
                            {
                                Shifts str1 = child.getValue(Shifts.class);
                                shifts.add(str1);
                            }
                            count_shifts.add(-1);
                            count_days.add(-1);
                            adapter = new ShiftsAdapter(shifts, count_shifts, count_days);
                            adapter.setCount(1);
                            LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
                            linearLayout.setOrientation(RecyclerView.VERTICAL);
                            binding.shiftsView.setLayoutManager(linearLayout);
                            binding.shiftsView.setAdapter(adapter);
                        }
                    });

                }

              }
        });


        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.AddShift(-1);
                adapter.AddDay(-1);
                adapter.setCount(adapter.getCount()+1);
            }
        });

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getCount() != 1) {
                    adapter.RemoveShift();
                    adapter.RemoveDay();
                    adapter.setCount(adapter.getCount() - 1);
                }
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Shifts> shifts_send = new ArrayList<>();
                ArrayList<Integer> days_send = new ArrayList<>();
                for(PowerSpinnerView view1 : adapter.getShifts_schedule())
                {
                    if(view1.getSelectedIndex() != -1)
                        shifts_send.add(shifts.get(view1.getSelectedIndex()));
                }

                for(PowerSpinnerView view1 : adapter.getDays_schedule())
                {
                    if(view1.getSelectedIndex() != -1)
                        days_send.add(view1.getSelectedIndex() + 1);
                }

                shifts_send.remove(0);
                days_send.remove(0);

                String[] split = binding.date.getText().split("/");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(split[1])-1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[2]));

                Schedule schedule = new Schedule(shifts_send, days_send,calendar);
                HomeFragment.schedule = schedule;

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });
//        Task<DataSnapshot> dataSnapshotTask =  databaseReference.get();
//        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                for(DataSnapshot child : task.getResult().getChildren())
//                {
//                    Shifts str1 = child.getValue(Shifts.class);
//                    str1.getColor();
//                }
//              }
//        });

//        FirebaseDatabase
//                .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
//                .getReference()
//                .child("Users/").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean exist = false;
//
//                for(DataSnapshot child : snapshot.getChildren())
//                {
//
//                    if(child.getKey().toString().equals(AccountConst.account.getId()))
//                        exist = true;
//                }
//
//                if(!exist)
//                {
//                    //databaseReference.child(AccountConst.account.getId()).child("Shifts").setValue(new Shifts("1","2", "3", "4"));
//
////                    Shifts[] shifts = {new Shifts("TestName1", "TestStart1", "TestEnd1", "TestColor1"),
////                            new Shifts("TestName2", "TestStart2", "TestEnd2", "TestColor2"),
////                            new Shifts("TestName3", "TestStart3", "TestEnd3", "TestColor3"),
////                            new Shifts("TestName4", "TestStart4", "TestEnd4", "TestColor4")
////
////                    };
////                    databaseReference.child("Users").child(AccountConst.account.getId()).child("Shifts");
////
////                    //Account account = new Account(AccountConst.account.getId());
////                    databaseReference.push().setValue(new Shifts("1", "2", "3", "4"));
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}