package entities;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import toolbox.ByteUtils;

public class ChunkManager {
	
	private static ChunkManager instance = new ChunkManager();
	public static ChunkManager getInstance() { return instance; }
	
	// Key should always have the format: x + " " + z
	private Map<String, Chunk> chunks = new HashMap<>();
	private Map<String, Entity> chunkEntities = new HashMap<>();
	
	public boolean isValidChunk(int x, int z) {
		return chunks.containsKey(x+" "+z);
	}
	
	public void addChunk(int x, int z) {
		Chunk chunk = new Chunk(x, z);
		chunks.put(x+" "+z, chunk);
		chunkEntities.put(x+" "+z, chunk.getChunkEntity());
	}
	
	public void reloadChunk(int x, int z) {
		if (!isValidChunk(x, z)) return;
		
		Chunk chunk = chunks.get(x+" "+z);
		chunk.reload();
		chunks.put(x+" "+z, chunk);
		chunkEntities.put(x+" "+z, chunk.getChunkEntity());
	}
	
	public void removeChunk(int x, int z) {
		if (!isValidChunk(x, z)) return;
		chunks.remove(x+" "+z);
		chunkEntities.remove(x+" "+z);
	}
	
	public Map<String, Entity> getChunkEntityMap() {
		return chunkEntities;
	}
	
	public void fixEdgeRendering() {
		
	}
	
	public Vector3f getWorldBlockPosition(float x, float y, float z) {
		Chunk chunkWhichContainsBlock = getChunkAt(x, z);
		if (chunkWhichContainsBlock == null) return null;
		if (x < 0) {
			x = (int)x - 1;
		}
		
		if (z < 0) {
			z = (int)z - 1;
		}
		return new Vector3f((int)x, (int)y, (int)z);
	}
	
	public Chunk getChunkAt(float x, float z) {
		int chunkX = -1;
		int chunkZ = -1;
		
		if (x >= 0) {
			chunkX = (int)x / Chunk.getWidth();
		}
		if (x < 0) {
			chunkX = (int) Math.floor((int)x / Chunk.getDepth()) - 1;
		}
		
		if (z >= 0) {
			chunkZ = (int)z / Chunk.getDepth();
		}
		
		if (z < 0) {
			chunkZ = (int) Math.floor((int)z / Chunk.getDepth()) - 1;
		}
		
		if (!isValidChunk(chunkX, chunkZ)) {
			return null;
		}
		return chunks.get(chunkX + " " + chunkZ);
	}
	
	public Block getBlockAt(float x, float y, float z) {
		Chunk chunkWhichContainsBlock = getChunkAt(x, z);
		if (chunkWhichContainsBlock == null) return null;
		
		if (x < 0) x = (int)x-1;
		if (x > 0) x = (int)x;
		if (z < 0) z = (int)z-1;
		if (z > 0) z = (int)z;
		
		x %= Chunk.getWidth();
		z %= Chunk.getDepth();
		
		if (x < 0) {
			x += Chunk.getWidth();
		}
		
		if (z < 0) z += Chunk.getDepth();
		
		return chunkWhichContainsBlock.getBlockAt((int)x, (int)y, (int)z);
	}
	
	public void removeBlock(float x, float y, float z) {
		Block block = getBlockAt(x, y, z);
		block.setCullData((byte)0);
		block.setBlockID(-1);
		Vector3f blockPosition = getWorldBlockPosition(x, y, z);
		
		reloadBlock((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);
	}
	
	private boolean inSameChunk(int x1, int z1, int x2, int z2) {
		return getChunkAt(x1, z1) == getChunkAt(x2, z2);
	}
	
	public void reloadBlock(int x, int y, int z) {
//		Block thisBlock, leftBlock, rightBlock, frontBlock, backBlock;
//		
//		thisBlock = getBlockAt(x, y, z);
//		leftBlock = getBlockAt(x-1, y, z);
//		rightBlock = getBlockAt(x+1, y, z);
//		frontBlock = getBlockAt(x, y, z+1);
//		backBlock = getBlockAt(x, y, z-1);
		
//		if (thisBlock.getBlockID() == BlockList.AIR) {
//			leftBlock.setCullData((byte)(leftBlock.getCullData() | ByteUtils.RIGHT));
//			rightBlock.setCullData((byte)(rightBlock.getCullData() | ByteUtils.LEFT));
//			frontBlock.setCullData((byte)(frontBlock.getCullData() | ByteUtils.BACK));
//			backBlock.setCullData((byte)(backBlock.getCullData() | ByteUtils.FRONT));
//		}
		
		updateCullData(x, y, z);
		updateCullData(x-1, y, z);
		updateCullData(x+1, y, z);
		updateCullData(x, y, z-1);
		updateCullData(x, y, z+1);
	}
	
	private void updateCullData(int x, int y, int z) {
		int left, right, front, back;
		left = getBlockAt(x-1, y, z).getBlockID();
		right = getBlockAt(x+1, y, z).getBlockID();
		front = getBlockAt(x, y, z+1).getBlockID();
		back = getBlockAt(x, y, z-1).getBlockID();
		
		Block thisBlock = getBlockAt(x, y, z);
		byte cullData = thisBlock.getCullData();
		if (thisBlock.getBlockID() == BlockList.AIR) return;
		if (left == BlockList.AIR) {
			thisBlock.setCullData((byte) (cullData | ByteUtils.LEFT));
		}
		
		if (right == BlockList.AIR) {
			thisBlock.setCullData((byte) (cullData | ByteUtils.RIGHT));
		}
		
		if (front == BlockList.AIR) {
			thisBlock.setCullData((byte) (cullData | ByteUtils.FRONT));
		}
		
		if (back == BlockList.AIR) {
			thisBlock.setCullData((byte) (cullData | ByteUtils.BACK));
		}
	}
}
