using MongoDB.Bson.Serialization.Attributes;

namespace Racing2D.Services.Races
{
    public class Track
    {
        [BsonId]
        public int Id { get; set; }
        [BsonElement]
        public TileType[][] Tiles { get; set; }
        [BsonElement]
        public StartPosition StartPosition { get; set; }
    }
}
