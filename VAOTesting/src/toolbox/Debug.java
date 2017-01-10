package toolbox;

import org.lwjgl.opengl.GL11;

public class Debug {
	private static final int INT_SIZE = 32;
	private static final int FLOAT_SIZE = 32;
	public static long vertexCount = 0;
	public static long indexCount = 0;
	public static long textureCoordCount = 0;
	public static long vertexColorCount = 0;
	
	private static int renderType = GL11.GL_TRIANGLES;
	
	public static void printBufferData() {
		if (vertexCount > 0)
			System.out.println("Total Vertex Count: " + Debug.vertexCount);
		if (indexCount > 0)	
			System.out.println("Total Index Count: " + Debug.indexCount);
		if (textureCoordCount > 0)
			System.out.println("Total Texture Coordinate Count: " + Debug.textureCoordCount);
		if (vertexColorCount > 0)
			System.out.println("Total Vertex Color Count: " + Debug.vertexColorCount);
			
		System.out.println("Total Amount loaded to Graphics Card: " + (
				
				vertexCount * FLOAT_SIZE + 
				indexCount * INT_SIZE + 
				textureCoordCount * FLOAT_SIZE +
				vertexColorCount * FLOAT_SIZE)/8f/1000000f 
				
				+ "MB");
	}
	
	public static void cycleRenderType() {
		if (renderType == GL11.GL_TRIANGLES) {
			renderType = GL11.GL_LINES;
		} else if (renderType == GL11.GL_LINES) {
			renderType = GL11.GL_TRIANGLES;
		}
	}
	
	public static int getRenderType() {
		return renderType;
	}
}
