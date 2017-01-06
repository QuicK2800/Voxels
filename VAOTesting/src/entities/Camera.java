package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float zoomLevel = 3;
	private float minZoomLevel = 3;
	private float maxZoomLevel = 50;
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	private float zoomSpeed = 0.3f;
	private float zoomBuffer = 0.3f;
	private boolean mouseGrabToggle = false;
	
	private Vector3f position = new Vector3f();
	private float pitch, yaw, roll;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		while (Mouse.next()) {
			if (Mouse.getEventButtonState() && Mouse.getEventButton() == 1 && !mouseGrabToggle) {
				Mouse.setGrabbed(true);
				mouseGrabToggle = true;
			} else if (!Mouse.getEventButtonState() && Mouse.getEventButton() == 1 && mouseGrabToggle) {
				Mouse.setGrabbed(false);
				mouseGrabToggle = false;
			}
		}
		
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		
		calculateCameraPosition(horizontalDistance, verticalDistance);
		
		yaw = (180 - angleAroundPlayer);
		//yaw = 180 - (player.getRotation().y + angleAroundPlayer);
		
		zoomSpeed = (Math.abs(distanceFromPlayer - zoomLevel) * 0.1f);
		
		if (distanceFromPlayer < zoomLevel - zoomBuffer) {
			distanceFromPlayer += zoomSpeed;
		} else if (distanceFromPlayer > zoomLevel + zoomBuffer) {
			distanceFromPlayer -= zoomSpeed;
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = angleAroundPlayer;
		float offsetX = horizontalDistance * (float)Math.sin(Math.toRadians(theta));
		float offsetZ = horizontalDistance * (float)Math.cos(Math.toRadians(theta));
		
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticalDistance;
		position.z = player.getPosition().z - offsetZ;
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		zoomLevel -= Mouse.getDWheel() * 0.03f;
		if (distanceFromPlayer < minZoomLevel) {
			distanceFromPlayer = minZoomLevel;
		}
		
		if (zoomLevel < minZoomLevel) zoomLevel = minZoomLevel;
		if (zoomLevel > maxZoomLevel) zoomLevel = maxZoomLevel;
		
		if (distanceFromPlayer > maxZoomLevel) {
			distanceFromPlayer = maxZoomLevel;
		}
	}
	
	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
}
