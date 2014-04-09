package test;

import hexsystem.events.ChunkChangeEvent;
import hexsystem.events.ChunkChangeListener;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;

/**
 *
 * @author Eike Foede
 */
public class TestListener implements ChunkChangeListener, TileChangeListener{

    public void chunkUpdate(ChunkChangeEvent event) {
        System.out.println("chunkUpdate with pos " + event.getChunkPos().toString());
    }

    public void tileChange(TileChangeEvent event) {
        System.out.println("tileChange with tilepos " + event.getTilePos().getAsOffset().toString());
    }
    
}
