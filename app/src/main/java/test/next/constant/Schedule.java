package test.next.constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Schedule implements Serializable {


    ArrayList<ScheduleDay> scheduleDayArrayList = new ArrayList<>();

    public Schedule(ArrayList<Shifts> shifts, ArrayList<Integer> days, Calendar day)
    {
        for(int i = 0; i < 730; i++)
        {
            for(int j = 0; j < days.size(); j++)
            {
                for(int g = 0; g < days.get(j); g++)
                {
                    scheduleDayArrayList.add(new ScheduleDay(day.get(Calendar.DAY_OF_MONTH),
                            day.get(Calendar.MONTH),
                            day.get(Calendar.YEAR), shifts.get(j)));
                    day.set(Calendar.DAY_OF_MONTH, day.get(Calendar.DAY_OF_MONTH) + 1);
                    i++;
                }

            }

        }
    }
    public ArrayList<ScheduleDay> getScheduleDayArrayList() {
        return scheduleDayArrayList;
    }
    public void setScheduleDayArrayList(ArrayList<ScheduleDay> scheduleDayArrayList) {
        this.scheduleDayArrayList = scheduleDayArrayList;
    }
    public ArrayList<ScheduleDay> getMonth(int month, int year)
    {
        ArrayList<ScheduleDay> send = new ArrayList<ScheduleDay>();
        for(ScheduleDay day : scheduleDayArrayList)
        {
            if(day.getMonth() == month && day.getYear() == year)
                send.add(day);
        }
        return send;
    }
}
