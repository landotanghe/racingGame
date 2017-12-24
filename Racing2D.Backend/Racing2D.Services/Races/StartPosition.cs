using MongoDB.Bson.Serialization.Attributes;

namespace Racing2D.Services.Races
{
    public class StartPosition
    {
        [BsonElement]
        public int X { get; set; }
        [BsonElement]
        public int Y { get; set; }
    }
}