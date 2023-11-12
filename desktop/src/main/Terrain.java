package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * This class contains the functions for calculating the height of the terrain at a given position and for generating the terrain.
 */
public class Terrain {

    private static final float stepSize = 0.05f;
    private static final float terrainModelSize = 0.1f;
    /**
     * Creates the terrain corresponding to the input function, based on the stepSize and the terrainModelSize
     * @return a ModelCache containing models that represent the terrain
     */
    public static ModelCache createHeightMap(){
        ModelCache modelCache = new ModelCache();

        ModelBuilder modelBuilder = new ModelBuilder();
        Model grassBox = modelBuilder.createBox(terrainModelSize,terrainModelSize,terrainModelSize,new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);

        modelCache.begin();
        for(float x = -10; x < 10; x+= stepSize){
            for(float z = -10; z < 10; z+= stepSize){
                float height = (float) HeightFunction.calculateHeight(x,z);
                if(height >= 0) {
                    ModelInstance grassBoxInstance = new ModelInstance(grassBox, x, height, z);
                    grassBoxInstance.materials.get(0).set(ColorAttribute.createDiffuse(getColourFromHeight(height)));

                    modelCache.add(grassBoxInstance);

                }
            }
        }
        modelCache.end();
        return modelCache;
    }

    /**
     * Creates a color based on the given height, used to make a difference between the color of high and low terrain
     * @param height the height for which the color has to be calculated
     * @return the color corresponding to the given height
     */
    public static Color getColourFromHeight(float height) {
            float r=(50*height+50f)/255;
            float g=(50*height+100f)/255;
            return new Color(r,g,0,1f);
    }

    public static float getTerrainModelSize() {
        return terrainModelSize;
    }
}
