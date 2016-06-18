package org.multiverseking.render.utility;

import java.util.List;
import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 *
 * @author roah
 * @deprecated The movement system handle the curve internaly
 */
public class Curve {

    private final float speed;
    private final List<HexCoordinate> waypoints;
    private final CurveType curveType;

    public Curve(CurveType curveType, float speed, List<HexCoordinate> waypoints) {
        this.waypoints = waypoints;
        this.curveType = curveType;
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public CurveType getCurveType() {
        return curveType;
    }

    public List<HexCoordinate> getWaypoints() {
        return waypoints;
    }

    public enum CurveType {

        BEZIER,
        EASE_IN,
        EASE_OUT,
        LINEAR;
    }
}
