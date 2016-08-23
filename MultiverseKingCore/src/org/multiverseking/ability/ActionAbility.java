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
package org.multiverseking.ability;

import org.multiverseking.field.collision.CollisionData;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.utility.ElementalAttribut;

/**
 * Store ability data
 * @author roah
 */
public class ActionAbility {

    private final String name;
    private final Animation animation;
    private final int power;
    private final int cost;
    private final ElementalAttribut eAttribut;
    private final String description;
    private final CollisionData castRange;
    private final CollisionData effectRange;

    public ActionAbility(String name, Animation animation, int power, int cost, 
            ElementalAttribut eAttribut, String description, CollisionData castRange, CollisionData effectRange) {
        this.name = name;
        this.animation = animation;
        this.power = power;
        this.cost = cost;
        this.eAttribut = eAttribut;
        this.description = description;
        this.castRange = castRange;
        this.effectRange = effectRange;
    }
    
    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public int getCost() {
        return cost;
    }

    public ElementalAttribut geteAttribut() {
        return eAttribut;
    }

    public String getDescription() {
        return description;
    }

    public CollisionData getCastRange() {
        return castRange;
    }

    public CollisionData getEffectRange() {
        return effectRange;
    }

    public Animation getAnimation() {
        return animation;
    }
}
