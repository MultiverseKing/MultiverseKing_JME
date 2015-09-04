package org.multiverseking.loader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @todo Use for game configuration
 * @author roah
 */
public class JSONLoader implements AssetLoader {

    private final static JSONParser parser = new JSONParser();

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
        JSONObject data = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                    stringBuilder.append(s);
            }
            data = (JSONObject) parser.parse(stringBuilder.toString());
        } catch (ParseException ex) {
            Logger.getLogger(JSONLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bufferedReader != null) {
                    try {
                            bufferedReader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(JSONLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            is.close();
        }

        if (data != null) {
            return data;
        } else {
            Logger.getGlobal().log(Level.WARNING, "{0} : Data couldn't be loaded.", new Object[]{getClass().getName()});
            return null;
        }
    }
}
