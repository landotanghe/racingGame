using System;
using UnityEngine;

public class MapBuilder : MonoBehaviour {
    public const int TileRadius = 10;
    public int width;
    public int height;
    public GameObject eastWestRoad;
    public GameObject crossRoads;
    public GameObject noRoad;
    public GameObject northEastCorner;
    public GameObject player;

    // Use this for initialization
    void Start ()
    {
        LoadRaceTrack();
    }


    private void LoadRaceTrack()
    {
        try
        {
            DownloadManager.Download("http://localhost:50248/races/track", OnRaceTrackLoaded);
        }
        catch (Exception e)
        {
            Debug.Log(e.ToString());
        }
    }

    private void OnRaceTrackLoaded(string data, string sError)
    {
        try
        {
            if (null != sError)
                Debug.Log(sError);
            else
            {
                var track = JSONParser.FromJson<Track>(data);
                Create(track);
            }
        }
        catch (Exception e)
        {
            Debug.Log(e.ToString());
            var track = new Track
            {
                StartPosition = new StartPosition { X = 0, Y = 0 },
                Tiles = new int[][]
                {
                    new int[]{1}
                }
            };
            Create(track);
        }
    }


    private void Create(Track track)
    {
        var roadDefinitions = InitializeRoadDefinitions();

        player.transform.position = new Vector3(TileRadius * track.StartPosition.X, 5, TileRadius * track.StartPosition.Y);

        var height = track.Tiles.Length;
        var width = track.Tiles[0].Length;

        for (int z = 0; z < height; z++)
        {
            for (int x = 0; x < width; x++)
            {
                var index = track.Tiles[height-z-1][x];

                var position = new Vector3(TileRadius * x, 1, TileRadius * z);
                Instantiate(roadDefinitions[index], position);
            }
        }
    }

    private void Instantiate(RoadDefinition roadDefinition, Vector3 position)
    {
        Instantiate(roadDefinition.Prefab, position, Quaternion.AngleAxis(roadDefinition.OrientationInDegrees, Vector3.up));
    }

    private class RoadDefinition
    {
        public GameObject Prefab { get; private set; }
        public int OrientationInDegrees { get; private set; }

        public RoadDefinition(GameObject prefab, int orientationInDegrees)
        {
            Prefab = prefab;
            OrientationInDegrees = orientationInDegrees;
        }
    }

    private RoadDefinition[] InitializeRoadDefinitions()
    {
        RoadDefinition[] roadDefinitions = new RoadDefinition[] {
            new RoadDefinition(noRoad, 0),
            new RoadDefinition(eastWestRoad, 90),
            new RoadDefinition(eastWestRoad, 0),
            new RoadDefinition(crossRoads, 0),
            new RoadDefinition(northEastCorner, 0),
            new RoadDefinition(northEastCorner, -90),
            new RoadDefinition(northEastCorner, 90),
            new RoadDefinition(northEastCorner, 180),
        };

        return roadDefinitions;
    }


    //GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
    //cube.AddComponent<Rigidbody>();
    //var renderer = cube.GetComponent<Renderer>();
    //if ((x + z) % 2 == 0)
    //    renderer.material.SetColor("_Color", Color.red);
    //cube.transform.position = new Vector3(x, 1, z);
}
