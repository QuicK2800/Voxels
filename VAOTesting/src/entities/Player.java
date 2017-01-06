package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Player {
	private Vector3f position = new Vector3f();
	private Vector3f rotation = new Vector3f();
	private Vector3f velocity = new Vector3f();
	
	private float yTarget = 0;
	
	private static final float MOVE_SPEED = 0.25f;
	private static final float GRAVITY = -0.009f;
	private static final float JUMP_POWER = 0.3f;
	
	private boolean inAir = true;
	
	private Vector3f hitBox = new Vector3f(0.5f, 1.9f, 0.5f);
	
	private Camera camera;
	
	public Player(Vector3f position) {
		this.position = position;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public void move() {
		
		ChunkManager chunkManager = ChunkManager.getInstance();
		
		Vector2f direction = new Vector2f((float)Math.cos(Math.toRadians(camera.getYaw())), (float)Math.sin(Math.toRadians(camera.getYaw())));
		
		float velocityModifierX = 0f;
		float velocityModifierZ = 0f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			velocityModifierZ += -direction.x * MOVE_SPEED;
			velocityModifierX += direction.y * MOVE_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			velocityModifierZ += direction.x * MOVE_SPEED;
			velocityModifierX += -direction.y * MOVE_SPEED;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			velocityModifierZ += direction.y * MOVE_SPEED;
			velocityModifierX += direction.x * MOVE_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			velocityModifierZ += -direction.y * MOVE_SPEED;
			velocityModifierX += -direction.x * MOVE_SPEED;
		}
		
		velocity.x = velocityModifierX;
		velocity.z = velocityModifierZ;
		
		// Collision Detection with Blocks
		if (velocity.x > 0) {
			if (chunkManager.getBlockAt(position.x + velocity.x + hitBox.x, position.y - hitBox.y, position.z + hitBox.z).getBlockID() != -1
		     || chunkManager.getBlockAt(position.x + velocity.x + hitBox.x, position.y - hitBox.y, position.z - hitBox.z).getBlockID() != -1) {
				position.y += 1;
			}
		}
		if (velocity.x < 0) {
			if (chunkManager.getBlockAt(position.x + velocity.x - hitBox.x, position.y - hitBox.y, position.z + hitBox.z).getBlockID() != -1
			 || chunkManager.getBlockAt(position.x + velocity.x - hitBox.x, position.y - hitBox.y, position.z - hitBox.z).getBlockID() != -1) {
				position.y += 1;
			}
		}
		if (velocity.z > 0) {
			if (chunkManager.getBlockAt(position.x + hitBox.x, position.y - hitBox.y, position.z + velocity.z + hitBox.z).getBlockID() != -1
			 || chunkManager.getBlockAt(position.x - hitBox.x, position.y - hitBox.y, position.z + velocity.z + hitBox.z).getBlockID() != -1) {
				position.y += 1;
			}
		}
		if (velocity.z < 0) {
			if (chunkManager.getBlockAt(position.x + hitBox.x, position.y - hitBox.y, position.z + velocity.z - hitBox.z).getBlockID() != -1
			 || chunkManager.getBlockAt(position.x - hitBox.x, position.y - hitBox.y, position.z + velocity.z - hitBox.z).getBlockID() != -1) {
				position.y += 1;
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !inAir) {
			velocity.y = JUMP_POWER;
			inAir = true;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			rotation.y += 1;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			rotation.y -= 1;
		}
		
		if (rotation.x > 90) rotation.x = 90;
		if (rotation.x < -90) rotation.x = -90;
		
		float target = -camera.getYaw()+180;
		float rotateSpeed = (Math.abs(target-rotation.y)) * 0.3f;
		
		if (rotation.y < target) {
			rotation.y += rotateSpeed;
		} else if (rotation.y > target) {
			rotation.y -= rotateSpeed;
		}
		
		velocity.y += GRAVITY;
		
		Vector3f.add(position, velocity, position);
		
		// Post-movement corrections
		
		if (velocity.y <= 0) {
			if (chunkManager.getBlockAt(position.x + hitBox.x, position.y - hitBox.y, position.z + hitBox.z).getBlockID() != -1
		     || chunkManager.getBlockAt(position.x - hitBox.x, position.y - hitBox.y, position.z + hitBox.z).getBlockID() != -1
			 || chunkManager.getBlockAt(position.x - hitBox.x, position.y - hitBox.y, position.z - hitBox.z).getBlockID() != -1
			 || chunkManager.getBlockAt(position.x + hitBox.x, position.y - hitBox.y, position.z - hitBox.z).getBlockID() != -1) {
				position.y -= velocity.y;
				inAir = false;
				velocity.y = 0;
				yTarget = position.y;
			}
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
