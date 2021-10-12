package test.next.ui.create;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ozcanalasalvar.library.utils.DateUtils;
import com.ozcanalasalvar.library.view.datePicker.DatePicker;
import com.ozcanalasalvar.library.view.popup.DatePickerPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import test.next.databinding.FragmentCreateBinding;

public class CreateViewModel extends ViewModel {

    private MutableLiveData<View.OnClickListener> mDateClick;
    private Activity activity;
    private FragmentCreateBinding binding;

    public void setActivity(Activity activity, FragmentCreateBinding binding)
    {
        this.activity = activity;
        this.binding = binding;
    }


    public CreateViewModel() {

    }

    public void CreateView()
    {

        mDateClick = new MutableLiveData<>();
        mDateClick.setValue(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] split = binding.date.getText().split("/");
                Calendar calendar = Calendar.getInstance();
                DatePickerPopup datePickerPopup = new DatePickerPopup.Builder()
                        .from(activity)
                        .offset(3)
                        .pickerMode(DatePicker.MONTH_ON_FIRST)
                        .textSize(19)
                        .startDate(DateUtils.getTimeMiles(calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.MONTH), 1))
                        .endDate(DateUtils.getTimeMiles(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
                        .currentDate(DateUtils.getTimeMiles(Integer.parseInt(split[0]), Integer.parseInt(split[1])-1, Integer.parseInt(split[2])))
                        .listener(new DatePickerPopup.OnDateSelectListener() {
                            @Override
                            public void onDateSelected(DatePicker dp, long date, int day, int month, int year) {
                                int m = month + 1;
                                binding.date.setText(year + "/" + m + "/" + day);
                            }
                        })
                        .build();
                datePickerPopup.show();
            }
        });
    }

    public MutableLiveData<View.OnClickListener> getmDateClick() {
        return mDateClick;
    }
}