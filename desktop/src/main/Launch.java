package main;

import bots.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import testing.ErrorTesting;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is the main class of the program.
 * It is being executed upon launch, and it calls all the other classes based on the state of the program
 */
public class Launch implements Screen {
	private static int currentState;
	private static final int WAIT_FOR_VELOCITIES = 1;
	private static final int BALL_MOVING = 2;
	private static final int GAME_OVER = 3;

	private PerspectiveCamera camera;
	private FirstPersonCameraController cameraController;
	private ModelBatch modelBatch;
	private ModelBuilder modelBuilder;
	private ModelInstance waterInstance;
	private Ball ball;
	private Goal goal;
	private ModelCache grassModels;
	private static ArrayList<Tree> treeList = new ArrayList<>();
	private ModelCache treeModels;
	private ModelInstance pointer;
	private float pointerX;
	private float pointerZ;

	private double inputVelocity = 0;
	private int shotsAmount = 0;
	private int livesLeft = 3;
	private int score = 0;
	private int tries = 0;
	private Vector3 previousBallPos;

	private SpriteBatch spriteBatch;
	private BitmapFont font;

	private Sound goalSound;
	private Sound gameOverSound;
	private Sound outOfMapSound;
	private Sound waterFallSound;

	public Launch(){}

	@Override
	public void show () {
		outOfMapSound = Gdx.audio.newSound(Gdx.files.internal("BoomSound.mp3"));
		goalSound = Gdx.audio.newSound(Gdx.files.internal("goalSound.mp3"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameOverSound.mp3"));
		waterFallSound = Gdx.audio.newSound(Gdx.files.internal("waterDropSound.mp3"));

		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();

		loadInitialValues();
		loadBall();
		loadGoal();

		createCamera();

		loadTerrain();
		loadTrees();
		loadScreenText();

		Environment environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1f));

		currentState = WAIT_FOR_VELOCITIES;

		Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMusicSound.mp3"));
		gameMusic.setVolume(.4f);
		gameMusic.setLooping(true);
		gameMusic.play();

		if(InputReader.getGameType().equals("Testing")){
			ErrorTesting.testErrors(ball, goal);
			System.exit(0);
		}
	}

