using System;

[Serializable]
public class Track
{
    public int[][] Tiles { get; set; }
    public StartPosition StartPosition { get; set; }
}

[Serializable]
public class StartPosition
{
    public int X { get; set; }
    public int Y { get; set; }
}