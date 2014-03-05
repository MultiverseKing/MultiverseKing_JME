/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import hexsystem.HexTile;
import hexsystem.MapData;
import java.util.ArrayList;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshParameter {
    private final MapData mapData;
    private final HexCoordinate subChunkWorldGridPos;
    
    private ArrayList<Vector2Int> position = new ArrayList<Vector2Int>();
    private ArrayList<Vector2Int> size = new ArrayList<Vector2Int>();
    private ArrayList <Byte> elementType = new ArrayList<Byte>();
    private ArrayList <Byte> height = new ArrayList<Byte>();

    public MeshParameter(MapData mapData, HexCoordinate subChunkWorldGridPos) {
        this.mapData = mapData;
        this.subChunkWorldGridPos = subChunkWorldGridPos;
    }

    public void add(Vector2Int position, Vector2Int size, byte elementType, byte height) {
        this.position.add(position);
        this.size.add(size);
        this.elementType.add(elementType);
        this.height.add(height);
    }
    
    public void extendsSizeX(int i) {
        size.get(i).x++;
    }
    
    public Vector2Int getPosition(int i){
        return position.get(i);
    }

    public Vector2Int getSize(int i) {
        return size.get(i);
    }

    public byte getElementType(int i) {
        return elementType.get(i);
    }

    public byte getHeight(int i) {
        return height.get(i);
    }

    public int size() {
        return this.size.size();
    }

    public Boolean[][] getCulling(int i) {
        Boolean[][] neightborsCull = new Boolean[size.get(i).x][6];
        for(int j = 0; j < size.get(i).x; j++){
            HexTile[] neightbors = mapData.getNeightbors(new HexCoordinate(HexCoordinate.OFFSET,position.get(i).x+subChunkWorldGridPos.q+j, position.get(i).y+subChunkWorldGridPos.r));
            for(byte k = 0; k < 6; k++){
                if(neightbors[k] != null){
                        if(neightbors[k].getHeight() >= height.get(i)){
                            neightborsCull[j][k] = false;
                        } else if(neightbors[k].getHeight() == height.get(i)) {
                            neightborsCull[j][k] = false;
                        } else {
                            neightborsCull[j][k] = true;
                        }
                    }
                else {
                    neightborsCull[j][k] = false;
                }
            }
        }
        return neightborsCull;
    }
}
