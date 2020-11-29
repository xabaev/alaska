package model;

import com.google.gson.annotations.SerializedName;
import com.sun.istack.Nullable;

import java.util.Objects;

public class Bear {
    @SerializedName("bear_id")
    private Integer bearId;

    @SerializedName("bear_type")
    private String bearType;

    @SerializedName("bear_name")
    private String bearName;

    @SerializedName("bear_age")
    private Double bearAge;

    public Bear(@Nullable String type, @Nullable String name, @Nullable Double age) {
        this.bearType = type;
        this.bearName = name;
        this.bearAge = age;
    }

    public Bear(@Nullable Integer bearId, @Nullable String type, @Nullable String name, @Nullable Double age) {
        this.bearId = bearId;
        this.bearType = type;
        this.bearName = name;
        this.bearAge = age;
    }


    public String getBearType() {
        return bearType;
    }

    public void setBearType(@Nullable String bearType) {
        this.bearType = bearType;
    }

    public Double getBearAge() {
        return bearAge;
    }

    public void setBearAge(@Nullable Double bearAge) {
        this.bearAge = bearAge;
    }

    public String getBearName() {
        return bearName;
    }

    public void setBearName(@Nullable String bearName) {
        this.bearName = bearName;
    }

    public Integer getBearId() {
        return bearId;
    }

    public void setBearId(@Nullable Integer bearId) {
        this.bearId = bearId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || this.hashCode() != obj.hashCode()) return false;
        Bear myObject = (Bear) obj;
        return Objects.equals(bearId, myObject.bearId) &&
                Objects.equals(bearType, myObject.bearType) &&
                Objects.equals(bearName, myObject.bearName) &&
                Objects.equals(bearAge, myObject.bearAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bearId, bearType, bearName, bearAge);
    }

    public String toString() {
        return String.format("bear_id = %s, bear_type = %s, bear_name = %s, bear_age = %f", bearId, bearType, bearName, bearAge);
    }
}
