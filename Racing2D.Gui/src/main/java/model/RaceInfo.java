/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *  collection of info that is loaded from the database and locally stored
 *  used by RaceOverviewPanel
 * 
 * @author Lando
 */
public class RaceInfo {
    private int id;
    private String name;
    private String creatorName;//redundant, zodat creator niet verplicht bestaat in progr(geh. besparen)
                                
    //private User creator;//enkel maken in racedetails, relatie met race overbodig?
   // private Time time;

    public RaceInfo(int id, String name, String creatorName) {
        this.id = id;
        this.name = name;
        this.creatorName = creatorName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getCreatorName() {
        return creatorName;
    }
}
