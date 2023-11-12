package main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Tree extends ModelCache {

    private static final float treeTrunkRadius = 0.25f;
    private static final float treeTrunkHeight = 2.0f;
    private static final float treeTopRadius = 1f;

    private final float xPos;
    private final float zPos;

    public Tree(float xPos, float zPos){
        this.xPos = xPos;
        float yPos = (float) HeightFunction.calculateHeight(xPos, zPos);
        this.zPos = zPos;

        float terrainSize = Terrain.getTerrainModelSize()/2;

        ModelBuilder builder = new ModelBuilder();

        Model treeTrunk = builder.createCylinder(treeTrunkRadius*2,treeTrunkHeight,treeTrunkRadius*2,20,new Material(ColorAttribute.createDiffuse(Color.BROWN), new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.9f)),VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance treeTrunkInstance = new ModelInstance(treeTrunk, xPos, yPos + treeTrunkHeight/2+terrainSize-0.2f,zPos);

        Model treeTops = builder.createSphere(treeTopRadius*2,treeTopRadius*2,treeTopRadius*2,16,16,new Material(ColorAttribute.createDiffuse(Color.FOREST), new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.75f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance treeTopsInstance = new ModelInstance(treeTops,xPos, yPos +treeTrunkHeight+treeTopRadius+terrainSize-0.4f,zPos);

        this.begin();
        this.add(treeTrunkInstance);
        this.add(treeTopsInstance);
        this.end();
    }

    /**
     * Creates an ArrayList of Tree objects
     * @param numberOfTrees the amount of trees to be created
     * @return an ArrayList with the Tree objects, stored as ModelCaches
     */
    public static ArrayList<Tree> createTreeList(int numberOfTrees){
        ArrayList<Tree> trees = new ArrayList<>();
        final double bound = 10 - treeTrunkRadius;
        float xPos;
        float zPos;

        for(int i = 0; i < numberOfTrees;){
            xPos = (float)ThreadLocalRandom.current().nextDouble(-bound, bound);
            zPos = (float)ThreadLocalRandom.current().nextDouble(-bound, bound);

            //We only place the tree if it's in a valid location
            if(isValidPlacement(xPos, zPos, trees)){
                trees.add(new Tree(xPos, zPos));
                i++;
            }
        }
        return trees;
    }

    /**
     * Create a single ModelCache containing all the trees
     * @param trees the array of ModelCaches representing the trees
     * @return ModelCache containing all trees
     */
    public static ModelCache createTreeModels(ArrayList<Tree> trees){
        ModelCache treesModelCache = new ModelCache();

        treesModelCache.begin();
        for (Tree tree : trees) {
            treesModelCache.add(tree);
        }
        treesModelCache.end();

        return treesModelCache;
    }

    /**
     * Checks if it's allowed to place a tree in the current position
     * @param xPos the XPosition to check for
     * @param zPos the ZPosition to check for
     * @param trees an ArrayList containing all previously placed trees
     * @return true if the tree has no overlap and false otherwise
     */
    public static boolean isValidPlacement(float xPos, float zPos, ArrayList<Tree> trees){
        float xDistance;
        float zDistance;

        for (Tree tree : trees) {

            //Check for overlap between trees
            xDistance = xPos - tree.xPos;
            zDistance = zPos - tree.zPos;
            if(Math.sqrt(xDistance * xDistance + zDistance * zDistance) <= 2 * treeTrunkRadius) {
                return false;
            }

            //Check if the tree is in the water
            if(HeightFunction.calculateHeight(xPos, zPos) < 0){
                return false;
            }

            //Check if the tree is inside the target region
            xDistance = InputReader.getXt() - tree.xPos;
            zDistance = InputReader.getYt() - tree.zPos;
            if(Math.sqrt(xDistance * xDistance + zDistance * zDistance) <= treeTrunkRadius + InputReader.getR()){
                return false;
            }

            //Check if the tree is inside the initial ball position
            xDistance = InputReader.getX0() - tree.xPos;
            zDistance = InputReader.getY0() - tree.zPos;
            if(Math.sqrt(xDistance * xDistance + zDistance * zDistance) <= treeTrunkRadius + Ball.getBallRadius()){
                return false;
            }
        }
        return true;
    }

    public float getXPos() {
        return xPos;
    }

    public float getZPos() {
        return zPos;
    }

    public static float getTreeTrunkRadius() {
        return treeTrunkRadius;
    }
}
