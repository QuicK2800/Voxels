package guis;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.Loader;

public class GuiManager {
	private static final int GRID_ROWS = 16;
	private static final int GRID_COLUMNS = 16;
	
	public static final int TOP = 0;
	public static final int BOTTOM = GRID_ROWS - 1;
	public static final int LEFT = 0;
	public static final int RIGHT = GRID_COLUMNS - 1;
	public static final int WIDTH = GRID_COLUMNS;
	public static final int HEIGHT = GRID_ROWS;
	
	private float gridHeight, gridWidth;
	
	private List<GuiTexture> guiElements = new ArrayList<>();
	private Loader loader = Loader.getInstance();
	
	private GuiRenderer renderer = new GuiRenderer(loader);
	
	public GuiManager() {
		gridHeight = 2f / (float)GRID_ROWS;
		gridWidth = 2f / (float)GRID_COLUMNS;
	}
	
	public void addGuiElement(int gridX, int gridY, int width, int height, String fileName) {
		GuiTexture gui = new GuiTexture(loader.loadTexture(fileName), new Vector2f((gridWidth * gridX) - 1, 1 - (gridHeight * gridY) - gridHeight), new Vector2f(gridWidth * width, gridHeight * height));
		guiElements.add(gui);
	}
	
	public void addGuiElementCustomPosition(float x, float y, float width, float height, String fileName) {
		GuiTexture gui = new GuiTexture(loader.loadTexture(fileName), new Vector2f(x, y), new Vector2f(width, height));
		guiElements.add(gui);
	}
	
	public void render() {
		renderer.render(guiElements);
	}
	
	public void cleanUp() {
		renderer.cleanUp();
	}
}
