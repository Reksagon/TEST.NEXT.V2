package test.next.ui.splash;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import test.next.MainActivity;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Schedule;
import test.next.constant.ScheduleFB;
import test.next.constant.Shifts;
import test.next.databinding.ActivityMainBinding;
import test.next.databinding.FragmentSplashBinding;
import test.next.ui.home.HomeFragment;
import test.next.ui.home.HomeViewModel;

public class Splash extends Fragment {

    private SplashViewModel mViewModel;
    private FragmentSplashBinding binding;
    private Task<DataSnapshot> dataSnapshotTask;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        Sprite doubleBounce = new CubeGrid();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);

        if(!sharedPreferences.getString("SignIN", "No").equals("No"))
        {
            binding.splashText.setVisibility(View.GONE);
            binding.signIn.setVisibility(View.GONE);
            binding.spinKit.setVisibility(View.VISIBLE);
            signIn();
            mViewModel.setSetting(getActivity());
            getData().execute();
        }

        if(!sharedPreferences.getString("Background", "default").equals("default"))
        {
            String base64Str = sharedPreferences.getString("Background", "default");
            byte[] decodedBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",")  + 1),
                    Base64.DEFAULT
            );
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            AccountConst.background = bitmap;
        }

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getActivity().registerReceiver(new BroadcastReceiver() {
            public boolean check;
            ConnectivityManager manager;
            NetworkInfo info;
            @Override

            public void onReceive(Context context, Intent intent) {
                Manager();
                Info();
                check = info != null && info.isConnectedOrConnecting();
                if (check && AccountConst.account == null) {
                    binding.signIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signIn();
                            mViewModel.setSetting(getActivity());
                            getData().execute();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }

            void Manager() {
                manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            }

            void Info() {
                info = manager.getActiveNetworkInfo();
            }
        }, intentFilter);


        return root;
    }

    private void signIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 123);
    }

    private AsyncTask<Void, Void, Void> getData()
    {
        return new AsyncTask<Void, Void, Void>()
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


                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Shifts").get().addOnCompleteListener(
                        new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                AccountConst.shiftsArrayList = new ArrayList<>();
                                for (DataSnapshot child : task.getResult().getChildren()) {
                                    Shifts str1 = child.getValue(Shifts.class);
                                    AccountConst.shiftsArrayList.add(str1);
                                }
                            }
                        });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Settings/CurrentScheduls").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Long num = task.getResult().getValue(Long.class);
                        if(num != null)
                            HomeFragment.current_schedule = Math.toIntExact(num);
                    }
                });

                Task<DataSnapshot> databaseReference = FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
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
                            HomeFragment.scheduls.add(schedule);
                        }
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_home);
                        MainActivity.binding.appBarMain.toolbar.setVisibility(View.VISIBLE);
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getId() + "/Settings/Board").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.getResult().getValue(String.class).equals("true"))
                        {
                            AccountConst.board = true;
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
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else
        {
            System.exit(0);
        }
    }

    private void UpdateUI(GoogleSignInAccount account) throws IOException {
        TextView name = getActivity().findViewById(R.id.name_account);
        name.setText(account.getGivenName());
        TextView email = getActivity().findViewById(R.id.email_account);
        email.setText(account.getEmail());
        ImageView imageView = getActivity().findViewById(R.id.photo_account);
        Glide.with(getActivity()).load(account.getPhotoUrl().toString())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        AccountConst.account = account;
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            if(account != null)
            {
                binding.splashText.setVisibility(View.GONE);
                binding.signIn.setVisibility(View.GONE);
                binding.spinKit.setVisibility(View.VISIBLE);
                editor.putString("SignIN", "Yes");
                editor.apply();
                UpdateUI(account);
            }

        } catch (ApiException | FileNotFoundException e) {
            Log.d("EROOR", e.toString());
            //System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}