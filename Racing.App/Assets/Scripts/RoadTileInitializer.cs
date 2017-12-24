using UnityEngine;

public class RoadTileInitializer : MonoBehaviour
{
    public GameObject eastWestRoad;
    public GameObject crossRoads;
    public GameObject noRoad;
    public GameObject northEastCorner;

    private RoadDefinition[] roadDefinitions;
    
    // Use this for initialization
    void Start()
    {
        roadDefinitions = new RoadDefinition[] {
            new RoadDefinition(noRoad, 0),
            new RoadDefinition(eastWestRoad, 90),
            new RoadDefinition(eastWestRoad, 0),
            new RoadDefinition(crossRoads, 0),
            new RoadDefinition(northEastCorner, 0),
            new RoadDefinition(northEastCorner, -90),
            new RoadDefinition(northEastCorner, 90),
            new RoadDefinition(northEastCorner, 180),
        };
    }

    public RoadDefinition GetRoadDefinitions(TileType tile)
    {
        return roadDefinitions[(int) tile];
    }
}
