using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.Networking;

public class MapBuilder : MonoBehaviour {
    public const int TileRadius = 10;
    public RoadTileInitializer roadTileInitializer;

    private int groundMask;
    private TileType tileSelected;
    private Dictionary<Vector3, TileType> tileTypes;
    private Dictionary<Vector3, GameObject> tileInstances;

    // Use this for initialization
    void Start ()
    {
        tileTypes = new Dictionary<Vector3, TileType>();
        tileInstances = new Dictionary<Vector3, GameObject>();
    }

    private void Awake()
    {
        groundMask = LayerMask.GetMask("Underground");
    }

    public void FixedUpdate()
    {
        SelectTile();
        HandleSave();

        if (Input.GetMouseButton(0))
        {
            Debug.Log("left click, looking for groundmask " + groundMask);
            Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
            RaycastHit rayTarget;
            if (Physics.Raycast(ray, out rayTarget, float.MaxValue, groundMask))
            {
                var p = rayTarget.point;
                var cell = new Vector3(GetCell(p.x), 0, GetCell(p.z));
                var location = new Location(cell);
                
                SaveTile(location);
                DeleteOldInstance(location);
                DrawTile(location);
            }
        }
    }

    private class Location
    {
        public Location(Vector3 logicalPosition)
        {
            Logical = logicalPosition;
        }

        public Vector3 Logical { get; private set; }
        public Vector3 Visual => Logical * TileRadius;
    }

    private void HandleSave()
    {
        if (Input.GetKeyUp(KeyCode.S))
        {
            var track = new Track {
                StartPosition = new StartPosition { X = 1, Y = 1 },
                Tiles = ConvertToTiles()
            };
            var json = JsonConvert.SerializeObject(track);
            Debug.Log(track);
            Debug.Log(json);
            StartCoroutine(PostRequest("http://localhost:50248/races/", json));
        }
    }

    private TileType[][] ConvertToTiles()
    {
        var locations = tileTypes.Keys;
        Debug.Log(locations.Count);
        foreach(var l in locations)
        {
            Debug.Log(l.x + "." + l.y + "." + l.z);
        }
        var xRange = new Range(locations.Select(point => point.x));
        var zRange = new Range(locations.Select(point => point.z));


        Debug.Log(xRange.Length + ", " + zRange.Length);

        var tiles = new TileType[xRange.Length][];
        for(var x = 0; x < xRange.Length; x++)
        {
            tiles[x] = new TileType[zRange.Length];
        }

        foreach(var location in locations)
        {
            Debug.Log(location);
            var row = tiles[(int)location.x - xRange.Min];
            Debug.Log(row);
            row[(int)location.z - zRange.Min] = tileTypes[location];
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

    private IEnumerator PostRequest(string url, string bodyJsonString)
    {
        var request = new UnityWebRequest(url, "POST");
        byte[] bodyRaw = new System.Text.UTF8Encoding().GetBytes(bodyJsonString);
        request.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");

        yield return request.Send();

        Debug.Log("Response: " + request.downloadHandler.text);
    }

    private void DeleteOldInstance(Location location)
    {
        if (tileInstances.ContainsKey(location.Logical))
        {
            Destroy(tileInstances[location.Logical]);
            tileInstances[location.Logical] = null;
        }
    }

    private void SaveTile(Location location)
    {
        tileTypes[location.Logical] = tileSelected;
    }

    private void DrawTile(Location location)
    {
        var roadDefinition = roadTileInitializer.GetRoadDefinitions(tileSelected);
        Instantiate(roadDefinition, location.Visual);
    }

    private int GetCell(float freePosition)
    {
        var clipped = (int)(freePosition / TileRadius);
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


    private void Instantiate(RoadDefinition roadDefinition, Vector3 position)
    {
        var transformedPosition = position + new Vector3(TileRadius / 2, 0.01f, TileRadius / 2);
        tileInstances[position] = Instantiate(roadDefinition.Prefab, transformedPosition, Quaternion.AngleAxis(roadDefinition.OrientationInDegrees, Vector3.up));
    }
    

    //GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
    //cube.AddComponent<Rigidbody>();
    //var renderer = cube.GetComponent<Renderer>();
    //if ((x + z) % 2 == 0)
    //    renderer.material.SetColor("_Color", Color.red);
    //cube.transform.position = new Vector3(x, 1, z);
}
