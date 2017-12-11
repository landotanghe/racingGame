/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package repositories.races;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  collection of info that is loaded from the database and locally stored
 *  used by RaceOverviewPanel
 * 
 * @author Lando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RaceInfo {
    private int id;
    private String name;
    private String creator;//redundant, zodat creator niet verplicht bestaat in progr(geh. besparen)
                                
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    //private User creator;//enkel maken in racedetails, relatie met race overbodig?
    // private Time time;
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public RaceInfo() {
    }

    public RaceInfo(int id, String name, String creatorName) {
        this.id = id;
        this.name = name;
        this.creator = creatorName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getCreatorName() {
        return creator;
    }
}