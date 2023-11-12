package menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * MenuDesign Based on guide.
 * https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
 * in which the same skins and structure is used as our menu
 */
public class MainMenuScreen implements Screen {
    private final MenuController parent;
    private final Stage stage;
    private final Skin skin;

    /**
     * The Constructor of the starting screen.In which all the actors are initialized.
     * @param controller gives this screen access to the controller class, so it can change screens
     */
    public MainMenuScreen(MenuController controller){
        parent = controller;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
    }

    /**
     * This method constructs the table, sets the input processor and adds all the actors to the table.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //Creates the buttons for the three options
        // newGame starts a new game
        // preferences allows you to change your settings
        // Exit closes the application
        TextButton newGame = new TextButton("Play", skin);
        TextButton preferencesTextButton = new TextButton("Settings",skin);
        TextButton exit = new TextButton("Exit",skin);

        //this section adds all the action listeners to the buttons
        exit.addListener((new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        }));

        preferencesTextButton.addListener((new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.switchScreens(MenuController.PREFERENCES);
            }
        }));

        newGame.addListener((new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.switchScreens(MenuController.APPLICATION);
            }
        }));

        //Adds all the actors to the table
        table.add(newGame).fillX().uniformX();
        table.row().pad(10,0,10,0);
        table.add(preferencesTextButton).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();
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
    public void dispose() {
        stage.dispose();
    }
}
