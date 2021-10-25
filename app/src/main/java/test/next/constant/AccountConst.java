package test.next.constant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

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
//        if (AccountConst.mInterstitialAd != null) {
//            AccountConst.mInterstitialAd.show(activity);
//            LoadAd();
//        } else {
//            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//        }
    }

    public static void loadAdView(AdView adView)
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
