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
		int width = Chunk.getWidth();
		int depth = Chunk.getDepth();
		
		for (String key : chunks.keySet()) {
			Chunk chunk = chunks.get(key);
			int x = chunk.getX();
			int z = chunk.getZ();
			Map<String, Block> visibleBlocks = chunk.getVisibleBlocks();
			
			for (String blockKey : visibleBlocks.keySet()) {
				Block block = visibleBlocks.get(blockKey);
				String[] positionData = blockKey.split(" ");
				int blockX = Integer.parseInt(positionData[0]);
				int blockY = Integer.parseInt(positionData[1]);
				int blockZ = Integer.parseInt(positionData[2]);
				
				// If it is not on the border, skip.
				if (blockX < width-1 && blockX > 0 && blockZ < depth-1 && blockZ > 0) continue;
				
				Chunk adjacentChunk;
				byte cullData = block.getCullData();
				
				if (blockX == width-1) {
					if (isValidChunk(x+1, z)) {
						adjacentChunk = getChunkAt(x+1, z);
						if ((cullData & ByteUtils.RIGHT) != ByteUtils.RIGHT && adjacentChunk.getBlockAt((blockX+1)%width, blockY, blockZ).getBlockID() != -1) {
							block.setCullData((byte)(cullData | ByteUtils.RIGHT));
						}
					}
				}
				if (blockX == 0) {
					if (isValidChunk(x-1, z)) {
						adjacentChunk = getChunkAt(x-1, z);
						if ((cullData & ByteUtils.LEFT) != ByteUtils.LEFT && adjacentChunk.getBlockAt(blockX+width-1, blockY, blockZ).getBlockID() != -1) {
							block.setCullData((byte)(cullData | ByteUtils.LEFT));
						}
					}
				}
				
				if (blockZ == depth-1) {
					if (isValidChunk(x, z+1)) {
						adjacentChunk = getChunkAt(x, z+1);
						if ((cullData & ByteUtils.FRONT) != ByteUtils.FRONT && adjacentChunk.getBlockAt(blockX, blockY, (blockZ+1)%width).getBlockID() != -1) {
							block.setCullData((byte)(cullData | ByteUtils.FRONT));
						}
					}
				}
				if (blockZ == 0) {
					if (isValidChunk(x, z-1)) {
						adjacentChunk = getChunkAt(x, z-1);
						if ((cullData & ByteUtils.BACK) != ByteUtils.BACK && adjacentChunk.getBlockAt(blockX, blockY, blockZ+width-1).getBlockID() != -1) {
							block.setCullData((byte)(cullData | ByteUtils.BACK));
						}
					}
				}
			}
		}
	}
	
	private Chunk getChunkAt(int x, int z) {
		return chunks.get(x + " " + z);
	}
	
	private Vector3f getWorldBlockPosition(float x, float y, float z) {
		Chunk chunkWhichContainsBlock = getChunkAt(x, z);
		if (chunkWhichContainsBlock == null) return null;
		x %= Chunk.getWidth();
		z %= Chunk.getDepth();
		return new Vector3f((int)x, (int)y, (int)z);
	}
	
	public Chunk getChunkAt(float x, float z) {
		int chunkX = -1;
		int chunkZ = -1;
		
		if (x >= 0) {
			chunkX = (int)x / Chunk.getWidth();
		}
		if (x < 0) {
			chunkX = (int)x / Chunk.getWidth() - Chunk.getWidth();
		}
		
		if (z >= 0) {
			chunkZ = (int)z / Chunk.getDepth();
		}
		
		if (z < 0) {
			chunkZ = (int)z / Chunk.getDepth() - Chunk.getDepth();
		}
		
		if (!isValidChunk(chunkX, chunkZ)) {
			return null;
		}
		return chunks.get(chunkX + " " + chunkZ);
	}
	
	public Block getBlockAt(float x, float y, float z) {
		Chunk chunkWhichContainsBlock = getChunkAt(x, z);
		if (chunkWhichContainsBlock == null) return null;
		x %= Chunk.getWidth();
		z %= Chunk.getDepth();
		
		return chunkWhichContainsBlock.getBlockAt((int)x, (int)y, (int)z);
	}
	
	public void removeBlock(float x, float y, float z) {
		Chunk chunk = getChunkAt(x, z);
		Block block = getBlockAt(x, y, z);
		block.setCullData((byte)0);
		block.setBlockID(-1);
		//reloadChunk(chunk.getX(), chunk.getZ());
		Vector3f blockPosition = getWorldBlockPosition(x, y, z);
		chunk.reloadBlock((int)blockPosition.x, (int)blockPosition.y, (int)blockPosition.z);
	}
}
