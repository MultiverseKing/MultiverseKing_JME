/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.attribut;

import java.util.ArrayList;

/**
 *
 * @author roah
 */
public class Faction {
    private ArrayList<String> factionList = new ArrayList<String>();
    
    public Faction(){

    }
    
    public Faction(ArrayList<String> factionList){
        this.factionList = factionList;
    }
    
    /**
     * @return faction list registered.
     */
    public ArrayList<String> getFactionList(){
        return factionList;
    }
    
    /**
     * Check if the asked faction exist in the current context.
     * @param faction you looking for.
     * @return if exist.
     */
    public boolean exist(String faction){
        return factionList.contains(faction);
//        for(String f : factionList){
//            if(f == faction){
//                return true;
//            }
//        }
//        return false;
    }
}
