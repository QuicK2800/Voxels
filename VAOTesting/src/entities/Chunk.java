package entities;

import java.util.HashMap;
import java.util.Map;

import models.BlockModel;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import textures.ModelTexture;
import toolbox.ByteUtils;
import toolbox.Debug;
import toolbox.HeightsGenerator;

public class Chunk {
	
	private static final int WIDTH = 16, HEIGHT = 32, DEPTH = 16;
	
	private int x, z;
	
	private RawModel rawModel;
	private ModelTexture texture;
	private TexturedModel texturedModel;
	
	private static HeightsGenerator generator = new HeightsGenerator();
	
	private Block[][][] blocks = new Block[WIDTH][HEIGHT][DEPTH];
	private int[][] heightMap = new int[WIDTH][DEPTH];
	private Map<String, Block> blocksWithVisibleSides = new HashMap<>();
	
	public Chunk(int x, int z) {
		this.x = x;
		this.z = z;
		
		generateBlocks();
		//setAllBlocksVisible(true);
		cullFaces();
		
		rawModel = loadChunk();
		texture = new ModelTexture(Loader.getInstance().loadTexture("textureSheet"));
		texturedModel = new TexturedModel(rawModel, texture);
	}
	
	public void cullFaces() {
		for (int i = 0; i < WIDTH; i++)
		for (int j = 0; j < HEIGHT; j++)
		for (int k = 0; k < DEPTH; k++)
		{
			if (blocks[i][j][k].getBlockID() == -1) continue;
			byte cullData = (byte)0;
			
			if (j < HEIGHT-1 && blocks[i][j+1][k].getBlockID() == -1) {
				cullData |= ByteUtils.TOP;
			}
			if (j > 0 && blocks[i][j-1][k].getBlockID() == -1) {
				cullData |= ByteUtils.BOTTOM;
			}
			if (i < WIDTH-1 && blocks[i+1][j][k].getBlockID() == -1) {
				cullData |= ByteUtils.RIGHT;
			}
			if (i > 0 && blocks[i-1][j][k].getBlockID() == -1) {
				cullData |= ByteUtils.LEFT;
			}
			if (k < DEPTH-1 && blocks[i][j][k+1].getBlockID() == -1) {
				cullData |= ByteUtils.FRONT;
			}
			if (k > 0 && blocks[i][j][k-1].getBlockID() == -1) {
				cullData |= ByteUtils.BACK;
			}
			if (cullData != 0) {
				blocksWithVisibleSides.put(i+" "+j+" "+k, blocks[i][j][k]);
			}
			blocks[i][j][k].setCullData(cullData);
		}
	}
	
	public void setAllBlocksVisible(boolean flag) {
		for (int i = 0; i < WIDTH; i++)
		for (int j = 0; j < HEIGHT; j++)
		for (int k = 0; k < DEPTH; k++)
		{
			if (flag) blocks[i][j][k].setCullData(ByteUtils.ALL);
			else blocks[i][j][k].setCullData(ByteUtils.NONE);
		}
	}
	
	public void generateBlocks() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < DEPTH; j++) {
				heightMap[i][j] = (int)generator.generateHeight(x + i/(float)WIDTH, z + j/(float)DEPTH) + 15;
			}
		}
		
		for (int x = 0; x < WIDTH; x++)
		for (int y = 0; y < HEIGHT; y++)
		for (int z = 0; z < DEPTH; z++)
		{
			if (y < 1) {
				blocks[x][y][z] = new Block(0, (byte)(0));
				continue;
			} else if (y >= 1 && y < 4) {
				int rand = (int) (Math.random()*(y+1));
				if (rand == 0) {
					blocks[x][y][z] = new Block(0, (byte)(0));
					continue;
				}
			}
			if (y <= heightMap[x][z]) {
				int rand = (int)(Math.random()*2);
				if (rand == 0) {
					blocks[x][y][z] = new Block(0, (byte)(0));
				} else
					blocks[x][y][z] = new Block(0, (byte)(0));
			} else {
				blocks[x][y][z] = new Block(-1, (byte)0);
			}
		}
	}
	
	
	/** I MIGHT USE THIS LATER FOR TOP BLOCK CALCULATIONS **/
