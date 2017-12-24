using UnityEngine;

public class RoadDefinition
{
    public GameObject Prefab { get; private set; }
    public int OrientationInDegrees { get; private set; }

    public RoadDefinition(GameObject prefab, int orientationInDegrees)
    {
        Prefab = prefab;
        OrientationInDegrees = orientationInDegrees;
    }
}
