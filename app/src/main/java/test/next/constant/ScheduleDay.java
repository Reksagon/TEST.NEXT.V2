package test.next.constant;

import java.io.Serializable;

public class ScheduleDay implements Serializable {
    private int day, month, year;
    private Shifts shift;
    private String start, end, start_lanch, end_lanch;
    private boolean offday;

    public ScheduleDay(int day, int month, int year, Shifts shift) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.shift = shift;
        start = shift.getStart();
        end = shift.getEnd();
        start_lanch = shift.getStart_lanch();
        end_lanch = shift.getEnd_lanch();
        offday = shift.isOffday();
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart_lanch() {
        return start_lanch;
    }

    public void setStart_lanch(String start_lanch) {
        this.start_lanch = start_lanch;
    }

    public String getEnd_lanch() {
        return end_lanch;
    }

    public void setEnd_lanch(String end_lanch) {
        this.end_lanch = end_lanch;
    }

    public boolean isOffday() {
        return offday;
    }

    public void setOffday(boolean offday) {
        this.offday = offday;
    }
}
