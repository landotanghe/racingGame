using System.Collections.Generic;
using UnityEngine;

public class TilesManager : MonoBehaviour
{
    public const int TileRadius = 10;

    public RoadTileInitializer roadTileInitializer;

    private Dictionary<Vector3, TileType> tileTypes;
    private Dictionary<Vector3, GameObject> tileInstances;

    public TilesManager()
    {
        tileTypes = new Dictionary<Vector3, TileType>();
        tileInstances = new Dictionary<Vector3, GameObject>();
    }

    public IEnumerable<KeyValuePair<Vector3, TileType>> GetTiles()
    {
        return tileTypes;
    }

    public class Location
    {
        public Location(int x, int y) : this(new Vector3(x, 0, y))
        {
        }

        private Location(Vector3 logicalPosition)
        {
            Logical = logicalPosition;
        }

        public Vector3 Logical { get; private set; }
        public Vector3 Visual => Logical * TileRadius;
    }

    public void ResetTiles()
    {
        foreach (var tile in tileInstances.Values)
        {
            Destroy(tile);
        }
        tileTypes = new Dictionary<Vector3, TileType>();
        tileInstances = new Dictionary<Vector3, GameObject>();
    }

    public void AddTile(int x, int y, TileType tile)
    {
        var location = new Location(x, y);

        AddLogicalTile(location, tile);
        DeleteOldInstance(location);
        DrawTile(location, tile);
    }


    private void DeleteOldInstance(Location location)
    {
        if (tileInstances.ContainsKey(location.Logical))
        {
            Destroy(tileInstances[location.Logical]);
            tileInstances[location.Logical] = null;
        }
    }

    private void AddLogicalTile(Location location, TileType tile)
    {
        tileTypes[location.Logical] = tile;
    }

    private void DrawTile(Location location, TileType tile)
    {
        var roadDefinition = roadTileInitializer.GetRoadDefinitions(tile);
        Instantiate(roadDefinition, location.Visual);
    }

    private void Instantiate(RoadDefinition roadDefinition, Vector3 position)
    {
        var transformedPosition = position + new Vector3(TileRadius / 2, 0.01f, TileRadius / 2);
        tileInstances[position] = Instantiate(roadDefinition.Prefab, transformedPosition, Quaternion.AngleAxis(roadDefinition.OrientationInDegrees, Vector3.up));
    }
}