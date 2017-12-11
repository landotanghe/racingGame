/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package repositories.races;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  collection of info that is loaded from the database and locally stored
 *  used by RaceOverviewPanel
 * 
 * @author Lando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RaceInfo {
    @JsonProperty("Id")
    private int id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Creator")
    private String creator;
    
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
    
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
