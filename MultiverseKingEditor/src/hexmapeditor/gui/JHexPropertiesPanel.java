package hexmapeditor.gui;

import gui.control.ComboBoxRenderer;
import gui.control.JPropertiesPanel;
import hexmapeditor.HexMapSystem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.events.TileSelectionListener;
import org.hexgridapi.utility.HexCoordinate;
import core.EditorMain;

/**
 *
 * @author roah
 */
public class JHexPropertiesPanel extends JPropertiesPanel {

    private final EditorMain editorMain;
    private HexMapSystem editorSystem;
    private MouseControlSystem mouseSystem;
    private Boolean currentIsGhost;
    private boolean currentIsGroup = false;
    private JPanel tileProperties;
    private HashMap<String, JComponent> comps = new HashMap<>();
    private boolean update = true;

    public JHexPropertiesPanel(EditorMain editorMain) {
        super(editorMain.getAssetManager().loadTexture(
                "Textures/Icons/Buttons/configKey.png").getImage(), "HexMapConfig");
        this.editorMain = editorMain;
        mouseSystem = editorMain.getStateManager().getState(MouseControlSystem.class);
        editorSystem = editorMain.getStateManager().getState(HexMapSystem.class);

        editorMain.getStateManager().getState(MouseControlSystem.class).getSelectionControl().registerTileListener(selectionListener);
        editorMain.getStateManager().getState(MapDataAppState.class).getMapData().registerTileChangeListener(tileListener);
        
        buildMenu();
    }

