package test.next.constant;

import java.io.Serializable;

public class Shifts implements Serializable {
    private String name;
    private String start, end;
    private String color;

    public Shifts(String name, String start, String end, String color) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.color = color;
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
