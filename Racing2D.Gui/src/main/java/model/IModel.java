/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import repositories.races.RaceInfo;
import exceptions.LoginException;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public interface IModel {

    void deleteGhost(int gid);

    ArrayList<GhostInfo> getGhostInfos();

    ArrayList<GhostTraject> getGhostTrajects();

    Time getPersonelBestTime();

    RaceInfo getRaceInfo();

    ArrayList<RaceInfo> getRaceList(int aantal);

    FormattedTile[][] getRaceTrack();

    Point getStartLocation();

    ArrayList<Time> getRankings(int topN);

    void saveGhost(GhostTraject ghost);

    void saveTime(int ms);

    void selectGhostIds(ArrayList<Integer> gids);

    void setRaceInfo(RaceInfo raceInfo);
    
}
