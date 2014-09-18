package test;

/**
 *
 * @author roah
 */
public class ObjectComponent {
    public enum ObjectType{
        CONSOMABLE,
        WEAPON,
        ARMOR;
    }
    private ObjectType objType;

    public ObjectComponent(ObjectType objType) {
        this.objType = objType;
    }

    public ObjectType getObjType() {
        return objType;
    }
}