//	public void calculateTopBlocks() {
//		for (int i = 0; i < WIDTH; i++) {
//			for (int j = 0; j < DEPTH; j++) {
//				int low = 0;
//				int high = HEIGHT-1;
//				while (high >= low) {
//					int middle = (low + high) / 2;
//					if (high == low) {
//						System.out.println("Found the top block at: " + middle);
//						blocks[i][high][j].setCullData((byte)(1|2|4|8|16|32));
//					}
//					int blockID = blocks[i][middle][j].getBlockID();
////					System.out.println(blockID);
//					if (blockID == -1) {
//						high = middle - 1;
//					}
//					if (blockID >= 0) {
//						low = middle + 1;
//					}
////					System.out.println("High: " + high);
////					System.out.println("Low: " + low);
//				}
//			}
//		}
//	}
	
	public RawModel loadChunk() {
		
		/***********************************************************\
		 *                    Generate Vertices                    *
		\***********************************************************/
		
		float[][] baseVertices = BlockModel.vertices;
		float[] vertices = new float[WIDTH * HEIGHT * DEPTH * 72];
		int iterator = 0;
		
		for (int x = 0; x < WIDTH; x++)
		for (int y = 0; y < HEIGHT; y++)
		for (int z = 0; z < DEPTH; z++)
		for (int i = 0; i < baseVertices.length; i++) 
		{
			int blockID = blocks[x][y][z].getBlockID();
			byte cullData = blocks[x][y][z].getCullData();
			boolean[] renderFace = new boolean[6];
			for (int b = 0; b < renderFace.length; b++) {
				if (ByteUtils.isBitOn(cullData, b)) {
					renderFace[b] = true;
				} else {
					renderFace[b] = false;
				}
			}
			
			if (blockID == -1) continue;
			//This for loop should only run for faces that are to be drawn
			if (renderFace[i]) {
				for (int j = 0; j < baseVertices[i].length; j++) {
					if (j == 0 || j == 3 || j == 6 || j == 9) {
						vertices[iterator++] = baseVertices[i][j] + x + this.x * WIDTH;
					} else if (j == 1 || j == 4 || j == 7 || j == 10) {
						vertices[iterator++] = baseVertices[i][j] + y;
					} else {
						vertices[iterator++] = baseVertices[i][j] + z + this.z * DEPTH;
					}
					Debug.vertexCount++;
				}
			}
		}
		
		/***********************************************************\
		 *              Generate Texture Coordinates               *
		\***********************************************************/
		/*
		float[] baseTextureCoords = BlockModel.textureCoords;
		float[] textureCoords = new float[WIDTH * HEIGHT * DEPTH * 48];
		iterator = 0;
		
		for (int x = 0; x < WIDTH; x++)
		for (int y = 0; y < HEIGHT; y++)
		for (int z = 0; z < DEPTH; z++)
		{
			int texID = blocks[x][y][z].getBlockID();
			byte cullData = blocks[x][y][z].getCullData();
			
			boolean[] renderFace = new boolean[6];
			for (int b = 0; b < renderFace.length; b++) {
				if (ByteUtils.isBitOn(cullData, b)) {
					renderFace[b] = true;
				} else {
					renderFace[b] = false;
				}
			}
			
			int u, v;
			
			u = texID % 16;
			v = texID / 16;
			
			if (texID == -1) {
				continue;
			}
			for (int i = 0; i < baseTextureCoords.length; i++) {
				if (renderFace[i/8]) {
					if (blocks[x][y][z].getBlockID() == BlockList.GRASS) {
						if (i%2 == 0) {
							if (i < 31) {
								textureCoords[iterator++] = (baseTextureCoords[i] + u+3)/16;
							} else
								textureCoords[iterator++] = (baseTextureCoords[i] + u)/16;
						} else {
							if (i < 31)
								textureCoords[iterator++] = (baseTextureCoords[i] + v)/16;
							else
								textureCoords[iterator++] = (baseTextureCoords[i] + v)/16;
						}
					} else {
						if (i%2 == 0) {
							textureCoords[iterator++] = (baseTextureCoords[i] + u)/16;
						} else {
							textureCoords[iterator++] = (baseTextureCoords[i] + v)/16;
						}
					}
					Debug.textureCoordCount++;
				}
			}
		}
		*/
		/***********************************************************\
		 *                    Generate Colors                      *
		\***********************************************************/
		
		float[] colors = new float[WIDTH * HEIGHT * DEPTH * 72];
		iterator = 0;
		for (int x = 0; x < WIDTH; x++)
		for (int y = 0; y < HEIGHT; y++)
		for (int z = 0; z < DEPTH; z++) {
			colors[iterator++] = 0.2f;
		}
		
		/***********************************************************\
		 *                    Generate Indices                     *
		\***********************************************************/
		
		int[][] baseIndices = BlockModel.indices;
		int[] indices = new int[WIDTH * HEIGHT * DEPTH * 36];
		iterator = 0;
		int currentNumber = 0;
		
		for (int x = 0; x < WIDTH; x++)
		for (int y = 0; y < HEIGHT; y++)
		for (int z = 0; z < DEPTH; z++) {
			int blockID = blocks[x][y][z].getBlockID();
			byte cullData = blocks[x][y][z].getCullData();
			
			boolean[] renderFace = new boolean[6];
			for (int b = 0; b < renderFace.length; b++) {
				if (ByteUtils.isBitOn(cullData, b)) {
					renderFace[b] = true;
				} else {
					renderFace[b] = false;
				}
			}
			
			if (blockID == -1) continue;
			for (int i = 0; i < baseIndices.length; i++) 
			{
				if (renderFace[i]) {
					for (int j = 0; j < baseIndices[i].length; j++) {
						indices[iterator++] = currentNumber;
						indices[iterator++] = currentNumber+1;
						indices[iterator++] = currentNumber+2;
						indices[iterator++] = currentNumber+2;
						indices[iterator++] = currentNumber+3;
						indices[iterator++] = currentNumber;
						Debug.indexCount++;
					}
					currentNumber += 4;
				}
			}
		}
		
		return Loader.getInstance().loadToVAOWithColors(vertices, colors, indices);
	}
	
	public void reloadBlock(int x, int y, int z) {
		byte cullData = 0;
		
		if (getBlockAt(x, y, z).getBlockID() == -1) {
			cullData = blocks[x+1][y][z].getCullData();
			blocks[x+1][y][z].setCullData((byte)(cullData | ByteUtils.LEFT));
			
			cullData = blocks[x-1][y][z].getCullData();
			blocks[x-1][y][z].setCullData((byte)(cullData | ByteUtils.RIGHT));
			
			cullData = blocks[x][y+1][z].getCullData();
			blocks[x][y+1][z].setCullData((byte)(cullData | ByteUtils.BOTTOM));
			
			cullData = blocks[x][y-1][z].getCullData();
			blocks[x][y-1][z].setCullData((byte)(cullData | ByteUtils.TOP));
			
			cullData = blocks[x][y][z+1].getCullData();
			blocks[x][y][z+1].setCullData((byte)(cullData | ByteUtils.BACK));
			
			cullData = blocks[x][y][z-1].getCullData();
			blocks[x][y][z-1].setCullData((byte)(cullData | ByteUtils.FRONT));
		}
	}
	
	public void reload() {
		rawModel = loadChunk();
		texturedModel = new TexturedModel(rawModel, texture);
		cullFaces();
	}
	
	public Block getBlockAt(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public int howManyVisibleBlocks() {
		return blocksWithVisibleSides.size();
	}
	
	public Entity getChunkEntity() {
		return new Entity(texturedModel, new Vector3f(), 0, 0, 0, 1);
	}

	public Map<String, Block> getVisibleBlocks() {
		return blocksWithVisibleSides;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	public static int getDepth() {
		return DEPTH;
	}
}
