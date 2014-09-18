package test;

/**
 *
 * @author roah
 */
public class SummonComponent {
    public enum SummonType{
        TITAN,
        INCARNATE,
        TRAP;
    }
    
    private SummonType summonType;

    public SummonComponent(SummonType summonType) {
        this.summonType = summonType;
    }

    public SummonType getSummonType() {
        return summonType;
    }
}
