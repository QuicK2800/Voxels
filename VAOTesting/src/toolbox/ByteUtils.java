package toolbox;

public class ByteUtils {
	
	public static final byte BACK = 1;
	public static final byte FRONT = 2;
	public static final byte RIGHT = 4;
	public static final byte LEFT = 8;
	public static final byte TOP = 16;
	public static final byte BOTTOM = 32;
	public static final byte ALL = BACK | FRONT | RIGHT | LEFT | TOP | BOTTOM;
	public static final byte NONE = 0;
	
	public static int getNumberOfOnBits(byte b) {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			if ((b & 1) == 1) {
				count++;
			}
			b = (byte) (b >> 1);
		}
		return count;
	}
	
	public static boolean isBitOn(byte b, int positionFromRight) {
		b = (byte) (b >> positionFromRight);
		return (b & 1) == 1;
	}
}
