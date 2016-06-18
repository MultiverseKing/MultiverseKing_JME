package org.multiverseking.battle.battleSystem.gui.utils;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.text.Label;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author t0neg0d
 */
public final class Layout {
	public static Vector2f pos = new Vector2f();
	public static Vector2f dim = new Vector2f();
	public static float
		pad = 5, x = 0, y = 0, w = 120, h = 20,
		lWidthN = 125, lWidthM = 75, lWidthS = 25,
		sbWidth = 200, floatW = 40, bWidth = 80;
	
	public static Vector2f getPos() { return Layout.pos; }
	public static Vector2f getDim() { return Layout.dim; }
	public static void reset() {
		x = 0;
		y = 0;
		pos.set(x,y);
	}
	public static void updatePos() { pos.set(x,y); }
	public static void incRow() {
		x = 0;
		y += h;
		pos.set(x,y);
	}
	public static void incCol(Element el) {
		Layout.x += el.getWidth()+pad;
		pos.set(x,y);
	}
	public static Label getNewLabel(ElementManager screen, String text) {
		Label l = new Label(screen, pos, dim);
		l.setText(text);
		l.setTextAlign(BitmapFont.Align.Right);
		l.setTextVAlign(BitmapFont.VAlign.Center);
		l.setFontSize(16);
		return l;
	}
}
