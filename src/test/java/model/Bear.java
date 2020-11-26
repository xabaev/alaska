package model;

import com.sun.istack.Nullable;

import java.util.Objects;

public class Bear {
    private Integer bear_id;
    private String bear_type;
    private String bear_name;
    private Double bear_age;

    public Bear(@Nullable String type, @Nullable String name, @Nullable Double age) {
        this.bear_type = type;
        this.bear_name = name;
        this.bear_age = age;
    }

    public Bear(@Nullable Integer bear_id, @Nullable String type, @Nullable String name, @Nullable Double age) {
        this.bear_id = bear_id;
        this.bear_type = type;
        this.bear_name = name;
        this.bear_age = age;
    }

    public String getBear_type() {
        return bear_type;
    }

    public void setBear_type(@Nullable String bear_type) {
        this.bear_type = bear_type;
    }

    public Double getBear_age() {
        return bear_age;
    }

    public void setBear_age(@Nullable Double bear_age) {
        this.bear_age = bear_age;
    }

    public String getBear_name() {
        return bear_name;
    }

    public void setBear_name(@Nullable String bear_name) {
        this.bear_name = bear_name;
    }

    public Integer getBear_id() {
        return bear_id;
    }

    public void setBear_id(@Nullable Integer bear_id) {
        this.bear_id = bear_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || this.hashCode() != obj.hashCode()) return false;
        Bear myObject = (Bear) obj;
        return Objects.equals(bear_id, myObject.bear_id) &&
                Objects.equals(bear_type, myObject.bear_type) &&
                Objects.equals(bear_name, myObject.bear_name) &&
                Objects.equals(bear_age, myObject.bear_age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bear_id, bear_type, bear_name, bear_age);
    }

    public String toString() {
        return String.format("bear_id = %s, bear_type = %s, bear_name = %s, bear_age = %f", bear_id, bear_type, bear_name, bear_age);
    }
}
