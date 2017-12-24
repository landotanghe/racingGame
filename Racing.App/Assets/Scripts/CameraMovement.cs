using System;
using UnityEngine;

public class CameraMovement : MonoBehaviour {
    public float movementSpeedArrows = 1;
    public float movementSpeedScroll = 4;
    public float rotationSpeed = 4;
    private Vector3 dragOrigin;
    private float X;
    private float Y;

    private Vector3 originalPosition;
    private Quaternion originalRotation;

    // Use this for initialization
    void Start () {
        originalPosition = transform.position;
        originalRotation = transform.rotation;
    }
	
	void FixedUpdate ()
    {
        HandleRotation();
        HandleTranslation();

        HandleReset();
    }

    private void HandleReset()
    {
        if (Input.GetKey(KeyCode.R))
        {
            transform.SetPositionAndRotation(originalPosition, originalRotation);
        }
    }

    private void HandleRotation()
    {
        if (Input.GetMouseButton(1))
        {
            transform.Rotate(new Vector3(Input.GetAxis("Mouse Y") * rotationSpeed, -Input.GetAxis("Mouse X") * rotationSpeed, 0));
            X = transform.rotation.eulerAngles.x;
            Y = transform.rotation.eulerAngles.y;
            transform.rotation = Quaternion.Euler(X, Y, 0);
        }
    }


    private void HandleTranslation()
    {
        float moveX = movementSpeedArrows * Input.GetAxis("Horizontal");
        float moveY = movementSpeedArrows * Input.GetAxis("Vertical");
        float moveZ = - movementSpeedScroll * Input.GetAxis("Mouse ScrollWheel");
        
        Vector3 movement = new Vector3(moveX, moveY, moveZ);

        transform.Translate(movement);

        if(transform.position.y < 0.5f)
        {
            transform.Translate(new Vector3(0, 0.5f-transform.position.y, 0));
        }
    }
}
