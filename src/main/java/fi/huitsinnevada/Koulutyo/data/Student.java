package fi.huitsinnevada.Koulutyo.data;

public class Student {
    String id = "dummy";
    String name = "Pertti Eräreikä";
    int points = 0;
    // More data can be added when needed, but for now we have just the name and overall points.
    public Student(String id, String name, int points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
