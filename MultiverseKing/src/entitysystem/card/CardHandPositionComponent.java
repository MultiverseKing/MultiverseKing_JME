/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

/**
 * Position of the card in the player hand.
 * @author roah
 */
public class CardHandPositionComponent {
    private byte position = 0;

    public CardHandPositionComponent(byte handPosition) {
        this.position = handPosition;
    }

    public byte getPosition() {
        return position;
    }
}
