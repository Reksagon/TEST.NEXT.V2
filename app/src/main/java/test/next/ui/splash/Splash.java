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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import me.wangyuwei.particleview.ParticleView;
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
    private FirebaseAuth mAuth;
    private boolean error = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AccountConst.activity = getActivity();
        AccountConst.LoadAd();

        mAuth = FirebaseAuth.getInstance();

        binding.splashText.setVisibility(View.GONE);
        binding.signIn.setVisibility(View.GONE);
        binding.buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error = false;
                LoadData();
                binding.buttonRepeat.setVisibility(View.GONE);
                binding.buttonOffline.setVisibility(View.GONE);

            }
        });
        binding.splash.startAnim();
        binding.splash.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                LoadData();
            }
        });

        Sprite doubleBounce = new CubeGrid();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                mViewModel.setSetting(getActivity());
                getData().execute();
            }
        });

        binding.buttonOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountConst.offline = true;
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });


        return root;
    }

    private void LoadData() {
        if (!sharedPreferences.getString("SignIN", "No").equals("No")) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            UpdateUI(currentUser);
            binding.splashText.setVisibility(View.GONE);
            binding.signIn.setVisibility(View.GONE);
            binding.spinKit.setVisibility(View.VISIBLE);
            mViewModel.setSetting(getActivity());
            getData().execute();
        } else {
            binding.splashText.setVisibility(View.VISIBLE);
            binding.signIn.setVisibility(View.VISIBLE);
        }

        if (!sharedPreferences.getString("Background", "default").equals("default")) {
            String base64Str = sharedPreferences.getString("Background", "default");
            byte[] decodedBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",") + 1),
                    Base64.DEFAULT
            );
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            AccountConst.background = bitmap;
        }
    }

    private void signIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 123);
    }

    private void ErrorLoad()
    {
        if(!error)
        {
            Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
            error = true;
            binding.buttonRepeat.setVisibility(View.VISIBLE);
            binding.spinKit.setVisibility(View.GONE);
            binding.splashText.setVisibility(View.GONE);
            binding.buttonOffline.setVisibility(View.VISIBLE);
        }
    }

    private AsyncTask<Void, Void, Void> getData()
    {
        return new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPostExecute(Void unused) {
                LoadShifts();


                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Shifts").get().addOnCompleteListener(
                        new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                try {
                                    AccountConst.shiftsArrayList = new ArrayList<>();
                                    for (DataSnapshot child : task.getResult().getChildren()) {
                                        Shifts str1 = child.getValue(Shifts.class);
                                        AccountConst.shiftsArrayList.add(str1);
                                    }
                                } catch (Exception ex)
                                {
                                    ErrorLoad();
                                }
                            }
                        });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/CurrentScheduls").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            Long num = task.getResult().getValue(Long.class);
                            if (num != null)
                                HomeFragment.current_schedule = Math.toIntExact(num);
                        }
                        catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                Task<DataSnapshot> databaseReference = FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Scheduls").get();
                databaseReference.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                ScheduleFB scheduleFB = child.getValue(ScheduleFB.class);
                                String data = null;
                                if (scheduleFB != null)
                                    data = scheduleFB.getData();

                                byte[] data2 = Base64.decode(data, Base64.DEFAULT);
                                Schedule schedule = SerializationUtils.deserialize(data2);
                                schedule.getScheduleDayArrayList();
                                HomeFragment.scheduls.add(schedule);
                            }
                            byte[] data = SerializationUtils.serialize(HomeFragment.scheduls.get(HomeFragment.current_schedule));
                            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            editor.putString("Schedule", base64);
                            editor.apply();
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.nav_home);
                        }
                        catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/Board").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            String check = task.getResult().getValue(String.class);
                            if (check != null) {
                                if (check.equals("true"))
                                {
                                    AccountConst.board = true;
                                    editor.putBoolean("Board", true);
                                    editor.apply();
                                }
                                else {
                                    AccountConst.board = false;
                                    editor.putBoolean("Board", false);
                                    editor.apply();
                                }
                            } else {
                                task.getResult().getRef().setValue("true");
                                AccountConst.board = true;
                                editor.putBoolean("Board", true);
                                editor.apply();
                            }
                        }
                        catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorCalendar").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            String color = task.getResult().getValue(String.class);
                            if (color != null) {
                                AccountConst.text_color_calendar = color;
                                editor.putString("TextColorCalendar", color);
                                editor.apply();
                            }
                            else {
                                task.getResult().getRef().setValue("#FF000000");
                                AccountConst.text_color_calendar = "#FF000000";
                                editor.putString("TextColorCalendar", "#FF000000");
                                editor.apply();
                            }
                        }
                        catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/ColorBorder").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {


                            String color = task.getResult().getValue(String.class);
                            if (color != null) {
                                AccountConst.color_Border = color;
                                editor.putString("ColorBorder", color);
                                editor.apply();
                            }
                            else {
                                task.getResult().getRef().setValue("#FFDDDDDD");
                                AccountConst.color_Border = "#FFDDDDDD";
                                editor.putString("ColorBorder", "#FFDDDDDD");
                                editor.apply();
                            }
                        }catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/DaysOther").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            String check = task.getResult().getValue(String.class);
                            if (check != null) {
                                if (check.equals("true")) {
                                    AccountConst.days_other = true;
                                    editor.putBoolean("DaysOther", true);
                                    editor.apply();
                                } else {
                                    AccountConst.days_other = false;
                                }
                            } else {
                                task.getResult().getRef().setValue("false");
                                AccountConst.days_other = false;
                                editor.putBoolean("DaysOther", false);
                                editor.apply();
                            }
                        }catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });


                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorShift").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            String color = task.getResult().getValue(String.class);
                            if (color != null) {
                                AccountConst.text_color_shift = color;
                                editor.putString("TextColorShift", color);
                                editor.apply();
                            }
                            else {
                                task.getResult().getRef().setValue("#FFFFFFFF");
                                AccountConst.text_color_shift = "#FFFFFFFF";
                                editor.putString("TextColorShift", "#FFFFFFFF");
                                editor.apply();
                            }
                        }
                        catch (Exception ex)
                        {
                            ErrorLoad();
                        }
                    }
                });

                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/SizeTextShift").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            Long num = task.getResult().getValue(Long.class);
                            if (num != null)
                                AccountConst.size_text_shift = Math.toIntExact(num);
                            else
                                task.getResult().getRef().setValue(14);
                        }catch (Exception ex)
                        {
                            ErrorLoad();
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
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toasty.error(getActivity(), e.toString(), Toasty.LENGTH_SHORT).show();
            }
        }
    }

    public void LoadShifts()
    {
        Task<DataSnapshot> dataSnapshotTask =  FirebaseDatabase
                .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                .getReference()
                .child("Users/").get();

        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                boolean exist = false;
                try {
                    for (DataSnapshot child : task.getResult().getChildren()) {
                        if (child.getKey().toString().equals(AccountConst.account.getUid()))
                            exist = true;
                    }
                    if (!exist) {
                        DatabaseReference databaseReference = FirebaseDatabase
                                .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                .getReference()
                                .child("Users/" + AccountConst.account.getUid() + "/Shifts");
                        databaseReference.push()
                                .setValue(new Shifts(1, getActivity().getResources().getString(R.string.day_sh), "07:00", "19:00", "#fcba03", false));
                        databaseReference.push()
                                .setValue(new Shifts(2, getActivity().getResources().getString(R.string.night_sh), "19:00", "07:00", "#0339fc", false));
                        databaseReference.push()
                                .setValue(new Shifts(3, getActivity().getResources().getString(R.string.offday_sh), "00:00", "00:00", "#00d4d0", true));
                    }
                }
                catch (Exception ex)
                {
                    ErrorLoad();
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UpdateUI(user);
                            binding.splashText.setVisibility(View.GONE);
                            binding.signIn.setVisibility(View.GONE);
                            binding.spinKit.setVisibility(View.VISIBLE);
                            editor.putString("SignIN", "Yes");
                            editor.apply();
                        } else {
                            UpdateUI(null);
                        }
                    }
                });
    }
    private void UpdateUI(FirebaseUser account)  {
        TextView name = getActivity().findViewById(R.id.name_account);
        name.setText(account.getDisplayName());
        TextView email = getActivity().findViewById(R.id.email_account);
        email.setText(account.getEmail());
        ImageView imageView = getActivity().findViewById(R.id.photo_account);
        if(account.getPhotoUrl() != null) {
            Glide.with(getActivity()).load(account.getPhotoUrl().toString())
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        AccountConst.account = account;
    }




}