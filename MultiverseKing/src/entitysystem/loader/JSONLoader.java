/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.loader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.IOUtils;

/**
 *
 * @author roah
 */
public class JSONLoader implements AssetLoader, AssetLocator {

    private String rootPath;
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
            return new PropertiesLoader(data);
        }
        return null;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        return new AssetInfo(manager, key) {
            @Override
            public InputStream openStream() {
                try {
                    return new FileInputStream(new File(rootPath + key.getName()));
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        };
    }
}
