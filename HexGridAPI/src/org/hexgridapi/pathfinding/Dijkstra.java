package org.hexgridapi.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.utility.HexCoordinate;

/**
 * Dijkstra algorithm
 *
 * @author Eike Foede
 */
public class Dijkstra implements Pathfinder {

    private MapData mapData;

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    /**
     * Returns a shortest path between the two given HexCoordinates, if there is
     * any. Else returns null
     *
     * @param from
     * @param to
     * @return shortest path or null, if no path found
     */
    public List<HexCoordinate> getPath(HexCoordinate from, HexCoordinate to) {
//        long time = System.nanoTime();
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
//                                System.out.println((System.nanoTime() - time)/100000);
                                return way;
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
        return null;//If no way is found
    }

    private boolean isPassable(HexCoordinate from, HexCoordinate to) {
        boolean passable = true;
        if (from.distanceTo(to) != 1) {
            passable = false;
        }
        HexTile a = mapData.getTile(from);
        HexTile b = mapData.getTile(to);
        if (a != null && b != null) {
            if (Math.abs(a.getHeight() - b.getHeight()) > 1) {
                passable = false;
            }
        } else {
            passable = false;
        }
        return passable;
    }
}
