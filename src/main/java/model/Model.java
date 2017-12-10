/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import data.ConnectionFactory;
import ghosts.GhostRepository;
import ghosts.IGhostRepository;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import races.FakeRaceRepository;
import races.IRaceRepository;
import racetrack.FakeRaceTrackRepository;
import racetrack.IRaceTrackRepository;
import rankings.IRankingsRepository;
import rankings.RankingsRepository;

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
        _raceRepository = new FakeRaceRepository();
        _rankingsRepository = new RankingsRepository(_connectionFactory);
        _ghostRepository = new GhostRepository(_connectionFactory);
    }

    @Override
    public ArrayList<RaceInfo> getRaceList(int count) {
        return _raceRepository.getRaces(count);
    }

    @Override
    public ArrayList<Time> getRankings(int topN) {
        return _rankingsRepository.getRankings(raceInfo.getId(), topN);
    }

    @Override
    public Time getPersonelBestTime() {
        return _rankingsRepository.getPersonelBestTime(raceInfo.getId(), user);
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
        Logger.getLogger(Model.class.getName()).info("saving time...");
        try {
            Connection con = _connectionFactory.getConnection();
            try {
                con.setAutoCommit(false);
                PreparedStatement deleteOld = con.prepareStatement(resources.getString("delete_time"));
                deleteOld.setInt(1, raceInfo.getId());
                deleteOld.setInt(2, user.getId());
                deleteOld.execute();

                PreparedStatement insertNew = con.prepareStatement(resources.getString("insert_time"));
                insertNew.setInt(1, user.getId());
                insertNew.setInt(2, raceInfo.getId());
                insertNew.setInt(3, ms);
                insertNew.execute();
                con.commit();
                Logger.getLogger(Model.class.getName()).info("time succesfully saved");
            } catch (SQLException e) {
                con.rollback();
            } finally {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).warning("could not save time");
        }
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
