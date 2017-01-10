package engineTester;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import shaders.TerrainShader;
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
		TerrainShader terrainShader = new TerrainShader();
		Renderer renderer = new Renderer(shader);
		Renderer colorRenderer = new Renderer(terrainShader);
		ChunkManager chunkManager = ChunkManager.getInstance();
		
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		GuiManager guiManager = new GuiManager();
		guiManager.addGuiElement(3, GuiManager.BOTTOM, GuiManager.WIDTH-6, 1, "white");
		
		Player player = new Player(new Vector3f(5, 16, 5));
		Camera camera = new Camera(player);
		player.setCamera(camera);
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				chunkManager.addChunk(i-2, j-3);
			}
		}
		
		RawModel rawPlayerModel = OBJLoader.loadObjModel("person", loader);
		ModelTexture playerTexture = new ModelTexture(Loader.getInstance().loadTexture("playerTexture"));
		TexturedModel playerTexturedModel = new TexturedModel(rawPlayerModel, playerTexture);
		Entity playerEntity = new Entity(playerTexturedModel, new Vector3f(5, 16, 5), 0, 0, 0, 0.25f);

		chunkManager.fixEdgeRendering();
		
		Debug.printBufferData();
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 300, 3, 1, 4);
		
//		boolean toggle = false;
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
						float camX = player.getPosition().x;
						float camY = player.getPosition().y;
						float camZ = player.getPosition().z;
						Vector3f blockPos = chunkManager.getWorldBlockPosition(camX, camY-2, camZ);
						
						//for (int i = 0; i < 4; i++) {
							//for (int k = 0; k < 4; k++) {
								chunkManager.removeBlock(camX, camY-2, camZ);
							//}
						//}
								chunkManager.reloadBlock((int)blockPos.x, (int)blockPos.y, (int)blockPos.z);
						
						Chunk chunk = chunkManager.getChunkAt(camX, camZ);
						chunkManager.reloadChunk(chunk.getX(), chunk.getZ());
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_O) {
						float camX = player.getPosition().x;
						float camZ = player.getPosition().z;
						
						Chunk chunk = chunkManager.getChunkAt(camX, camZ);
						chunkManager.reloadChunk(chunk.getX(), chunk.getZ());
					}
				}
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				system.generateParticles(player.getPosition());
			}
			
			Chunk playerChunk = chunkManager.getChunkAt(player.getPosition().x, player.getPosition().z);
//			System.out.println(playerChunk.getX() + ", " + playerChunk.getZ());
			Vector3f pos = chunkManager.getWorldBlockPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
			System.out.println(pos.x + ", " + pos.z);
			
			player.move();
			playerEntity.setPosition(player.getPosition());
			playerEntity.setRotY(player.getRotation().y);
			camera.move();
			
			ParticleMaster.update(camera);
			
			renderer.prepare();
			
			shader.start();
			shader.loadViewMatrix(camera);
				renderer.render(playerEntity, shader);
			shader.stop();
			
			terrainShader.start();
			terrainShader.loadViewMatrix(camera);
				colorRenderer.render(chunkManager.getChunkEntityMap(), terrainShader);
			terrainShader.stop();
			
			ParticleMaster.renderParticles(camera);
			
			guiManager.render();
			DisplayManager.updateDisplay();
		}
		
		ParticleMaster.cleanUp();
		guiManager.cleanUp();
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
