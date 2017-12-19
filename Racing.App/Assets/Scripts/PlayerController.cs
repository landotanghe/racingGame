using UnityEngine;
using UnityEngine.UI;

public class PlayerController : MonoBehaviour {
    public float Acceleration;
    private Rigidbody _rigidbody;

    private int _itemsCollected;
    public Text countText;

    private float _speed;
    public Text speedText;

	// Use this for initialization
	void Start ()
    {
        _rigidbody = GetComponent<Rigidbody>();
    }
	
	// Update is called once per frame
	void FixedUpdate()
    {
        float moveHorizontal = Input.GetAxis("Horizontal");
        float moveVertical = Input.GetAxis("Vertical");
        
        var force = new Vector3(moveHorizontal, 0.0f, moveVertical);
        _rigidbody.AddForce(force * Acceleration);

        _speed = _rigidbody.velocity.magnitude;
        //_rigidbody.transform.Rotate(new Vector3(moveHorizontal, moveHorizontal, 0.0f));
    }

    private void OnTriggerEnter(Collider other)
    {
        if (other.CompareTag("Collectible"))
        {
            Collect(other);
        }
    }

    private void Collect(Collider collectible)
    {
        collectible.gameObject.SetActive(false);
        _itemsCollected++;
        SetCountText();
        //Destroy(other.gameObject);
    }
    private void SetCountText()
    {
        countText.text = "collected items: " + _itemsCollected;
        speedText.text = "speed: " + _speed;
    }
}
