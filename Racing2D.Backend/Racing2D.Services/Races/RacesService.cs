using MongoDB.Driver;
using Racing2D.Services.Races;
using System.Collections.Generic;
using System.Linq;

namespace Racing2D.Services
{
    public class RacesService
    {
        public List<RaceInfo>  GetRaces()
        {
            var client = new MongoClient();//TODO pass in connection string with config file
            var db = client.GetDatabase("Racing");
            var tracks = db.GetCollection<Track>("tracks");

            var allTracks = tracks.Find(x => x.Id != "0").ToList();
            return allTracks.Select(t => new RaceInfo {
                Id = t.Id,
                Creator = "lando",
                Name = "test"
            }).ToList();
        }

        public Track GetRaceTrack(string id)
        {
            var client = new MongoClient();//TODO pass in connection string with config file
            var db = client.GetDatabase("Racing");
            var tracks = db.GetCollection<Track>("tracks");
            var track = tracks.Find(x => x.Id == id).FirstOrDefault();
            return track;
        }

        private static TileType[][] Dummy1()
        {
            return new TileType[][]
            {
                new TileType[] { TileType.SouthEast, TileType.SouthWest, TileType.NoRoads, TileType.NoRoads },
                new TileType[] { TileType.NorthEast, TileType.Crossroads, TileType.EastWest, TileType.SouthWest},
                new TileType[] { TileType.NoRoads, TileType.NorthEast, TileType.EastWest, TileType.NorthWest},
            };
        }

        public void SaveRaceTrack(Track track)
        {
            var client = new MongoClient();//TODO pass in connection string with config file
            var db = client.GetDatabase("Racing");
            var tracks = db.GetCollection<Track>("tracks");
            
            tracks.InsertOne(track);
        }

        private static TileType[][] Dummy2()
        {
            return new TileType[][]
            {
                new TileType[] { TileType.SouthEast, TileType.EastWest, TileType.SouthWest },
                new TileType[] { TileType.NorthEast, TileType.EastWest, TileType.NorthWest},
            };
        }
    }
}
