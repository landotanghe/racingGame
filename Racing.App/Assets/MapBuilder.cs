using System;
using UnityEngine;

public class MapBuilder : MonoBehaviour {
    public const int TileRadius = 10;
    public int width;
    public int height;
    public GameObject eastWestRoad;
    public GameObject northSouthRoad;
    public GameObject crossRoads;
    public GameObject noRoad;


    public GameObject northEastCorner;
    public GameObject southEastCorner;
    public GameObject northWestCorner;
    public GameObject southWestCorner;


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
        }
    }


    private void Create(Track track)
    {
        System.Random r = new System.Random();
        for (int z = 0; z < track.Tiles.Length; z++)
        {
            for (int x = 0; x < track.Tiles[0].Length; x++)
            {
                var index = track.Tiles[z][x];//r.Next(14);
                var prefab = GetPrefab(index);

                //GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
                //cube.AddComponent<Rigidbody>();
                //var renderer = cube.GetComponent<Renderer>();
                //if ((x + z) % 2 == 0)
                //    renderer.material.SetColor("_Color", Color.red);
                //cube.transform.position = new Vector3(x, 1, z);
                var position = new Vector3(TileRadius * x, 1, TileRadius * z);
                Instantiate(prefab, position, Quaternion.identity);
            }
        }
    }

    private GameObject GetPrefab(int index)
    {
        switch (index)
        {
            case 0: return northSouthRoad;
            case 1: return eastWestRoad;
            case 2: return crossRoads;
            case 3: return northEastCorner;
            case 4: return northWestCorner;
            case 5: return southEastCorner;
            case 6: return southWestCorner;
            case 7: return noRoad;
        }
        return crossRoads;

    }
}
