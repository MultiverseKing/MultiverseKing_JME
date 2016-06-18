package org.multiverseking.utility;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author roah
 */
public class SimpleMath {

    /**
     * Substract two value using their absolute value.
     *
     * @result absolute value of the result
     */
    public static float substractAbs(float value1, float value2) {
        return FastMath.abs(FastMath.abs(value1) - FastMath.abs(value2));
    }

    /**
     * Substract two vector using their absolute value.
     *
     * @result absolute value of the result
     */
    public static Vector2f substractAbs(Vector2f value1, Vector2f value2) {
        Vector2f result = new Vector2f();
        result.x = substractAbs(value2.x, value1.x);
        result.y = substractAbs(value1.y, value2.y);
        return result;
    }

    /**
     * Substract two vector using their absolute value.
     *
     * @result absolute value of the result
     */
    public static Vector3f substractAbs(Vector3f value1, Vector3f value2) {
        Vector3f result = new Vector3f();
        result.x = substractAbs(value1.x, value2.x);
        result.y = substractAbs(value1.y, value2.y);
        result.z = substractAbs(value1.z, value2.z);
        return result;
    }
}
