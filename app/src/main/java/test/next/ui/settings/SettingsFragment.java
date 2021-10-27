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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import net.igenius.customcheckbox.CustomCheckBox;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import es.dmoral.toasty.Toasty;
import test.next.R;
import test.next.constant.AccountConst;
import test.next.databinding.FragmentSettingsBinding;
import test.next.ui.calendar.DayCalendarDialogFragment;
import test.next.ui.home.HomeFragment;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

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

        binding.checkBoard.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/Board").setValue(String.valueOf(isChecked));
                AccountConst.board = isChecked;
            }
        });

        binding.checkDaysOther.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                FirebaseDatabase
                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                        .getReference()
                        .child("Users/" + AccountConst.account.getUid() + "/Settings/DaysOther").setValue(String.valueOf(isChecked));
                AccountConst.days_other = isChecked;
            }
        });

        binding.colorPickTextShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(getActivity())
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
                                binding.colorPickTextShift.setBackgroundColor(selectedColor);
                                AccountConst.text_color_shift = String.format("#%06X", (0xFFFFFF & selectedColor));
                                FirebaseDatabase
                                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                        .getReference()
                                        .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorShift").setValue(AccountConst.text_color_shift);
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
                                binding.colorPickText.setBackgroundColor(selectedColor);
                                AccountConst.text_color_calendar = String.format("#%06X", (0xFFFFFF & selectedColor));
                                FirebaseDatabase
                                        .getInstance(new String(Base64.decode(getActivity().getResources().getString(R.string.firebase), Base64.DEFAULT)))
                                        .getReference()
                                        .child("Users/" + AccountConst.account.getUid() + "/Settings/TextColorCalendar").setValue(AccountConst.text_color_calendar);
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

    private void Start()
    {
        binding.checkBoard.setChecked(AccountConst.board);
        binding.checkDaysOther.setChecked(AccountConst.days_other);
        binding.colorPickText.setBackgroundColor(Color.parseColor(AccountConst.text_color_calendar));
        binding.colorPickTextShift.setBackgroundColor(Color.parseColor(AccountConst.text_color_shift ));
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