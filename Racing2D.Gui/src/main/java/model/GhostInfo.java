/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 * collection of info about a Ghost that is copied from the database
 * and used in the ghost overview
 * 
 * @author Lando
 */
public class GhostInfo {
    private int gid;
    private int uid;
    private String pilot;
    private Time time;

    public int getGid() {
        return gid;
    }

    public int getUid() {
        return uid;
    }

    public String getPilot() {
        return pilot;
    }

    public GhostInfo(int gid, int uid, String pilot, Time time) {
        this.gid = gid;
        this.uid = uid;
        this.pilot = pilot;
        this.time = time;
    }

    public String getTime() {
        return time.toString();
    }
}
