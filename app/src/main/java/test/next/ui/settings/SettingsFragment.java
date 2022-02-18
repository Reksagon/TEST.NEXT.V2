package test.next.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import net.igenius.customcheckbox.CustomCheckBox;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.databinding.FragmentSettingsBinding;
import test.next.ui.calendar.DayCalendarDialogFragment;
import test.next.ui.home.HomeFragment;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Start();
        AccountConst.ShowAd();
        AccountConst.loadAdView(binding.adView2);
        binding.buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 777);

            }
        });

        getReviewInfo();
        startReviewFlow();
        binding.checkBoard.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                try {

                    FirebaseDatabase
                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                            .getReference()
                            .child("Users/" + AccountConst.account.getUid() + "/Settings/Board").setValue(String.valueOf(isChecked));
                    AccountConst.board = isChecked;
                }catch (Exception ex)
                {
                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                }
            }
        });

        binding.checkDaysOther.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                try {
                    FirebaseDatabase
                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                            .getReference()
                            .child("Users/" + AccountConst.account.getUid() + "/Settings/DaysOther").setValue(String.valueOf(isChecked));
                    AccountConst.days_other = isChecked;
                }catch (Exception ex)
                {
                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                }
            }
        });

        binding.colorPickTextShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .initialColor(Color.parseColor(AccountConst.text_color_shift))
                        .setTitle(getActivity().getResources().getString(R.string.choose_color))
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
                                try {
                                    binding.colorPickTextShift.setBackgroundColor(selectedColor);
                                    AccountConst.text_color_shift = String.format("#%06X", (0xFFFFFF & selectedColor));
                                    FirebaseDatabase
                                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                            .getReference()
                                            .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorShift").setValue(AccountConst.text_color_shift);
                                }catch (Exception ex)
                                {
                                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        binding.colorBorderPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .initialColor(Color.parseColor(AccountConst.color_Border))
                        .setTitle(getActivity().getResources().getString(R.string.choose_color))
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
                                try {
                                    binding.colorBorderPick.setBackgroundColor(selectedColor);
                                    AccountConst.color_Border = String.format("#%06X", (0xFFFFFF & selectedColor));
                                    FirebaseDatabase
                                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                            .getReference()
                                            .child("Users/" + AccountConst.account.getUid() + "/Settings/ColorBorder").setValue(AccountConst.color_Border);
                                }catch (Exception ex)
                                {
                                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        binding.textSizeShift.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                try {
                    switch (i1) {
                        case 0:
                            AccountConst.size_text_shift = 11;
                            break;
                        case 1:
                            AccountConst.size_text_shift = 14;
                            break;
                        case 2:
                            AccountConst.size_text_shift = 17;
                            break;

                    }
                    FirebaseDatabase
                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                            .getReference()
                            .child("Users/" + AccountConst.account.getUid() + "/Settings/SizeTextShift").setValue(AccountConst.size_text_shift);
                }catch (Exception ex)
                {
                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_home);
            }
        });

        binding.colorPickText.setClickable(false);
        binding.colorPickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .initialColor(Color.parseColor(AccountConst.text_color_calendar))
                        .setTitle(getActivity().getResources().getString(R.string.choose_color))
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
                                try {
                                    binding.colorPickText.setBackgroundColor(selectedColor);
                                    AccountConst.text_color_calendar = String.format("#%06X", (0xFFFFFF & selectedColor));
                                    FirebaseDatabase
                                            .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                            .getReference()
                                            .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorCalendar").setValue(AccountConst.text_color_calendar);
                                } catch (Exception ex)
                                {
                                    Toasty.error(getActivity(), getString(R.string.error_load), Toasty.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    private void getReviewInfo() {
        reviewManager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });
    }

    public void startReviewFlow()
    {
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
        }
        else {

            //Toasty.error(getActivity(), "In App Rating failed", Toasty.LENGTH_SHORT).show();

        }
    }

    private void Start()
    {
        binding.checkBoard.setChecked(AccountConst.board);
        binding.checkDaysOther.setChecked(AccountConst.days_other);
        binding.colorPickText.setBackgroundColor(Color.parseColor(AccountConst.text_color_calendar));
        binding.colorPickTextShift.setBackgroundColor(Color.parseColor(AccountConst.text_color_shift ));
        binding.colorBorderPick.setBackgroundColor(Color.parseColor(AccountConst.color_Border ));
        ArrayList<String> text_size = new ArrayList<>();
        text_size.add(getActivity().getResources().getString(R.string.small));
        text_size.add(getActivity().getResources().getString(R.string.medium));
        text_size.add(getActivity().getResources().getString(R.string.big));
        binding.textSizeShift.setItems(text_size);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;

        switch(requestCode) {
            case 777:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ProgressDialog progressDialog = new ProgressDialog();
                    progressDialog.show(fm, "fragment_edit_name");

                    AccountConst.background = bitmap;
                    Bitmap finalBitmap = bitmap;
                    new AsyncTask<Void, Void, Void>()
                    {
                        @Override
                        protected void onPostExecute(Void unused) {
                            Toasty.success(getActivity(), getActivity().getResources().getString(R.string.loading_succes), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            super.onPostExecute(unused);
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream .toByteArray();

                            String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ShiftSchedulePlus", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Background", base64);
                            editor.apply();
                            return null;
                        }
                    }.execute();

                }
        }

    }

}