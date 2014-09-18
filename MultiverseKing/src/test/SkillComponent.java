/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class SkillComponent implements PersistentComponent {

    public enum SkillType {

        SUMMON,
        SPELL,
        TRAP;
    }
    private SkillType skillType;

    public SkillComponent(SkillType skillType) {
        this.skillType = skillType;
    }

    public SkillType getSkillType() {
        return skillType;
    }
}
