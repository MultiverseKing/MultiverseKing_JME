package org.hexgridapi.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hexgridapi.base.HexTile;
import org.hexgridapi.base.MapData;
import org.hexgridapi.utility.HexCoordinate;

/**
 * A* algorithm, should be faster than Dijkstra.
 *
 * @author Eike Foede
 */
public class Astar implements Pathfinder {

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
        ArrayList<WayPoint> actualPoints[] = new ArrayList[3];
        for (int i = 0; i < actualPoints.length; i++) {
            actualPoints[i] = new ArrayList<WayPoint>();

        }
        WayPoint first = new WayPoint(from, null);

        actualPoints[0].add(first);
        visitedFields.add(from);

        while (!(actualPoints[0].isEmpty() && actualPoints[1].isEmpty() && actualPoints[2].isEmpty())) {

            ArrayList<WayPoint> tmp = actualPoints[0];
            actualPoints[0] = actualPoints[1];
            actualPoints[1] = actualPoints[2];
            actualPoints[2] = new ArrayList<WayPoint>();
            for (WayPoint point : tmp) {
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
                            int diff = next.distanceTo(to) - point.thisPoint.distanceTo(to);
                            if (diff < 0) {
                                actualPoints[0].add(newPoint);
                            } else if (diff == 0) {
                                actualPoints[1].add(newPoint);
                            } else {
                                actualPoints[2].add(newPoint);
                            }
                            visitedFields.add(next);
                        }
                    }
                }
            }
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
