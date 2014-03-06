package hexsystem.pathfinding;

import hexsystem.HexTile;
import hexsystem.MapData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede
 */
public class Dijkstra implements Pathfinder {

    private MapData mapData;

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    public List<HexCoordinate> getPath(HexCoordinate from, HexCoordinate to) {

        HashSet<HexCoordinate> visitedFields = new HashSet<HexCoordinate>();
        ArrayList<WayPoint> actualPoints = new ArrayList<WayPoint>();

        WayPoint first = new WayPoint(from, null);

        actualPoints.add(first);
        visitedFields.add(from);

        while (!actualPoints.isEmpty()) {
            ArrayList<WayPoint> nextPoints = new ArrayList<WayPoint>();
            for (WayPoint point : actualPoints) {
                HexCoordinate[] neighbours = point.thisPoint.getNeighbours();
                for (HexCoordinate next : neighbours) {
                    if (!visitedFields.contains(next)) {
                        if (isPassable(point.thisPoint, next)) {
                            if (to.equals(next)) {
                                //WAY IS FOUND
                                ArrayList<HexCoordinate> way = new ArrayList<HexCoordinate>();
                                way.add(next);
                                while (point != null) {
                                    way.add(0, point.thisPoint);
                                    point = point.lastPoint;
                                }
                                return null;
                            }
                            WayPoint newPoint = new WayPoint(next, point);
                            nextPoints.add(newPoint);
                            visitedFields.add(next);
                        }
                    }
                }
            }
            actualPoints = nextPoints;
        }
        return null;
    }

    private boolean isPassable(HexCoordinate from, HexCoordinate to) {
        boolean passable = true;
        if (from.distanceTo(to) != 1) {
            passable = false;
        }
        HexTile a = mapData.getTile(from);
        HexTile b = mapData.getTile(to);
        if (a != null && b != null) {
            if (Math.abs(a.getHeight() - b.getHeight()) > 2) {
                passable = false;
            }
        } else {
            passable = false;
        }
        return passable;
    }
}
