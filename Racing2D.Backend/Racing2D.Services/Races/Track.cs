using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;

namespace Racing2D.Services.Races
{
    public class Track
    {
        [BsonId(IdGenerator = typeof(StringObjectIdGenerator))]
        public string Id { get; set; }
        [BsonElement]
        public TileType[][] Tiles { get; set; }
        [BsonElement]
        public StartPosition StartPosition { get; set; }
    }
}
