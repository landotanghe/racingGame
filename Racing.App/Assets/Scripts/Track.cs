public class Track
{
    public string Id { get; set; }
    public TileType[][] Tiles { get; set; }
    public StartPosition StartPosition { get; set; }
}

public class StartPosition
{
    public int X { get; set; }
    public int Y { get; set; }
}