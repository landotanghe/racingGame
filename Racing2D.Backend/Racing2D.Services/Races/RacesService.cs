using MongoDB.Driver;
using Racing2D.Services.Races;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Threading.Tasks;

namespace Racing2D.Services
{
    public class RacesService
    {
        public RacesService()
        {           
        }

        public List<RaceInfo> GetRaces()
        {
            var tracks = Tracks();

            var allTracks = tracks.Find(x => x.Id != "0").ToList();


            return allTracks.Select(t => new RaceInfo {
                Id = t.Id,
                Creator = "lando",
                Name = "test",
                CreatedOn = t.CreatedOn
            }).ToList();
        }

        public Track GetRaceTrack(string id)
        {
            var tracks = Tracks();
            var track = tracks.Find(x => x.Id == id).FirstOrDefault();
            return track;
        }
        
        public async Task SaveRaceTrack(Track track)
        {
            track.CreatedOn = DateTime.Now;

            var tracks = Tracks();
            await tracks.InsertOneAsync(track);
        }

        private IMongoCollection<Track> Tracks()
        {
            var client = CreateClient();
            var db = client.GetDatabase("Racing");
            var tracks = db.GetCollection<Track>("tracks");
            return tracks;
        }

        private MongoClient CreateClient()
        {
            return new MongoClient(ConfigurationManager.AppSettings["connectionString"]);
        }
    }
}
