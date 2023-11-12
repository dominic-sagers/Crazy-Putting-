package menu;

import com.badlogic.gdx.Game;
import main.Launch;

/**
 * MenuDesign Based on guide.
 * https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
 * in which the same skins and structure is used as our menu
 */
public class MenuController extends Game{

    private SettingsScreen preferencesScreen;
    private MainMenuScreen mainMenuScreen;
    private Launch mainScreen;
    private AdvancedSettingsScreen fileCreationScreen;
    private MapSelectionScreen mapSelectionScreen;

    public final static int MENU = 0, PREFERENCES = 1, APPLICATION = 2, FILE = 3, MAP = 4;

    /**
     * Acts like the constructor of this controller class. this class is used to switch between the screens
     */
    @Override
    public void create () {
        MainMenuScreen mainMenuScreenS = new MainMenuScreen(this);
        this.setScreen(mainMenuScreenS);
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {}

    /**
     * This method switches the screens based on an Integer values;
     * MENU = 0;
     * PREFERENCES = 1;
     * APPLICATION = 2;
     * FILE = 3;
     * @param screen the integer value based on the final = Integers
     */
    public void switchScreens(int screen){
        switch (screen){
            case MENU:
                if(mainMenuScreen == null) mainMenuScreen = new MainMenuScreen(this);
                this.setScreen(mainMenuScreen);
                break;
            case PREFERENCES:
                if(preferencesScreen == null) preferencesScreen = new SettingsScreen(this);
                this.setScreen(preferencesScreen);
                break;
            case APPLICATION:
                if(mainScreen == null) mainScreen = new Launch();
                this.setScreen(mainScreen);
                break;
            case FILE:
                if(fileCreationScreen == null) fileCreationScreen = new AdvancedSettingsScreen(this,this.preferencesScreen);
                this.setScreen(fileCreationScreen);
                break;
            case MAP:
                if(mapSelectionScreen == null) mapSelectionScreen = new MapSelectionScreen(this);
                this.setScreen(mapSelectionScreen);
                break;
        }
    }
}
