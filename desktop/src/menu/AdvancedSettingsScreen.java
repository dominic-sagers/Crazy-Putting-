package menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;

/**
 * MenuDesign Based on guide.
 * https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
 * in which the same skins and structure is used as our menu
 */
public class AdvancedSettingsScreen implements Screen {
    private final MenuController parent;
    private final Stage stage;
    final TextField initialBallPosXText,initialBallPosYText,initialGoalPosXText,
                    initialGoalPosYText,goalRadiusText,kineticFrictionText,staticFrictionText, amountOfTreesText;

    private final TextButton saveConfigButton, backButton;
    private final Label initialBallPosXLabel, initialBallPosYLabel, initialGoalPosXLabel,
                        initialGoalPosYLabel, goalRadiusLabel, kineticFrictionLabel, staticFrictionLabel, amountOfTreesLabel;

    private final SettingsScreen preferences;

    /**
     * The Constructor of the file creation screen.In which all the actors are initialized.
     * @param parent gives this screen access to the controller class, so it can change screens
     * @param preferences gives this screen access to the preferences screen, so it can get the value from the select-boxes
     */
    public AdvancedSettingsScreen(MenuController parent, SettingsScreen preferences){
        this.parent = parent;
        this.preferences = preferences;

        stage = new Stage(new ScreenViewport());
        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        saveConfigButton = new TextButton("Save Configuration", skin,"small");
        backButton = new TextButton("Back", skin,"small");

        initialBallPosXLabel = new Label("Initial Ball Position X", skin);
        initialBallPosYLabel = new Label("Initial Ball Position Y", skin);
        initialGoalPosXLabel = new Label("Initial Goal Position X", skin);
        initialGoalPosYLabel = new Label("Initial Goal Position Y", skin);
        goalRadiusLabel = new Label("Goal radius", skin);
        kineticFrictionLabel = new Label("Kinetic Friction", skin);
        staticFrictionLabel = new Label("Static Friction", skin);
        amountOfTreesLabel = new Label("Amount of Trees",skin);

        main.InputReader.setInputs(new File("assets/inputFile.txt"));

        initialBallPosXText = new TextField(String.valueOf(main.InputReader.getX0()), skin);
        initialBallPosYText = new TextField(String.valueOf(main.InputReader.getY0()), skin);
        initialGoalPosXText = new TextField(String.valueOf(main.InputReader.getXt()), skin);
        initialGoalPosYText = new TextField(String.valueOf(main.InputReader.getYt()), skin);
        goalRadiusText = new TextField(String.valueOf(main.InputReader.getR()), skin);
        kineticFrictionText = new TextField(String.valueOf(main.InputReader.getMuk()), skin);
        staticFrictionText = new TextField(String.valueOf(main.InputReader.getMus()), skin);
        amountOfTreesText = new TextField(String.valueOf(main.InputReader.getAmountOfTrees()),skin);
    }

    /**
     * This method constructs the table, sets the input processor and adds all the actors to the table.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        //This action listener saves all the information, update the input file by using the input reader class
        // and closes the application
        saveConfigButton.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateInputs();
                parent.switchScreens(MenuController.MENU);
            }
        }));

        //Goes back to the preferences screen
        backButton.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateInputs();
                parent.switchScreens(MenuController.PREFERENCES);
            }
        }));

        //In this part, all the actors are added to the table.
        table.add(initialBallPosXLabel).left();
        table.add(initialBallPosXText).right();
        table.row().pad(10,0,0,0);
        table.add(initialBallPosYLabel).left();
        table.add(initialBallPosYText).right();
        table.row().pad(10,0,0,0);
        table.add(initialGoalPosXLabel).left();
        table.add(initialGoalPosXText).right();
        table.row().pad(10,0,0,0);
        table.add(initialGoalPosYLabel).left();
        table.add(initialGoalPosYText).right();
        table.row().pad(10,0,0,0);
        table.add(goalRadiusLabel).left();
        table.add(goalRadiusText).right();
        table.row().pad(10,0,0,0);
        table.add(kineticFrictionLabel).left();
        table.add(kineticFrictionText).right();
        table.row().pad(10,0,0,0);
        table.add(staticFrictionLabel).left();
        table.add(staticFrictionText).right();
        table.row().pad(10,0,0,0);
        table.add(amountOfTreesLabel).left();
        table.add(amountOfTreesText).right();
        table.row().pad(40,0,0,0);
        table.add(saveConfigButton).left();
        table.add(backButton).right();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    /**
     * This method update all the inputs in the InputReader class based on inputted values
     */
    public void updateInputs(){
        //Saving all the values from the actors
        float initialBallPosX = Float.parseFloat(initialBallPosXText.getText());
        float initialBallPosY = Float.parseFloat(initialBallPosYText.getText());

        float initialGoalPosX = Float.parseFloat(initialGoalPosXText.getText());
        float initialGoalPosY = Float.parseFloat(initialGoalPosYText.getText());

        float goalRadius = Float.parseFloat(goalRadiusText.getText());

        float kineticFriction = Float.parseFloat(kineticFrictionText.getText());
        float staticFriction = Float.parseFloat(staticFrictionText.getText());

        String newPlayerType = preferences.botType.getSelected();
        String newPlayerOrBot = preferences.playerTypes.getSelected();
        String newAlgorithmType = preferences.ODEType.getSelected();

        int amountOfTrees = Integer.parseInt(amountOfTreesText.getText());

        //Setting everything in the input reader
        main.InputReader.setInitialBallPos(initialBallPosX,initialBallPosY);
        main.InputReader.setGoalPos(initialGoalPosX,initialGoalPosY);
        main.InputReader.setScoreRadius(goalRadius);
        main.InputReader.setFrictions(kineticFriction,staticFriction);
        main.InputReader.setPlayer(newPlayerOrBot,newPlayerType,newAlgorithmType);
        main.InputReader.setAmountOfTrees(amountOfTrees);

        main.InputReader.updateInputFile();
    }

}
