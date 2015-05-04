package org.hexgridapi.core.mesh;

import java.util.ArrayList;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
final class GreddyMeshingElementMeshData {

    /**
     * Mesh parameter needed to generate the mesh.
     */
    private ArrayList<Vector2Int> position = new ArrayList<Vector2Int>();
    private ArrayList<Vector2Int> size = new ArrayList<Vector2Int>();
    private ArrayList<Integer> height = new ArrayList<Integer>();
    private int readIndex = -1;

    GreddyMeshingElementMeshData(Vector2Int position, Integer height) {
        add(position, height);
    }

    void add(Vector2Int position, Integer height) {
        this.position.add(position);
        if (height == null) {
            height = 0;
        }
        this.height.add(height);
        this.size.add(new Vector2Int(1, 1));
    }

    Vector2Int getPosition() {
        return position.get(readIndex);
    }

    Vector2Int getSize() {
        return size.get(readIndex);
    }

    int getHeight() {
        return height.get(readIndex);
    }

    boolean hasNext() {
        readIndex++;
        if (position.size() > readIndex) {
            return true;
        } else {
            return false;
        }
    }

    Vector2Int getLastAddedPosition() {
        return position.get(position.size() - 1);
    }

    Vector2Int getLastAddedSize() {
        return size.get(size.size() - 1);
    }

    void expandSizeX() {
        size.get(size.size() - 1).x++;
    }

    void expandSizeY() {
        size.get(size.size() - 1).y++;
    }

    int size() {
        return position.size();
    }
}
