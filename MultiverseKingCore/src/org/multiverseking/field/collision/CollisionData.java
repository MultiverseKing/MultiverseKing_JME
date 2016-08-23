/*
 * Copyright (C) 2016 roah
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.multiverseking.field.collision;

import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 *
 * @author roah
 */
public class CollisionData {

    private final Type type;
    private final int layer;
    private final int min;
    private final int max;
    private final HexCoordinate[] position;

    /**
     * Set the range type to Self.
     */
    public CollisionData(int layer) {
        this.layer = layer;
        this.type = Type.SELF;
        this.min = 0;
        this.max = 0;
        this.position = null;
    }

    /**
     * @param type Type of collision to use
     * @param min Min position for it
     * @param max Max position
     */
    public CollisionData(int layer, Type type, int min, int max) {
        this.layer = layer;
        this.type = type;
        this.min = min;
        this.max = max;
        this.position = null;
    }

    /**
     * rangeType == RangeType.CUSTOM
     *
     * @param position where the collision trigger
     */
    public CollisionData(int layer, HexCoordinate... position) {
        this.layer = layer;
        this.type = Type.CUSTOM;
        this.min = 0;
        this.max = 0;
        this.position = position;
    }

    public int getLayer() {
        return layer;
    }

    public Type getType() {
        return type;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public HexCoordinate[] getPosition() {
        return position;
    }

    public enum Type {
        CIRCLE,
        SQUARE,
        LINE,
        SELF,
        CUSTOM
    }

}
