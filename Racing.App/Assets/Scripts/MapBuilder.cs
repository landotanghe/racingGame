using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.Networking;

public class MapBuilder : MonoBehaviour {

    public const string racesUrl = "http://localhost:50248/races/";
    // public const string racesUrl ="http://formeleins-lt.azurewebsites.net/races/";
    public RoadTileInitializer roadTileInitializer;
    public TilesManager tilesManager;

    private static readonly KeyCode[] ctrlKeys = new[] { KeyCode.LeftControl, KeyCode.RightControl };
    private const KeyCode LoadShortKey = KeyCode.K;
    private const KeyCode SaveShortKey = KeyCode.W;


    private int groundMask;
    private TileType tileSelected;




    // Use this for initialization
    void Start ()
    {

    }
    
    private void Awake()
    {
        groundMask = LayerMask.GetMask("Underground");
    }

    public void FixedUpdate()
    {
        SelectTile();
        CheckForSaving();
        CheckForLoading();

        if (Input.GetMouseButton(0))
        {
            Debug.Log("left click, looking for groundmask " + groundMask);
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit rayTarget;
            if (Physics.Raycast(ray, out rayTarget, float.MaxValue, groundMask))
            {
                var p = rayTarget.point;
                tilesManager.AddTile(GetCellCoordinate(p.x), GetCellCoordinate(p.z), tileSelected);
            }
        }
    }


    private void CheckForSaving()
    {
        if (SaveShortcutWasPressed())
        {
            var track = new Track
            {
                StartPosition = new StartPosition { X = 1, Y = 1 },
                Tiles = ConvertToTiles()
            };

            var json = JsonConvert.SerializeObject(track);
            Debug.Log(track);
            Debug.Log(json);
            StartCoroutine(SaveTrack(racesUrl, json));
        }
    }

    private void CheckForLoading()
    {
        if (LoadShortcutWasPressed())
        {
            var www = new WWW(racesUrl + "/" + "5a4514f12218f732a851c0be");
            StartCoroutine(LoadTrackFrom(www));
        }
    }

    private IEnumerator LoadTrackFrom(WWW www)
    {
        yield return www;
        Debug.Log("loading track from + " + www.url);

        if (www.error == null)
        {
            var track = JsonConvert.DeserializeObject<Track>(www.text);
            tilesManager.ResetTiles();
            LoadTilesFrom(track);
        }
        else
        {
            Debug.LogError("network error: " + www.error);
        }
    }

    private void LoadTilesFrom(Track track)
    {
        for (int x = 0; x < track.Tiles.Length; x++)
        {
            for (int y = 0; x < track.Tiles[x].Length; y++)
            {
                tilesManager.AddTile(x, y, track.Tiles[x][y]);
            }
        }
    }


    private bool LoadShortcutWasPressed()
    {
        return BothGroupsPressedAtSameTime(LoadShortKey, ctrlKeys);
    }

    private static bool SaveShortcutWasPressed()
    {
        return BothGroupsPressedAtSameTime(SaveShortKey, ctrlKeys);
    }

    private static bool BothGroupsPressedAtSameTime(KeyCode group1, KeyCode[] group2)
    {
        return Input.GetKeyDown(group1) && group2.Any(k => Input.GetKey(k))
            || Input.GetKey(group1) && group2.Any(k => Input.GetKeyDown(k));
    }

    private TileType[][] ConvertToTiles()
    {
        var tileLocations = tilesManager.GetTiles();
        Debug.Log(tileLocations.Count());
        foreach(var l in tileLocations)
        {
            Debug.Log(l.Key.x + "." + l.Key.y + "." + l.Key.z);
        }
        var xRange = new Range(tileLocations.Select(point => point.Key.x));
        var zRange = new Range(tileLocations.Select(point => point.Key.z));


        Debug.Log(xRange.Length + ", " + zRange.Length);

        var tiles = new TileType[xRange.Length][];
        for(var x = 0; x < xRange.Length; x++)
        {
            tiles[x] = new TileType[zRange.Length];
        }

        foreach(var tile in tileLocations)
        {
            Debug.Log(tile);
            var row = tiles[(int)tile.Key.x - xRange.Min];
            Debug.Log(row);
            row[(int)tile.Key.z - zRange.Min] = tile.Value;
        }
        return tiles;
    }

    private class Range
    {
        public int Min { get; }
        public int Max { get; }

        public int Length => Max - Min + 1;

        public Range(IEnumerable<float> values)
        {
            Min = (int)values.Min();
            Max = (int)values.Max();
        }
    }

    private IEnumerator SaveTrack(string url, string bodyJsonString)
    {
        var request = new UnityWebRequest(url, "POST");
        byte[] bodyRaw = new System.Text.UTF8Encoding().GetBytes(bodyJsonString);
        request.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");

        yield return request.Send();

        Debug.Log("Response: " + request.downloadHandler.text);
    }



    private int GetCellCoordinate(float freePosition)
    {
        var clipped = (int)(freePosition / TilesManager.TileRadius);
        return clipped;
    }

    private void SelectTile()
    {
        var index = GetKeyNumber();
        if(index != null)
        {
            tileSelected = (TileType)index;
        }
    }

    private int? GetKeyNumber()
    {
        var numericKeys = Enumerable.Range((int)KeyCode.Keypad0, 8);
        var alphaKeys = Enumerable.Range((int)KeyCode.Alpha0, 8);

        for (int i = 0; i < 8; i++)
        {
            if (Input.GetKey((KeyCode)numericKeys.ElementAt(i)))
            {
                return i;
            }
            if (Input.GetKey((KeyCode)alphaKeys.ElementAt(i)))
            {
                return i;
            }
        }

        return null;
    }
}