package entitysystem.field;

/**
 * Needed to activate some special input. (dash/burst etc...)
 * @author roah
 */
public class ATBBurstComponent extends ATBComponent {
    
    public ATBBurstComponent(byte max) {
        super(max);
    }
    
    public ATBBurstComponent(byte current, byte max) {
        super(current, max);
    }
}
