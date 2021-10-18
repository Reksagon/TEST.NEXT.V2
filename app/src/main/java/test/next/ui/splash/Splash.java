package test.next.ui.splash;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.util.concurrent.TimeUnit;

import test.next.MainActivity;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleFB;
import test.next.databinding.ActivityMainBinding;
import test.next.databinding.FragmentSlideshowBinding;
import test.next.databinding.SplashFragmentBinding;
import test.next.ui.home.HomeFragment;
import test.next.ui.home.HomeViewModel;

public class Splash extends Fragment {

    private SplashViewModel mViewModel;
    private SplashFragmentBinding binding;
    private Task<DataSnapshot> dataSnapshotTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        binding = SplashFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPostExecute(Void unused) {
                mViewModel.getDataSnapshotMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Task<DataSnapshot>>() {
                    @Override
                    public void onChanged(@Nullable Task<DataSnapshot> task) {
                        dataSnapshotTask = task;
                    }
                });

                mViewModel.getOnCompleteListenerMutableLiveData().observe(getViewLifecycleOwner(), new Observer<OnCompleteListener<DataSnapshot>>() {
                    @Override
                    public void onChanged(@Nullable OnCompleteListener<DataSnapshot> onCompleteListener) {
                        dataSnapshotTask.addOnCompleteListener(onCompleteListener);
                    }
                });

                Task<DataSnapshot> databaseReference = FirebaseDatabase
                        .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Scheduls").get();
                databaseReference.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for(DataSnapshot child : task.getResult().getChildren())
                        {
                            ScheduleFB scheduleFB = child.getValue(ScheduleFB.class);
                            String data = null;
                            if (scheduleFB != null)
                                data = scheduleFB.getData();

                            byte[] data2 = Base64.decode(data, Base64.DEFAULT);
                            Schedule schedule = SerializationUtils.deserialize(data2);
                            schedule.getScheduleDayArrayList();
                            HomeFragment.schedule = schedule;

                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.nav_home);
                            MainActivity.binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
                        }
                    }
                });

                super.onPostExecute(unused);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                while (true)
                {
                    if(AccountConst.account != null)
                        break;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }.execute();


        return root;
    }



}