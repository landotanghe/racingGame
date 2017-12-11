/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import repositories.races.RaceInfo;
import data.ConnectionFactory;
import repositories.ghosts.GhostRepository;
import repositories.ghosts.IGhostRepository;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import repositories.races.FakeRaceRepository;
import repositories.races.IRaceRepository;
import repositories.races.RaceRepository;
import repositories.racetrack.FakeRaceTrackRepository;
import repositories.racetrack.IRaceTrackRepository;
import repositories.rankings.IRankingsRepository;
import repositories.rankings.RankingsRepository;

/**
 *
 * contains data that is exchanged regularly between modules within the program
 * and communicates with the DB to store and load data
 *
 * @author Lando
 */
public class Model implements IModel {

    private ResourceBundle resources;
    private User user;
    private String password;
    private ArrayList<Integer> _ghostIds;
    private final ConnectionFactory _connectionFactory;
    private final IRaceTrackRepository _raceTrackRepository;
    private final IRaceRepository _raceRepository;
    private final IRankingsRepository _rankingsRepository;
    private final IGhostRepository _ghostRepository;

    private Point startLocation;

    private RaceInfo raceInfo;

    public Model() throws Exception {
        _ghostIds = new ArrayList<Integer>();
        _connectionFactory = new ConnectionFactory();
        //_raceTrackRepository = new RaceTrackRepository(_connectionFactory);
        _raceTrackRepository = new FakeRaceTrackRepository();
        _raceRepository = new RaceRepository();
        _rankingsRepository = new RankingsRepository(_connectionFactory);
        _ghostRepository = new GhostRepository(_connectionFactory);
        
        try{
            Connection con = _connectionFactory.getConnection();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public ArrayList<RaceInfo> getRaceList(int count) {
        RaceInfo[] array = _raceRepository.getRaces(count);
        return new ArrayList<RaceInfo>(Arrays.asList(array));
    }

    @Override
    public ArrayList<Time> getRankings(int topN) {
        return _rankingsRepository.getRankings(raceInfo.getId(), topN);
    }

    @Override
    public Time getPersonelBestTime() {
        return _rankingsRepository.getPersonalBestTime(raceInfo.getId(), user);
    }

    @Override
    public FormattedTile[][] getRaceTrack() {
        return _raceTrackRepository.getRaceTrack(raceInfo.getId());
    }

    @Override
    public Point getStartLocation() {
        return startLocation;
    }

    @Override
    public void setRaceInfo(RaceInfo raceInfo) {
        this.raceInfo = raceInfo;
    }

    @Override
    public void saveGhost(GhostTraject ghost) {
        _ghostRepository.saveGhost(raceInfo.getId(), user, ghost);
    }

    @Override
    public void deleteGhost(int ghostId) {
        _ghostRepository.deleteGhost(ghostId);
    }

    @Override
    public ArrayList<GhostTraject> getGhostTrajects() {
        return _ghostRepository.getGhostTrajects(_ghostIds);
    }


    @Override
    public ArrayList<GhostInfo> getGhostInfos() {
        return _ghostRepository.getGhostInfos(raceInfo.getId());
    }

    @Override
    public void saveTime(int ms) {
        _rankingsRepository.savePersonalBestTime(raceInfo.getId(), user.getId(), ms);
    }

    @Override
    public void selectGhostIds(ArrayList<Integer> gids) {
        this._ghostIds = gids;
    }

    @Override
    public RaceInfo getRaceInfo() {
        return raceInfo;
    }

}
