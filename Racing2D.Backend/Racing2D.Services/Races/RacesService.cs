using MongoDB.Driver;
using Racing2D.Services.Races;
using System.Collections.Generic;
using System.Linq;
using System.Security.Authentication;
using System.Threading.Tasks;

namespace Racing2D.Services
{
    public class RacesService
    {
        //private const string host = "racing-lt.documents.azure.com";
        //private const string dbName = "racing";
        //private const string tracksCollection = "tracks";
        //private const string userName = "racing-lt";
        //private const string password = "n0SkbpOLgWxbl4PMtp6An1TOF9mSvfGhkEVe0MYD5k3sYFb5WYhMJhsyQUFmWI7ypkQxwcKi5MAFPIHkfjfYlg==";

        private const string connectionString = "mongodb://racing-lt:n0SkbpOLgWxbl4PMtp6An1TOF9mSvfGhkEVe0MYD5k3sYFb5WYhMJhsyQUFmWI7ypkQxwcKi5MAFPIHkfjfYlg==@racing-lt.documents.azure.com:10255/?ssl=true&replicaSet=globaldb";

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
                Name = "test"
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
            var tracks = Tracks();

            tracks.InsertOne(track);
        }

        private IMongoCollection<Track> Tracks()
        {
            var client = CreateClient();
            var db = client.GetDatabase("Racing");
            var tracks = db.GetCollection<Track>("tracks");
            return tracks;
        }

        private MongoClient CreateClient()
        {//TODO pass in connection string with config file
            //MongoClientSettings settings = new MongoClientSettings();
            //settings.Server = new MongoServerAddress(host, 10255);
            //settings.UseSsl = true;
            //settings.SslSettings = new SslSettings();
            //settings.SslSettings.EnabledSslProtocols = SslProtocols.Tls12;

            //MongoIdentity identity = new MongoInternalIdentity(dbName, userName);
            //MongoIdentityEvidence evidence = new PasswordEvidence(password);

            //settings.Credential = new MongoCredential("SCRAM-SHA-1", identity, evidence);

            //MongoClient client = new MongoClient(settings);

            //return client;

            return new MongoClient(connectionString);
        }
    }
}