	/**
	 * Calls all the classes and functions necessary for the simulation, based on the state of the program.
	 * This method keeps on running in a loop as long as the program has not been terminated.
	 */
	public void render (float delta) {

		//This part of render is responsible for the rendering that always has to happen such as updating the camera, ball, terrain etc.
		Gdx.gl.glClearColor(150/255f, 150/255f, 255/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

		camera.update(true);
		cameraController.update(Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
			camera.translate(0f,0.1f,0f); //Move camera up
		}
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
			camera.translate(0f,-0.1f,0f); //Move camera down
		}
		modelBatch.begin(camera);
		modelBatch.render(grassModels);
		modelBatch.render(treeModels);
		modelBatch.render(waterInstance);
		modelBatch.render(goal);
		modelBatch.render(ball);
		modelBatch.end();

		spriteBatch.begin();
		font.draw(spriteBatch, "Number of shots: " + shotsAmount, 10, 20);
		font.draw(spriteBatch, "Score: "+score, 10, 60);
		font.draw(spriteBatch, "Ball xPos: "+ball.getXPos(), 10, Gdx.graphics.getHeight()-20);
		font.draw(spriteBatch, "Ball yPos: "+ball.getYPos(), 10, Gdx.graphics.getHeight()-40);
		font.draw(spriteBatch, "Ball zPos: "+ball.getZPos(), 10, Gdx.graphics.getHeight()-60);
		font.draw(spriteBatch, "target xPos: "+goal.getXPos(), 10, Gdx.graphics.getHeight()-100);
		font.draw(spriteBatch, "target yPos: "+goal.getYPos(), 10, Gdx.graphics.getHeight()-120);
		font.draw(spriteBatch, "target zPos: "+goal.getZPos(), 10, Gdx.graphics.getHeight()-140);
		font.draw(spriteBatch, "X-Velocity: "+ ball.getXVelocity(), 10, Gdx.graphics.getHeight()-180);
		font.draw(spriteBatch, "Z-Velocity: "+ ball.getZVelocity(), 10, Gdx.graphics.getHeight()-200);

		if(InputReader.getGameType().equals("Player")){
			font.draw(spriteBatch, "Starting velocity: " + inputVelocity + "m/s", Gdx.graphics.getWidth()-180, Gdx.graphics.getHeight()-20);
			font.draw(spriteBatch, "Lives left: "+livesLeft, 10, 40);
		}
		else if(InputReader.getGameType().equals("Bot")){
			font.draw(spriteBatch, "Press ENTER to (re)start the simulation", Gdx.graphics.getWidth()-260, Gdx.graphics.getHeight()-20);
			font.draw(spriteBatch, "Tries: "+tries, 10, 40);
		}
		spriteBatch.end();

		//While we are in this state of the program, we simulate everything that is necessary for either getting the starting velocities from either the user or the bot
		if(getCurrentState() == WAIT_FOR_VELOCITIES){
			if(InputReader.getGameType().equals("Player")) {
				updatePointer();
				updateVelocity();

				if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && inputVelocity != 0.0) { //Set velocities
					double xLength = pointerX - ball.getXPos();
					double zLength = pointerZ - ball.getZPos();
					double velocityLength = Math.sqrt(xLength * xLength + zLength * zLength);
					double xVelocity = inputVelocity * xLength / velocityLength;
					double zVelocity = inputVelocity * zLength / velocityLength;
					shotsAmount++;
					ball.setPhysics(xVelocity, zVelocity);
					setCurrentState(BALL_MOVING);
					previousBallPos = ball.getPosition();
					inputVelocity = 0.0;

					pointer = null;

					new Thread(() -> {
						FramesQueue.clear();
						Physics.setBallMoving(true);
						switch (InputReader.getOdeSolver()) {
							case "Euler": {
								ODESolvers.eulerCalculationFrames(new double[]{ball.getXPos(), ball.getZPos(), xVelocity, zVelocity, 0}, 0.00001, 0.0166666666666666);
								FramesQueue.isDone = true;
								break;
							}
							case "Runge2": {
								ODESolvers.rungeKutta2Frames(new double[]{ball.getXPos(), ball.getZPos(), xVelocity, zVelocity, 0}, 0.00004, 0.016666666666666);
								FramesQueue.isDone = true;

								break;
							}
							case "Runge4": {
								ODESolvers.rungeKutta4Frames(new double[]{ball.getXPos(), ball.getZPos(), xVelocity, zVelocity, 0}, 0.00004, 0.01666666666666666);
								FramesQueue.isDone = true;

								break;
							}
						}
					}).start();
					inputVelocity = 0.0;

				}
			}
			else if(InputReader.getGameType().equals("Bot") &&  (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))){
				previousBallPos = ball.getPosition();
				System.out.println("Simulating bot of type:\t" + InputReader.getBotType());
				Bot bot;
				switch(InputReader.getBotType()){
					case("Brute-Force"):
						bot = new BruteForceBot(ball);
						break;
					case("Rule-Based"):
						bot = new RuleBasedBot(ball, goal.getXPos(), goal.getZPos());
						break;
					case("Hill-Climbing"):
						bot = new HillClimbingBot(ball);
						break;
					case("Manhattan"):
						bot = new ManhattanBot(ball);
						break;
					case("Newton"):
						bot = new NewtonBot(ball);
						break;
					default: bot = new RandomBot(ball);
				}
				double[] startVelocities = bot.calculateStartVelocities();

				tries = bot.getTries();
				shotsAmount++;

				ball.setPosition(previousBallPos.x, previousBallPos.y, previousBallPos.z);
				ball.setPhysics(startVelocities[0],startVelocities[1]);
				currentState = BALL_MOVING;
				new Thread(() -> {
					FramesQueue.clear();
					Physics.setBallMoving(true);
					switch (InputReader.getOdeSolver()) {
						case "Euler": {
							ODESolvers.eulerCalculationFrames(new double[]{ball.getXPos(), ball.getZPos(), ball.getXVelocity(), ball.getZVelocity(), 0}, 0.000001, 0.0166666666666666);
							FramesQueue.isDone = true;
							break;
						}
						case "Runge2": {
							ODESolvers.rungeKutta2Frames(new double[]{ball.getXPos(), ball.getZPos(), ball.getXVelocity(), ball.getZVelocity(), 0}, 0.000004, 0.016666666666666);
							FramesQueue.isDone = true;

							break;
						}
						case "Runge4": {
							ODESolvers.rungeKutta4Frames(new double[]{ball.getXPos(), ball.getZPos(), ball.getXVelocity(), ball.getZVelocity(), 0}, 0.000004, 0.01666666666666666);
							FramesQueue.isDone = true;

							break;
						}
					}
				}).start();
			}
		}

		//While we are in this state of the program, we simulate and render the movement of the ball over the golf course
		if(currentState == BALL_MOVING){
			ball.engine();

			if(Physics.hitWater(ball.getStateVector())){
				waterFallSound.play(1f);

				livesLeft -= 1;
				ball.setPosition(previousBallPos.x, previousBallPos.y, previousBallPos.z);
				currentState = WAIT_FOR_VELOCITIES;
				if(livesLeft == 0) currentState = GAME_OVER;
			}
			else if(Physics.hitTree(ball.getStateVector(), treeList)){
				outOfMapSound.play(0.4f);

				livesLeft -= 1;
				ball.setPosition(previousBallPos.x, previousBallPos.y, previousBallPos.z);
				currentState = WAIT_FOR_VELOCITIES;
				if(livesLeft == 0) currentState = GAME_OVER;
			}
			else if(Physics.outOfMap(ball.getStateVector())){
				outOfMapSound.play(0.4f);

				livesLeft -= 1;
				ball.setPosition(previousBallPos.x, previousBallPos.y, previousBallPos.z);
				currentState = WAIT_FOR_VELOCITIES;
				if(livesLeft == 0) currentState = GAME_OVER;
			}
			else if(Physics.isInTargetRegion(ball.getStateVector())){
				goalSound.play(0.4f);

				score++;
				shotsAmount = 0;
				ball.setPosition(InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
				ball.resetStateVector();
				currentState = WAIT_FOR_VELOCITIES;
				pointer = null; //reset the pointer
			}
			else if(!(Physics.getBallMoving())){
				currentState = WAIT_FOR_VELOCITIES;
				pointer = null; //reset the pointer
			}
		}

		//While we are in this state of the program, we reset the program to the initial configurations.
		if(currentState == GAME_OVER){
			gameOverSound.play(.4f);

			score = 0;
			shotsAmount = 0;
			livesLeft = 3;
			ball.setPosition(InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
			currentState = WAIT_FOR_VELOCITIES;
		}
	}

	/**
	 * Loads the initial values from the InputReader
	 */
	public void loadInitialValues(){
		InputReader.setInputs(new File("assets/inputFile.txt"));
		Physics.loadInitialValues();
	}

	/**
	 * Loads the camera and sets its initial properties
	 */
	public void createCamera(){
		camera = new PerspectiveCamera(75,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		cameraController = new FirstPersonCameraController(camera);
		cameraController.setVelocity(5);
		Gdx.input.setInputProcessor(cameraController);
		camera.update();

		camera.position.set(ball.getXPos(), ball.getYPos()+1, ball.getZPos()+1);
		camera.lookAt(ball.getPosition());

		camera.near =0.1f;
		camera.far = 100f;
	}

	/**
	 * Loads the terrain by creating a box for the water at height 0 and generates the grassmodels for the terrain by calling the createHeightMap function
	 */
	public void loadTerrain(){
		Model waterModel = modelBuilder.createBox(20, 0.19f, 20, new Material(ColorAttribute.createDiffuse(new Color(69 / 255f, 99 / 255f, 144 / 255f, 1))), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		waterInstance = new ModelInstance(waterModel, 0, -0.1f, 0);
		grassModels = Terrain.createHeightMap();
	}

	/**
	 * Creates a new ball from the properties given in the InputReader
	 */
	public void loadBall(){
		float ballRadius = (float)Ball.getBallRadius();
		Model ballModel = modelBuilder.createSphere(ballRadius, ballRadius, ballRadius, 15, 15,
				new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		ball = new Ball(ballModel, InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
	}

	/**
	 * Creates a new ball from the properties given in the InputReader
	 */
	public void loadGoal(){
		goal = new Goal(InputReader.getXt(), (float)HeightFunction.calculateHeight(InputReader.getXt(), InputReader.getYt()), InputReader.getYt());
	}

	/**
	 * Creates a new SpriteBatch and BitmapFont in which the text displayed on screen is stored
	 */
	private void loadScreenText(){
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
	}

	/**
	 * Calls the necessary methods to create the list of trees and the corresponding ModelCache
	 */
	public void loadTrees(){
		treeList = Tree.createTreeList(InputReader.getAmountOfTrees());
		treeModels = Tree.createTreeModels(treeList);
	}


	/**
	 * Creates a new pointer if it has not been made yet, and also takes care of the rotation of the pointer
	 */
	public void updatePointer(){
		if(pointer == null){
			pointerX = ball.getXPos() + 0.4f; //+0.6 is the length of the arrow
			pointerZ = ball.getZPos() + 0.4f; //+0.6 is the length of the arrow
		}

		Model pointerModel = modelBuilder.createArrow(ball.getXPos(), ball.getYPos(), ball.getZPos(), pointerX, (float)HeightFunction.calculateHeight(pointerX,pointerZ)+Terrain.getTerrainModelSize()/2+(float)Ball.getBallRadius()/2, pointerZ, 0.09f, 0.25f, 10, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		pointer = new ModelInstance(pointerModel);

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			pointerX = ball.getXPos() + (float) ((pointerX-ball.getXPos()) * Math.cos(0.02) - (pointerZ-ball.getZPos()) * Math.sin(0.02));
			pointerZ = ball.getZPos() + (float) ((pointerX-ball.getXPos()) * Math.sin(0.02) + (pointerZ-ball.getZPos()) * Math.cos(0.02));
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			pointerX = ball.getXPos() + (float) ((pointerX-ball.getXPos()) * Math.cos(0.02) + (pointerZ-ball.getZPos()) * Math.sin(0.02));
			pointerZ = ball.getZPos() + (float) (-(pointerX-ball.getXPos()) * Math.sin(0.02) + (pointerZ-ball.getZPos()) * Math.cos(0.02));
		}
		modelBatch.render(pointer);
	}

	/**
	 * Checks for the user input to increase or decrease the velocity
	 */
	public void updateVelocity(){
		double stepSize = 0.02;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			if(inputVelocity + stepSize <= 5.0){
				inputVelocity = Math.round((inputVelocity + stepSize) * 100.0) / 100.0;
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			if(inputVelocity -stepSize >= 0.0){
				inputVelocity = Math.round((inputVelocity - stepSize) * 100.0) / 100.0;
			}
		}
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		System.exit(0);
	}
	public static void setCurrentState(int state){
		currentState = state;
	}
	public static int getCurrentState(){
		return currentState;
	}

	public static ArrayList<Tree> getTreeList() {
		return treeList;
	}
}
