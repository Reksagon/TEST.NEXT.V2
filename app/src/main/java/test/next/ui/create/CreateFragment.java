package test.next.ui.create;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ozcanalasalvar.library.utils.DateUtils;
import com.ozcanalasalvar.library.view.datePicker.DatePicker;
import com.ozcanalasalvar.library.view.popup.DatePickerPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import test.next.R;
import test.next.databinding.FragmentCreateBinding;

public class CreateFragment extends Fragment {

    private CreateViewModel createViewModel;
    private FragmentCreateBinding binding;

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


        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}