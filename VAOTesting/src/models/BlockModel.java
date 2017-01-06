package models;

public class BlockModel {

	public static final float[][] vertices = new float[][] {
			//Back
			{1, 1, 0,
			1, 0, 0,
			0, 0, 0,
			0, 1, 0},
			//Front
			{0, 1, 1,
			0, 0, 1,
			1, 0, 1,
			1, 1, 1},
			//Right
			{1, 1, 1,
			1, 0, 1,
			1, 0, 0,
			1, 1, 0},
			//Left
			{0, 1, 0,
			0, 0, 0,
			0, 0, 1,
			0, 1, 1},
			//Top
			{1, 1, 1,
			1, 1, 0,
			0, 1, 0,
			0, 1, 1},
			//Bottom
			{0, 0, 1,
			0, 0, 0,
			1, 0, 0,
			1, 0, 1}
	};
	
	public static final float[] textureCoords = new float[] {
			0, 0, 
			0, 1, 
			1, 1, 
			1, 0,
			0, 0, 0, 1, 1, 1, 1, 0,
			0, 0, 0, 1, 1, 1, 1, 0,
			0, 0, 0, 1, 1, 1, 1, 0,
			0, 0, 0, 1, 1, 1, 1, 0,
			0, 0, 0, 1, 1, 1, 1, 0
	};
	
	public static final int[][] indices = new int[][] {
			{0, 1, 2,
			2, 3, 0},
			{4, 5, 7,
			7, 5, 6},
			{8, 9, 11,
			11, 9, 10},
			{12, 13, 15,
			15, 13, 14},
			{16, 17, 19,
			19, 17, 18},
			{20, 21, 23,
			23, 21, 22}
	};
}
