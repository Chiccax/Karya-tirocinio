package com.example.tirocinio;

import java.time.LocalDateTime;

public class Fire {

    private static String actual_location;
    private static String fire_location;
    private static String photo;
    private static boolean industrial_area;
    private static boolean waste_dump;
    private static boolean city_center;
    private static boolean trusses;
    private static boolean gas_pipeline;
    private static boolean bell_tower;
    private static String more_info;
    private static String dateTime;

    public Fire(String actual_location, String fire_location, boolean industrial_area, boolean waste_dump, boolean city_center, boolean trusses, boolean gas_pipeline, boolean bell_tower, String more_info, String dateTime) {
        this.actual_location = actual_location;
        this.fire_location = fire_location;
        this.industrial_area = industrial_area;
        this.waste_dump = waste_dump;
        this.city_center = city_center;
        this.trusses = trusses;
        this.gas_pipeline = gas_pipeline;
        this.bell_tower = bell_tower;
        this.more_info = more_info;
        this.dateTime = dateTime;
    }

    public static String getActual_location() {
        return actual_location;
    }

    public static void setActual_location(String actual_location) {
        Fire.actual_location = actual_location;
    }

    public static String getFire_location() {
        return fire_location;
    }

    public static void setFire_location(String fire_location) {
        Fire.fire_location = fire_location;
    }

    public static String getPhoto() {
        return photo;
    }

    public static void setPhoto(String photo) {
        Fire.photo = photo;
    }

    public static boolean isIndustrial_area() {
        return industrial_area;
    }

    public static void setIndustrial_area(boolean industrial_area) {
        Fire.industrial_area = industrial_area;
    }

    public static boolean isWaste_dump() {
        return waste_dump;
    }

    public static void setWaste_dump(boolean waste_dump) {
        Fire.waste_dump = waste_dump;
    }

    public static boolean isCity_center() {
        return city_center;
    }

    public static void setCity_center(boolean city_center) {
        Fire.city_center = city_center;
    }

    public static boolean isTrusses() {
        return trusses;
    }

    public static void setTrusses(boolean trusses) {
        Fire.trusses = trusses;
    }

    public static boolean isGas_pipeline() {
        return gas_pipeline;
    }

    public static void setGas_pipeline(boolean gas_pipeline) {
        Fire.gas_pipeline = gas_pipeline;
    }

    public static boolean isBell_tower() {
        return bell_tower;
    }

    public static void setBell_tower(boolean bell_tower) {
        Fire.bell_tower = bell_tower;
    }

    public static String getMore_info() {
        return more_info;
    }

    public static void setMore_info(String more_info) {
        Fire.more_info = more_info;
    }

    public static String getDateTime() {
        return dateTime;
    }

    public static void setDateTime(String dateTime) {
        Fire.dateTime = dateTime;
    }
}
