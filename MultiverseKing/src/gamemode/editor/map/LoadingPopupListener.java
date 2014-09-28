package gamemode.editor.map;

import gamemode.gui.DialogWindowListener;

/**
 *
 * @author roah
 */
public interface LoadingPopupListener extends DialogWindowListener {

    public boolean loadForCurrent(String dialogUID, String value);
}
