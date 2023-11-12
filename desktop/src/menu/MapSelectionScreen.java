package menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * MenuDesign Based on guide.
 * https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
 * in which the same skins and structure is used as our menu
 */
public class MapSelectionScreen implements Screen {
    private final MenuController parent;
    private final Stage stage;

    private final SelectBox<String> mapSelection;
    private final TextButton backButton,saveConfigButton;
    private final Label titleLabel;
    private final Label functionLabel;

    private Image previewImage;
    private final Image bigLakeImage, flatImage, slightlyTiltedImage, smallHillInCentreImage, smallLakeInCentreImage, mazeImage;
    private final TextField heightFunctionTextField;

    MapSelectionScreen(MenuController parent){
        this.parent = parent;

        stage = new Stage();
        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        backButton = new TextButton("Go Back", skin,"small");
        saveConfigButton = new TextButton("Save Configuration", skin,"small");

        bigLakeImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/BigLake.png")));
        flatImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/Flat.png")));
        slightlyTiltedImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/SlightlyTilted.png")));
        smallHillInCentreImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/SmallHillInCenter.png")));
        smallLakeInCentreImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/SmallLakeInCenter.png")));
        mazeImage = new Image(new Texture(Gdx.files.internal("Pre-made_terrains/Maze.png")));

        heightFunctionTextField = new TextField("0", skin);

        String[] maps = {"Flat","BigLake","SlightlyTilted", "SmallHillInCenter", "SmallLakeInCenter", "Maze", "Custom"};
        mapSelection = new SelectBox<>(skin);
        mapSelection.setItems(maps);
        mapSelection.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switch(mapSelection.getSelected()){
                    case "Flat" :
                        previewImage = flatImage;
                        heightFunctionTextField.setText("0");
                        break;
                    case "BigLake" :
                        previewImage = bigLakeImage;
                        heightFunctionTextField.setText("0.5*(Math.sin((x-y)/7)+0.9)");
                        break;
                    case "SlightlyTilted" :
                        previewImage = slightlyTiltedImage;
                        heightFunctionTextField.setText("0.1*x+1");
                        break;
                    case "SmallHillInCenter" :
                        previewImage = smallHillInCentreImage;
                        heightFunctionTextField.setText("Math.exp(-(x*x+y*y)/40)");
                        break;
                    case "SmallLakeInCenter" :
                        previewImage = smallLakeInCentreImage;
                        heightFunctionTextField.setText("0.4*(0.9-Math.exp(-(x*x+y*y)/8))");
                        break;
                    case "Maze":
                        previewImage = mazeImage;
                        heightFunctionTextField.setText("(0.8*(y/(x*x+y*y)+0.7*Math.sin(y)*1.5*Math.cos(2*x)))*Math.cos(x*y) + 0.2");
                        break;
                    case "Custom" :
                        previewImage = null;
                        heightFunctionTextField.setText("");
                        break;
                }

                parent.switchScreens(MenuController.MAP);
                render(Gdx.graphics.getDeltaTime());
            }
        }));

        titleLabel = new Label("Select map", skin);
        functionLabel = new Label("Function", skin);

        previewImage = flatImage;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        backButton.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.switchScreens(MenuController.PREFERENCES);
            }
        }));

        //This action listener saves all the information, update the input file by using the input reader class
        // and closes the application
        saveConfigButton.addListener((new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String heightFunction = "";
                switch(mapSelection.getSelected()){
                    case "Flat": heightFunction += "0";
                        break;
                    case "BigLake": heightFunction += "0.5*(Math.sin((x-y)/7)+0.9)";
                        break;
                    case "SlightlyTilted": heightFunction += "0.1*x+1";
                        break;
                    case "SmallHillInCenter": heightFunction += "Math.exp(-(x*x+y*y)/40)";
                        break;
                    case "SmallLakeInCenter": heightFunction += "0.4*(0.9-Math.exp(-(x*x+y*y)/8))";
                        break;
                    case "Maze": heightFunction += "(0.8*(y/(x*x+y*y)+0.7*Math.sin(y)*1.5*Math.cos(2*x)))*Math.cos(x*y) + 0.2";
                        break;
                    case "Custom": heightFunction += heightFunctionTextField.getText();
                        break;
                }

                //Saving all the values from the actors
                main.InputReader.setHeightProfile(heightFunction);
                main.InputReader.updateInputFile();
                main.InputReader.createHeightFunctionClass(heightFunction);

                System.exit(0);
            }}));


        table.row().pad(60,0,0,0);
        table.add(titleLabel).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(mapSelection).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(previewImage).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(functionLabel).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(heightFunctionTextField).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(saveConfigButton).colspan(2);
        table.row().pad(10,0,0,0);
        table.add(backButton).colspan(2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
    public void dispose() {}
}
