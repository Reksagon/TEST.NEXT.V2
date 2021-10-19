package test.next.constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Schedule implements Serializable {

    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ArrayList<ScheduleDay> scheduleDayArrayList = new ArrayList<>();

    public void changeShift(Shifts new_s)
    {
        ArrayList<ScheduleDay> new_schedule = new ArrayList<>();
        for(ScheduleDay scheduleDay : scheduleDayArrayList)
        {
            if(scheduleDay.getShift().getId() == new_s.getId())
            {
                ScheduleDay tmp = scheduleDay;
                tmp.setShift(new_s);
                new_schedule.add(tmp);
            }
            else
            {
                new_schedule.add(scheduleDay);
            }
        }
    }

    public Schedule(int id, ArrayList<Shifts> shifts, ArrayList<Integer> days, Calendar day, String name)
    {
        this.name = name;
        this.id = id;
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
