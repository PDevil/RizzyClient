package dev.lazyllamas.rizzyclient.Business;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import dev.lazyllamas.rizzyclient.MainTabbedActivity;

public class Person implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    private String name;
    private Date age;

    private String description;

    @SerializedName("longitude")
    private double lon;
    @SerializedName("latitude")
    private double lat;

    private Activities currentActivities;
    private ArrayList<Activities> likedActivities = new ArrayList<>(2);

    private Bitmap image;

    @SerializedName("storage_id")
    private String id;

    public Person(double lat, double lon) {
        this.lon = lon;
        this.lat = lat;
    }

    public Person(String name, Date age, String description, double lat, double lon, Activities currentActivities, ArrayList<Activities> likedActivities, Bitmap image, String id) {
        this.name = name;
        this.age = age;
        this.description = description;
        this.lon = lon;
        this.lat = lat;
        this.currentActivities = currentActivities;
        this.likedActivities = likedActivities;
        this.image = image;
        this.id = id;

    }

    public Person(Parcel in) {
        readFromParcel(in);
    }


    public double getLon() {

        return lon;
    }

    public Activities getCurrentActivities() {

        return currentActivities;
    }

    public void setCurrentActivities(Activities currentActivities) {
        this.currentActivities = currentActivities;
    }

    public ArrayList<Activities> getLikedActivities() {
        return likedActivities;
    }

    public void setLikedActivities(ArrayList<Activities> likedActivities) {
        this.likedActivities = likedActivities;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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

    public Date getAge() {
        return age;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(age.getTime() / 1000);
        dest.writeString(description);
        dest.writeDouble(lon);
        dest.writeDouble(lat);


        dest.writeInt(currentActivities.ordinal());
        dest.writeInt(likedActivities.size());
        for (Activities act : likedActivities)
            dest.writeInt(act.ordinal());

        dest.writeString(id);

        File cacheDir = MainTabbedActivity.mainContext.getCacheDir();
        File f = new File(cacheDir, id);

        try {
            FileOutputStream out = new FileOutputStream(
                    f);
            image.compress(
                    Bitmap.CompressFormat.JPEG,
                    100, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        long tmp = in.readLong();
        age = new java.util.Date(tmp * 1000);
        description = in.readString();
        lon = in.readDouble();
        lat = in.readDouble();

        currentActivities = Activities.values()[in.readInt()];
        likedActivities = new ArrayList<>();
        int tmp2 = in.readInt();

        for (int i = 0; i < tmp2; i++)
            likedActivities.add(Activities.values()[in.readInt()]);

        id = in.readString();

    }

    public enum Activities {
        Running,
        Cycling,
        Skateboarding,
        NordicWalking

    }
}
