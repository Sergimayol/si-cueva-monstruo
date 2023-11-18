package gui;

import entities.CaveEditor;
import environment.TileData;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CaveInfo implements Serializable {

    private static final String basePath = "test_maps/";

    private TileData[][] tilesData;
    private Tile[][] tiles;

    public CaveInfo(TileData[][] tilesData, Tile[][] tiles) {
        this.tilesData = tilesData;
        this.tiles = tiles;
    }

    public TileData[][] getTilesData() {
        return tilesData;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public static CaveInfo useCave(String path) {
        String filePath = path;
        if (!path.endsWith(".map")) {
            filePath = path + ".map";
        }

        filePath = basePath + filePath;

        CaveInfo caveInfo = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(filePath));
            caveInfo = (CaveInfo) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return caveInfo;
    }

    public void saveCave(String path) {
        String filePath = path;
        if (!path.endsWith(".map")) {
            filePath = path + ".map";
        }

        filePath = basePath + filePath;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                CaveEditor entity = tiles[i][j].getEntity();
                if (entity != null) {
                    entity.restoreEntity();
                }
            }
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
