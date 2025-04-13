package com.dicerealm.core.skills;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.EntityClass;

/**
 *
 * @author Darren
 */
public class DummySkill extends Skill {
    
    public DummySkill() {
        super("Dummy Skill", "A dummy skill", EntityClass.WIZARD, ActionType.MAGIC, 0, 1, 1, 5);
    }
    

}
