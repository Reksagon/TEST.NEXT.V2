package test.next.constant;

import java.io.Serializable;

public class Shifts implements Serializable {
    private int id;

    public int getId() {
        return id;
    }

    private String name;
    private String start, end, start_lanch, end_lanch;
    private String color;

    public void setId(int id) {
        this.id = id;
    }

    private boolean offday;

    public boolean isOffday() {
        return offday;
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

    public void setOffday(boolean offday) {
        this.offday = offday;
    }

    public Shifts(int id, String name, String start, String end, String color, boolean offday) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.color = color;
        this.offday = offday;
        start_lanch = "00:00";
        end_lanch = "00:00";

    }

    public Shifts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
