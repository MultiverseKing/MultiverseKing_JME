package gui;

import com.simsilica.es.EntityId;
import com.simsilica.es.PersistentComponent;
import entitysystem.field.HealthComponent;
import entitysystem.field.InfluenceComponent;
import entitysystem.render.RenderComponent.RenderType;
import java.util.ArrayList;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class EntityStatsWindow extends EditorWindow {
    
    private EntityId currentId = null;
    
    public EntityStatsWindow(Screen screen, Element parent) {
        super(screen, parent, "Stats ");
    }
    
    public void show(EntityId id, RenderType renderType, ArrayList<PersistentComponent> comps) {
        if (id != currentId) {
            populate(renderType, comps);
            super.showConstrainToParent(VAlign.bottom, HAlign.left);
            currentId = id;
        }
    }
    
    @Override
    public void onPressCloseAndHide() {
    }
    
    private void populate(RenderType renderType, ArrayList<PersistentComponent> comps) {
        switch (renderType) {
            case Ability:
                break;
            case Core:
                HealthComponent hp = (HealthComponent) comps.get(0);
                addLabelPropertieField("Health Point", new Vector2Int(hp.getCurrent(), hp.getMax()), HAlign.left);
                InfluenceComponent inf = (InfluenceComponent) comps.get(1);
                addLabelPropertieField("Influence range", inf.getRange(), HAlign.left);
                break;
            case Debug:
                break;
            case Environment:
                break;
            case Equipement:
                break;
            case Titan:
                break;
            case Unit:
                break;
        }
    }
}
