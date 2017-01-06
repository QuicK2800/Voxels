package engineTester;

import models.BlockModel;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticColorShader;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Debug;
import entities.Camera;
import entities.Chunk;
import entities.ChunkManager;
import entities.Entity;
import entities.Player;
import guis.GuiManager;

public class MainGameLoop {
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = Loader.getInstance();
		StaticShader shader = new StaticShader();
		StaticColorShader colorShader = new StaticColorShader();
		Renderer renderer = new Renderer(shader);
		ChunkManager chunkManager = ChunkManager.getInstance();
		
		GuiManager guiManager = new GuiManager();
		guiManager.addGuiElement(3, GuiManager.BOTTOM, GuiManager.WIDTH-6, 1, "white");
		
		Player player = new Player(new Vector3f(5, 16, 5));
		Camera camera = new Camera(player);
		player.setCamera(camera);
		
		
		float[][] rawVertices = BlockModel.vertices;
		float[] vertices = new float[rawVertices.length * rawVertices[0].length];
		int iterator = 0;
		for (int i = 0; i < rawVertices.length; i++) {
			for (int j = 0; j < rawVertices[i].length; j++) {
				vertices[iterator++] = rawVertices[i][j];
			}
		}
		
		int[][] rawIndices = BlockModel.indices;
		int[] indices = new int[rawIndices.length * rawIndices[0].length];
		iterator = 0;
		for (int i = 0; i < rawIndices.length; i++) {
			for (int j = 0; j < rawIndices[i].length; j++) {
				indices[iterator++] = rawIndices[i][j];
			}
		}
		
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				chunkManager.addChunk(i, j);
			}
		}
		
		RawModel rawPlayerModel = OBJLoader.loadObjModel("person", loader);
		ModelTexture playerTexture = new ModelTexture(Loader.getInstance().loadTexture("playerTexture"));
		TexturedModel playerTexturedModel = new TexturedModel(rawPlayerModel, playerTexture);
		Entity playerEntity = new Entity(playerTexturedModel, new Vector3f(5, 16, 5), 0, 0, 0, 0.25f);

		chunkManager.fixEdgeRendering();
		
		Debug.printBufferData();
		
//		boolean toggle = false;
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			while (Keyboard.next() && Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.getEventKeyState()) {
				float camX = player.getPosition().x;
				float camY = player.getPosition().y;
				float camZ = player.getPosition().z;
				
				chunkManager.removeBlock(camX, camY-2, camZ);
				Chunk chunk = chunkManager.getChunkAt(camX, camZ);
				chunkManager.reloadChunk(chunk.getX(), chunk.getZ());
			}
			player.move();
			playerEntity.setPosition(player.getPosition());
			playerEntity.setRotY(player.getRotation().y);
			camera.move();
			//Chunk chunk = chunkManager.getChunkAt(camera.getPosition().x, camera.getPosition().z);
			//if (chunk != null)
				//System.out.println(chunk.getX() + ", " + chunk.getZ());
			//Block currentBlock = chunkManager.getBlockAt(camera.getPosition().x, camera.getPosition().y-2, camera.getPosition().z); 
			//if (currentBlock != null)
				//System.out.println(currentBlock.getBlockID());
			renderer.prepare();
			
			shader.start();
			shader.loadViewMatrix(camera);
				renderer.render(playerEntity, shader);
			shader.stop();
			
			colorShader.start();
			colorShader.loadViewMatrix(camera);
				renderer.render(chunkManager.getChunkEntityMap(), colorShader);
			colorShader.stop();
			
			guiManager.render();
			DisplayManager.updateDisplay();
		}
		
		guiManager.cleanUp();
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
