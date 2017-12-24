using System;
using UnityEngine;

public class MapLoader : MonoBehaviour {
    public const int TileRadius = 10;
    public RoadTileInitializer roadTileInitializer;
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
            var track = new Track
            {
                StartPosition = new StartPosition { X = 0, Y = 0 },
                Tiles = new TileType[][]
                {
                    new TileType[]{ TileType.Crossroads}
                }
            };
            Create(track);
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
                Tiles = new TileType[][]
                {
                    new TileType[]{ TileType.Crossroads}
                }
            };
            Create(track);
        }
    }
    
    private void Create(Track track)
    {
        player.transform.position = new Vector3(TileRadius * track.StartPosition.X, 5, TileRadius * track.StartPosition.Y);

        var height = track.Tiles.Length;
        var width = track.Tiles[0].Length;

        for (int z = 0; z < height; z++)
        {
            for (int x = 0; x < width; x++)
            {
                var index = track.Tiles[height - z - 1][x];

                var position = new Vector3(TileRadius * x, 1, TileRadius * z);
                Debug.Log("index" + index);
                var roadDefinition = roadTileInitializer.GetRoadDefinitions((TileType)index);
                Instantiate(roadDefinition, position);
            }
        }
    }

    private void Instantiate(RoadDefinition roadDefinition, Vector3 position)
    {
        Instantiate(roadDefinition.Prefab, position, Quaternion.AngleAxis(roadDefinition.OrientationInDegrees, Vector3.up));
    }
}
