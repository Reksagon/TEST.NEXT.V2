package test.next.constant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.ui.changes.ChangeAdapter;
import test.next.ui.home.HomeFragment;

public class AccountConst {
    public static FirebaseUser account;
    public static ArrayList<Shifts> shiftsArrayList;
    public static Bitmap background;
    public static boolean board = false;
    public static String text_color_calendar = null;
    public static String text_color_shift = null;
    public static boolean days_other = false;
    public static InterstitialAd mInterstitialAd;
    public static Activity activity;
    public static String color_Border = null;
    public static int size_text_shift = 14;
    public static boolean offline = false;

    public static void LoadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        AccountConst.mInterstitialAd.load(activity,"ca-app-pub-4885903693260597/8062039849", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        AccountConst.mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        AccountConst.mInterstitialAd = null;
                    }
                });
    }

    public static void ShowAd()
    {
        if (AccountConst.mInterstitialAd != null) {
            AccountConst.mInterstitialAd.show(activity);
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected void onPostExecute(Void unused) {
                    LoadAd();
                    super.onPostExecute(unused);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        TimeUnit.SECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public static void loadAdView(AdView adView)
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static void deleteSchedule(int id, ChangeAdapter adapter)
    {
        Task<DataSnapshot> databaseReference = FirebaseDatabase
                .getInstance(new String(Base64.decode(activity.getResources().getString(R.string.firebase), Base64.DEFAULT)))
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

                        if (schedule.getId() == id) {
                            child.getRef().setValue(null);
                            adapter.deleteId(id);
                            Toasty.success(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                            HomeFragment.scheduls.remove(id - 1);
                            if (HomeFragment.current_schedule >= HomeFragment.scheduls.size()) {
                                HomeFragment.current_schedule = HomeFragment.scheduls.size() - 1;
                                FirebaseDatabase
                                        .getInstance(new String(Base64.decode(activity.getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                        .getReference()
                                        .child("Users/" + AccountConst.account.getUid() + "/Settings/CurrentScheduls").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        task.getResult().getRef().setValue(HomeFragment.current_schedule);
                                    }
                                });
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


    public static ArrayList<ScheduleDay> newSchedule(ArrayList<Shifts> shifts, ArrayList<Integer> days, Calendar day_start, Calendar day_end)
    {
        ArrayList<ScheduleDay> scheduleDayArrayList = new ArrayList<>();
        for(int i = 0; i < 730; i++)
        {
            for(int j = 0; j < days.size(); j++)
            {
                for(int g = 0; g < days.get(j); g++)
                {
                    scheduleDayArrayList.add(new ScheduleDay(day_start.get(Calendar.DAY_OF_MONTH),
                            day_start.get(Calendar.MONTH),
                            day_start.get(Calendar.YEAR), shifts.get(j)));
                    if(day_start.get(Calendar.DAY_OF_MONTH) == day_end.get(Calendar.DAY_OF_MONTH) &&
                            day_start.get(Calendar.MONTH) == day_end.get(Calendar.MONTH) &&
                            day_start.get(Calendar.YEAR) == day_end.get(Calendar.YEAR))
                    {
                        return scheduleDayArrayList ;
                    }
                    day_start.set(Calendar.DAY_OF_MONTH, day_start.get(Calendar.DAY_OF_MONTH) + 1);
                    i++;
                }

            }
        }

        return scheduleDayArrayList;
    }

}
