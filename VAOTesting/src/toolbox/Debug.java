package toolbox;

public class Debug {
	public static long vertexCount = 0;
	public static long indexCount = 0;
	public static long textureCoordCount = 0;
	public static long vertexColorCount = 0;
	
	public static void printBufferData() {
		System.out.println("Total Vertex Count: " + Debug.vertexCount);
		System.out.println("Total Index Count: " + Debug.indexCount);
		System.out.println("Total Texture Coordinate Count: " + Debug.textureCoordCount);
		System.out.println("Total Amount loaded to Graphics Card: " + (
				
				vertexCount * 64L + 
				indexCount * 32L + 
				textureCoordCount * 64L +
				vertexColorCount * 64L)/8f/1000000f 
				
				+ "MB");
	}
}