    private void buildMenu() {
        setBorder(BorderFactory.createTitledBorder("Map Property"));
        setPreferredSize(new Dimension(170, 300));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addComp(separator);
        
        add(new JCursorPanel(editorMain.getStateManager().getState(MouseControlSystem.class)));
        
        JCheckBox box = new JCheckBox(new AbstractAction("Show ghost") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
        box.setSelected(true);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        box.setAlignmentX(0);
        addComp(box);
        
        JLabel mapNameLabel = new JLabel("Map Name : ");
        mapNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        add(mapNameLabel);

        JTextField mapName = new JTextField(editorSystem.getMapName());
        mapName.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setMapName(((JTextField)comps.get("mapName")).getText());
                        return null;
                    }
                });
            }
        });
        comps.put("mapName", mapName);
        mapName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mapName.setAlignmentX(0);
        add(mapName);

        /*-------       Map Generator       ------*/
        JLabel mapGenerator = new JLabel("Random Generator : ");
        mapGenerator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        add(mapGenerator);

        JPanel seedPan = new JPanel();
        seedPan.setLayout(new BoxLayout(seedPan, BoxLayout.LINE_AXIS));
        seedPan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        seedPan.setAlignmentX(0);

        JLabel currentSeed = new JLabel("Seed : " + String.valueOf(editorSystem.getSeed()));
        comps.put("currentSeed", currentSeed);
        seedPan.add(currentSeed);
        seedPan.add(Box.createRigidArea(new Dimension(5, 0)));
        add(seedPan);

        /*-------*/
        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 2));
        add(separator);
    }

    private void onAction(ActionEvent e) {
        if (e.getActionCommand().contains("comboBox") && update) {
            final int value = Integer.valueOf(e.getActionCommand().split("\\.")[1]);
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    editorSystem.setTilePropertiesTexTure(editorSystem.getTextureValueFromKey(value));
                    return null;
                }
            });
            return;
        } else if (e.getActionCommand().contains("comboBox")) {
            update = true;
            return;
        }
        switch (e.getActionCommand()) {
            case "GenerateMap":
                editorSystem.generateFromSeed();
                break;
            case "Show ghost":
                //TODO
                System.err.println("TODO : " + e.getActionCommand());
                break;
            case "Destroy":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.removeTile();
                        return null;
                    }
                });
                break;
            case "btnUp":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setTilePropertiesUp();
                        return null;
                    }
                });
                break;
            case "btnDown":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setTilePropertiesDown();
                        return null;
                    }
                });
                break;
            case "Generate/Reset":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setNewTile();
                        return null;
                    }
                });
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList) {
            if (currentIsGhost == null || !selectedList.isEmpty() != currentIsGroup
                    || !editorSystem.tileExist() != currentIsGhost) {
                if (!selectedList.isEmpty()) {
                    buildMultiTileMenu(selectedList);
                } else {
                    buildSingleTileMenu();
                }
            } else {
                if (!selectedList.isEmpty()) {
//                    updateMultiTileMenu(selectedList);
                } else {
                    updateSingleTileMenu();
                }
            }
        }
    };
    private TileChangeListener tileListener = new TileChangeListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            if (!currentIsGroup && events.length == 1
                    && events[0].getTilePos().equals(mouseSystem.getSelectionControl().getSelectedPos())) {
                if (!editorSystem.tileExist() != currentIsGhost) {
                    buildSingleTileMenu();
                } else {
                    updateSingleTileMenu();
                }
            }
        }
    };

    private void buildTileMenu() {

        currentIsGhost = !editorSystem.tileExist();
        if (tileProperties == null) {
            tileProperties = new JPanel();
            tileProperties.setLayout(new BoxLayout(tileProperties, BoxLayout.PAGE_AXIS));
            tileProperties.setAlignmentX(0);
            tileProperties.setBorder(BorderFactory.createTitledBorder("Tile Property"));
            addComp(tileProperties);
        } else {
            tileProperties.removeAll();
        }
    }

    private void buildSingleTileMenu() {
        buildTileMenu();
        // Component Value
        int compCount = 1;
        currentIsGroup = false;
        if (currentIsGhost) {
            compCount += addGenerateBtn();
        } else {
            compCount += addTextureList();
            compCount += addHeightBtn(false);
            compCount += addDestroyBtn();
            tileProperties.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        tileProperties.setMaximumSize(new Dimension(getMaximumSize().width, compCount * 23 + 10));
        revalidate();
    }

    private void buildMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
        buildTileMenu();
        currentIsGroup = !selectedList.isEmpty();
        int compCount = 1;
        compCount += addTextureList();
        compCount += addHeightBtn(true);
        compCount += addGenerateBtn();
        compCount += addDestroyBtn();
        tileProperties.setMaximumSize(new Dimension(getMaximumSize().width, compCount * 23 + 10));

        revalidate();
    }

    // <editor-fold defaultstate="collapsed" desc="Add Component Method">
    private int addGenerateBtn() {
        if (!comps.containsKey("generate")) {
            JButton generate = new JButton(new AbstractAction("Generate/Reset") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            addComp(tileProperties, generate);
            comps.put("generate", generate);
        } else {
            addComp(tileProperties, comps.get("generate"));
        }
        return 1;
    }

    private int addHeightBtn(boolean isMulti) {
        if (!comps.containsKey("heightPanel")) {
            JPanel heightPanel = new JPanel();
            heightPanel.setAlignmentX(0);
            heightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            heightPanel.setLayout(new BorderLayout());
            BasicArrowButton btn = new BasicArrowButton(BasicArrowButton.NORTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnUp"));
                }
            });
            heightPanel.add(btn, BorderLayout.NORTH);
            btn = new BasicArrowButton(BasicArrowButton.SOUTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnDown"));
                }
            });
            heightPanel.add(btn, BorderLayout.SOUTH);
            JLabel height;
            if (!isMulti) {
                height = new JLabel("height : " + editorSystem.getTileHeight());
            } else {
                height = new JLabel("height : undefined");
            }
            heightPanel.add(height, BorderLayout.CENTER);
            comps.put("height", height);
            addComp(tileProperties, heightPanel);
            heightPanel.validate();
            comps.put("heightPanel", heightPanel);
        } else {
            addComp(tileProperties, comps.get("heightPanel"));
            if (!isMulti) {
                ((JLabel) comps.get("height")).setText("height : " + editorSystem.getTileHeight());
            } else {
                ((JLabel) comps.get("height")).setText("height : undefined");
            }
        }
        return 3;
    }

    private int addTextureList() {
        if (!comps.containsKey("textureList")) {
            ComboBoxRenderer combo = new ComboBoxRenderer(
                    editorMain.getAssetManager(), editorSystem.getTextureKeys());
            JComboBox textureList = new JComboBox(combo.getArray());
            textureList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), e.getActionCommand() + "." + ((JComboBox) comps.get("textureList")).getSelectedIndex()));
                }
            });
            textureList.setRenderer(combo);
            textureList.setMaximumRowCount(4);
            textureList.setAlignmentX(0);
            textureList.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, textureList);
            comps.put("textureList", textureList);
        } else {
            addComp(tileProperties, comps.get("textureList"));
            update = false;
            int var = editorSystem.getTextureKeys().indexOf(editorSystem.getTileTextureKey());
            ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
        }
        return 1;
    }

    private int addDestroyBtn() {
        if (!comps.containsKey("destroy")) {
            JButton destroy = new JButton(new AbstractAction("Destroy") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            destroy.setAlignmentX(0);
            destroy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, destroy);
            comps.put("destroy", destroy);
        } else {
            addComp(tileProperties, comps.get("destroy"));
        }
        return 1;
    }

    // </editor-fold>
    private void updateSingleTileMenu() {
        currentIsGhost = !editorSystem.tileExist();
        if (!currentIsGhost) {
            ((JLabel) comps.get("height")).setText("height : " + editorSystem.getTileHeight());
            update = false;
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int var = editorSystem.getTextureKeys().indexOf(editorSystem.getTileTextureKey());
                    ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
                    return null;
                }
            });
        }
        revalidate();
    }

    private void updateMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
//        currentIsGroup = !selectedList.isEmpty();
        System.err.println("update group");
//        hexMapPanel.validate();
    }

    public JComponent getComponent(String UID) {
        return comps.get(UID);
    }

    @Override
    public void isShow() {
    }

    @Override
    public void isHidden() {
    }
}
