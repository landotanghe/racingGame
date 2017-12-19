
using UnityEngine;
using System;
using System.Collections;

public class DownloadManager : MonoBehaviour
{
    private WWW wwwData;
    private static DownloadManager instance = null;

    // Use this for initialization
    void Start()
    {
        if (instance == null)
            instance = FindObjectOfType(typeof(DownloadManager)) as DownloadManager;
    }

    // Update is called once per frame
    void Update()
    {

    }

    void OnApplicationQuit()
    {
        instance = null;
    }

    public delegate void DownloadCallback(string data, string sError);

    private IEnumerator WaitForDownload(DownloadCallback callBack)
    {
        Debug.Log("Yielding");
        yield return wwwData;
        Debug.Log("Yielded");
        callBack(wwwData.text, wwwData.error);
    }

    private void StartDownload(string sURL, DownloadCallback fn)
    {
        try
        {
            wwwData = new WWW(sURL);
            Debug.Log("Starting download.");
            StartCoroutine("WaitForDownload", fn);
        }
        catch (Exception e)
        {
            Debug.Log(e.ToString());
        }
    }

    public static void Download(string sURL, DownloadCallback fn)
    {
        instance.StartDownload(sURL, fn);
    }
}