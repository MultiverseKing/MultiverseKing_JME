package hexsystem.pathfinding;

import hexsystem.MapData;
import java.util.List;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede
 */
public interface Pathfinder {

    public void setMapData(MapData mapData);

    public List<HexCoordinate> getPath(HexCoordinate from, HexCoordinate to);
}
