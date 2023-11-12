package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Goal extends ModelCache {
    private final float xPos;
    private final float yPos;
    private final float zPos;

    public Goal(float xPos, float yPos, float zPos){
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;

        float poleHeight = 2f;
        float flagHeight = 0.25f;
        float flagWidth = 0.75f;
        float poleRadius = 0.05f;
        float scoreRadiusHeight = 0.1f;
        float terrainSize = Terrain.getTerrainModelSize()/2;

        ModelBuilder builder = new ModelBuilder();

        Model poleModel = builder.createCylinder(poleRadius, poleHeight, poleRadius, 8, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance poleInstance = new ModelInstance(poleModel, xPos, yPos + poleHeight/2 + terrainSize, zPos);

        Model scoreRadiusModel = builder.createCylinder(InputReader.getR()*2, scoreRadiusHeight, InputReader.getR()*2, 20, new Material(ColorAttribute.createDiffuse(Color.SALMON)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance scoreRadiusInstance = new ModelInstance(scoreRadiusModel, xPos, yPos + scoreRadiusHeight/2 + terrainSize, zPos);

        Model blueFlagModel = builder.createBox(flagWidth, flagHeight, 0f, new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model yellowFlagModel = builder.createBox(flagWidth, flagHeight, 0f, new Material(ColorAttribute.createDiffuse(255f, 215f, 0f, 1f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        ModelInstance blueFlagInstance = new ModelInstance(blueFlagModel, xPos + flagWidth/2, yPos + poleHeight + terrainSize - flagHeight/2, zPos);
        ModelInstance yellowFlagInstance = new ModelInstance(yellowFlagModel, xPos + flagWidth/2, yPos + poleHeight + terrainSize - 3*flagHeight/2, zPos);

        this.begin();
        this.add(scoreRadiusInstance);
        this.add(blueFlagInstance);
        this.add(yellowFlagInstance);
        this.add(poleInstance);
        this.end();
    }

    public float getXPos() {
        return xPos;
    }
    public float getYPos() {
        return yPos;
    }
    public float getZPos() {
        return zPos;
    }

}
