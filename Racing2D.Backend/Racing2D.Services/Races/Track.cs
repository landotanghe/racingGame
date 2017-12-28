using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;
using System;

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
        [BsonElement]
        public DateTime CreatedOn { get; set; }
    }
}
