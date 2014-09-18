package test;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import kingofmultiverse.MultiverseMain;

/**
 *
 * @author roah
 */
public class Player extends AbstractAppState implements ActionListener {

    private MultiverseMain main;
    private BulletAppState bulletAppState;
    private BetterCharacterControl player_phys;
    // track directional input, so we can walk left-forward etc
    private boolean left = false, right = false, up = false, down = false, run = false;
    private Vector3f walkDirection = new Vector3f(0, 0, 0); // stop
    private float airTime = 0;
    private float speed;

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.main = (MultiverseMain) app;
        bulletAppState = new BulletAppState();
        main.getStateManager().attach(bulletAppState);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        // Load any model
        Node player = (Node) main.getAssetManager().loadModel("Models/Characters/Model21.j3o");
        player.setName("Player");
        player.setLocalTranslation(0, 1, 0);
        // Create a appropriate physical shape for it
        player_phys = new BetterCharacterControl((float) 1.5, 3, (float) 0.1);
        // Attach physical properties to model and PhysicsSpace
        player.addControl(player_phys);

        Node terrain = (Node) main.getAssetManager().loadModel("Scenes/testMap.j3o");
        terrain.addControl(new RigidBodyControl(0));

        main.getRootNode().attachChild(player);
        main.getRootNode().attachChild(terrain);

        bulletAppState.getPhysicsSpace().add(player_phys);
        bulletAppState.getPhysicsSpace().add(terrain);
        bulletAppState.setDebugEnabled(true);
        cameraSettup(player);
        setInput();
    }

    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        speed = (run) ? 5f : 1f;
        Vector3f camDir = main.getCamera().getDirection().clone().multLocal(speed);
        Vector3f camLeft = main.getCamera().getLeft().clone().multLocal(speed);
        camDir.y = 0;
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }

        if (!player_phys.isOnGround()) {
            airTime = airTime + tpf;
        } else {
            airTime = 0;
        }
        player_phys.setWalkDirection(walkDirection); // THIS IS WHERE THE WALKING HAPPENS
    }

    private void cameraSettup(Node target) {
        // Enable a chase cam for this target (typically the player).
        ChaseCamera chaseCam = new ChaseCamera(main.getCamera(), target, main.getInputManager());
        chaseCam.setLookAtOffset(new Vector3f(0f, 1.5f, 0f));
        chaseCam.setSmoothMotion(true);
    }

    private void setInput() {
        // configure mappings, e.g. the WASD keys
        main.getInputManager().addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_Q));
        main.getInputManager().addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        main.getInputManager().addMapping("CharForward", new KeyTrigger(KeyInput.KEY_Z));
        main.getInputManager().addMapping("CharBackward", new KeyTrigger(KeyInput.KEY_S));
        main.getInputManager().addMapping("CharJump", new KeyTrigger(KeyInput.KEY_SPACE));
        main.getInputManager().addMapping("CharRun", new KeyTrigger(KeyInput.KEY_LSHIFT));
        main.getInputManager().addMapping("GetPosition", new KeyTrigger(KeyInput.KEY_E));
        main.getInputManager().addMapping("CharAttack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(this, "CharLeft", "CharRight");
        main.getInputManager().addListener(this, "CharForward", "CharBackward");
        main.getInputManager().addListener(this, "CharJump", "CharAttack", "CharRun", "GetPosition");
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("CharRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("CharForward")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("CharBackward")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        } else if (binding.equals("CharJump")) {
            player_phys.jump();
        }
        if (binding.equals("CharAttack")) {
            attack();
        }
        if (binding.equals("GetPosition")) {
            getPlayerPosition();
        }
        if (binding.equals("CharRun")) {
            if (value) {
                run = true;
            } else {
                run = false;
            }
        }
    }

    private void attack() {
        System.out.println("Attack");
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    private void getPlayerPosition() {
        System.out.print((int) main.getRootNode().getChild("Player").getLocalTranslation().x + " + ");
        System.out.print((int) main.getRootNode().getChild("Player").getLocalTranslation().y + " + ");
        System.out.println((int) main.getRootNode().getChild("Player").getLocalTranslation().z);
    }
}
