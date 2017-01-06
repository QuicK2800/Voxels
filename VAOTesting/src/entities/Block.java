package entities;

public class Block {
	private int blockID;
	private byte cullData;
	
	public Block(int blockID, byte cullData) {
		this.blockID = blockID;
		this.cullData = cullData;
	}

	public int getBlockID() {
		return blockID;
	}

	public byte getCullData() {
		return cullData;
	}
	
	public void setCullData(byte cullData) {
		this.cullData = cullData;
	}
	
	public void setBlockID(int blockID) {
		this.blockID = blockID;
	}
}
