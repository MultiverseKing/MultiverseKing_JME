package entitysystem.render.utility;

/**
 *
 * @author roah
 */
public class Curve {

    private final float speed;
    private final CurveType curveType;

    public Curve(CurveType curveType, float speed) {
        this.curveType = curveType;
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public CurveType getCurveType() {
        return curveType;
    }

    public enum CurveType {

        LINEAR;
    }
}
