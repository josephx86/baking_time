package io.github.josephx86.bakingtime;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeStep implements Parcelable{
    private int id;
    private String shortDescription, description, videoUrl, thumbnail;
    private RecipeStep next;

    public RecipeStep(int id, String shortDescription, String description, String videoUrl, String thumbnail) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnail = thumbnail;
    }

    protected RecipeStep(Parcel in) {
        id = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        thumbnail = in.readString();
    }

    public void setNextStep(RecipeStep rs) {
        next = rs;
    }

    public RecipeStep getNextStep() {
        return next;
    }

    public static final Creator<RecipeStep> CREATOR = new Creator<RecipeStep>() {
        @Override
        public RecipeStep createFromParcel(Parcel in) {
            return new RecipeStep(in);
        }

        @Override
        public RecipeStep[] newArray(int size) {
            return new RecipeStep[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoUrl);
        parcel.writeString(thumbnail);
    }
}
