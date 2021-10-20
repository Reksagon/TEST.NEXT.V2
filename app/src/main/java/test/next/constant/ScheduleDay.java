package test.next.constant;

import java.io.Serializable;

public class ScheduleDay implements Serializable {
    private int day, month, year;
    private Shifts shift;

    public ScheduleDay(int day, int month, int year, Shifts shift) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.shift = shift;

    }

    public Shifts getShifts()
    {
        return shift;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Shifts getShift() {
        return shift;
    }

    public void setShift(Shifts shift) {
        this.shift = shift;
    }


}
