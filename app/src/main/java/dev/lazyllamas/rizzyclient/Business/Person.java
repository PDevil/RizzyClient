package dev.lazyllamas.rizzyclient.Business;

public class Person {

    private String name;
    private int age;
    private String description;
    private double lon;
    private double lat;

    public Person(String name, int age, String description, double lat, double lon) {
        this.name = name;
        this.age = age;
        this.description = description;
        this.lon = lon;
        this.lat = lat;

    }

    public double getLon() {

        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
