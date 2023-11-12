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
public class SettingsScreen implements Screen {
    private final MenuController parent;
    private final Stage stage;

    private final TextButton advancedSettingsButton, mapSelectionButton, saveConfigButton;

    private final Label settingsLabel, physicsLabel, odeTypeLabel, gameTypeLabel, botTypeLabel, errorLabel;

    final SelectBox<String> playerTypes, ODEType,botType, physicsType, errorType;
    final Skin skin;

    /**
     * The Constructor of the preferences screen.In which all the actors are initialized.
     * @param controller gives this screen access to the controller class, so it can change screens
     */
    public SettingsScreen(MenuController controller){
        main.InputReader.setInputs(new File("assets/inputFile.txt"));
        this.parent = controller;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        //Setting the selectBoxes with a StringArray of options
        String[] physicsOptions = {"Normal", "Advanced"};
        physicsType = new SelectBox<>(skin);
        physicsType.setItems(physicsOptions);
        physicsType.setSelected(main.InputReader.getPhysicsType());


        String[] errorOptions = {"None","BallPosition","FoundVelocities"};
        errorType = new SelectBox<>(skin);
        errorType.setItems(errorOptions);
        errorType.setSelected(main.InputReader.getErrorType());

        String[] gameType = {"Player","Bot", "Testing"};
        playerTypes = new SelectBox<>(skin);
        playerTypes.setItems(gameType);
        playerTypes.setSelected(main.InputReader.getGameType());

        String[] ODEOptions = {"Euler","Runge2","Runge4"};
        ODEType = new SelectBox<>(skin);
        ODEType.setItems(ODEOptions);
        ODEType.setSelected(main.InputReader.getOdeSolver());

        String[] botOptions = {"Brute-Force","Random","Rule-Based","Hill-Climbing","Manhattan","Newton"};
        botType = new SelectBox<>(skin);
        botType.setItems(botOptions);
        botType.setSelected(main.InputReader.getBotType());

        //The rest initialization of the actors
        advancedSettingsButton = new TextButton("Advanced Settings",skin,"small");
        mapSelectionButton = new TextButton("Use Pre-Made map",skin,"small");
        saveConfigButton = new TextButton("Save Configuration",skin,"small");

        settingsLabel = new Label("Settings",skin);

        physicsLabel = new Label( "Physics Type", skin );
        odeTypeLabel = new Label( "ODE Solver", skin );
        gameTypeLabel = new Label( "Game Type", skin );
        botTypeLabel = new Label( "Bot Type", skin );
        errorLabel = new Label("Error Type",skin);
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

        //This action listener switches the screen to the fileCreation screen
        advancedSettingsButton.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateInputs();
                parent.switchScreens(MenuController.FILE);
            }
        }));

        mapSelectionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateInputs();
                parent.switchScreens(MenuController.MAP);
            }
        });

        saveConfigButton.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 updateInputs();
                 parent.switchScreens(MenuController.MENU);
             }
         });


        //In this part, all the actors are added to the table.
        table.add(settingsLabel).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(odeTypeLabel).left();
        table.add(ODEType).right();
        table.row().pad(10,0,0,0);
        table.add(gameTypeLabel).left();
        table.add(playerTypes).right();
        table.row().pad(10,0,0,0);
        table.add(botTypeLabel).left();
        table.add(botType).right();
        table.row().pad(10,0,0,0);
        table.add(physicsLabel).left();
        table.add(physicsType).right();
        table.row().pad(10,0,0,0);
        table.add(errorLabel).left();
        table.add(errorType).right();
        table.row().pad(40,0,0,0);
        table.add(advancedSettingsButton).left();
        table.add(mapSelectionButton).right();
        table.row().pad(40,0,0,0);
        table.add(saveConfigButton).colspan(2);
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
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    /**
     * This method update all the inputs in the InputReader class based on the selected choices
     */
    public void updateInputs(){
        String newPlayerType = botType.getSelected();
        String newPlayerOrBot = playerTypes.getSelected();
        String newAlgorithmType = ODEType.getSelected();
        String newPhysicsType = physicsType.getSelected();
        String newErrorType = errorType.getSelected();


        main.InputReader.setPlayer(newPlayerOrBot, newPlayerType, newAlgorithmType);
        main.InputReader.setPhysicsType(newPhysicsType);
        main.InputReader.setErrorType(newErrorType);
        main.InputReader.updateInputFile();
    }

}
