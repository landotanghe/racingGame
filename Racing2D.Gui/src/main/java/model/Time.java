/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * used both as a technical class: used to make formatting the time easier
 * 
 * and as a class to represent the time of user locally 
 * used by TopTimesPanel
 * @author Lando
 */
public class Time {

    //paren van user en tijd (in s of ms?)
    private String username;
    private int userId;
    private int miliseconds;
    private int seconds;
    private int minutes;

    public Time(String username, int userId, int miliseconds) {
        this.username = username;
        this.userId = userId;
        this.miliseconds = miliseconds % 1000; //max 999
        seconds = miliseconds / 1000;   //tot 
        this.minutes = seconds / 60; //tot, limiet??
        seconds %= 60; // max 59
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMiliseconds() {
        return miliseconds;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public String toString() {
        if (miliseconds <= 0) {
            return "";
        } else {
            String format = "%02d:%02d.%03d";
            return String.format(format, minutes, seconds, miliseconds);
        }
    }
}
