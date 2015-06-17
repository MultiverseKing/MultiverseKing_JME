package org.multiverseking.loader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.IOUtils;

/**
 * @todo Use for game configuration
 * @author roah
 */
public class JSONLoader implements AssetLoader {

    private static JSONParser parser = new JSONParser();

    /**
     * Load the file using the binary importer.
     *
     * @param assetInfo
     * @return
     * @throws IOException
     */
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        InputStream is = assetInfo.openStream();
        String s = new String(IOUtils.readFully(is, -1, true));
        JSONObject data = null;
        try {
            data = (JSONObject) parser.parse(s);
            is.close();
        } catch (ParseException ex) {
            Logger.getLogger(JSONLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (data != null) {
            return data;
        } else {
            Logger.getGlobal().log(Level.WARNING, "{0} : Data couldn't be loaded.", new Object[]{getClass().getName()});
            return null;
        }
    }
}
