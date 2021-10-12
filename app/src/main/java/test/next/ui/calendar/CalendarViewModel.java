package test.next.ui.calendar;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import test.next.databinding.CalendarFragmentBinding;

public class CalendarViewModel extends ViewModel {
    CalendarFragmentBinding binding;
    MutableLiveData<TextView[]> textViewMutableLiveData;

    public void setBinding(CalendarFragmentBinding binding) {
        this.binding = binding;
    }

    public void setTextViewMutableLiveData() {
        textViewMutableLiveData = new MutableLiveData<>();
        TextView[] textViews = {binding.day1Text, binding.day2Text, binding.day3Text, binding.day4Text,
                binding.day5Text, binding.day6Text, binding.day7Text, binding.day8Text, binding.day9Text,
                binding.day10Text, binding.day11Text, binding.day12Text, binding.day13Text, binding.day14Text,
                binding.day15Text, binding.day16Text, binding.day17Text, binding.day18Text, binding.day19Text,
                binding.day20Text, binding.day21Text, binding.day22Text, binding.day23Text, binding.day24Text,
                binding.day25Text, binding.day26Text, binding.day27Text, binding.day28Text, binding.day29Text,
                binding.day30Text, binding.day31Text, binding.day32Text, binding.day34Text, binding.day35Text,
                binding.day36Text, binding.day37Text, binding.day38Text, binding.day39Text, binding.day40Text,
                binding.day41Text, binding.day42Text };
        textViewMutableLiveData.setValue(textViews);
    }

    public CalendarViewModel(){

    }

    public MutableLiveData<TextView[]> getTextViewMutableLiveData() {
        return textViewMutableLiveData;
    }
}
