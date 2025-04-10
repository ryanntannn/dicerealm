/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.dicerealm.core.skills;

import com.dicerealm.core.combat.ActionType;
import com.dicerealm.core.entity.EntityClass;

/**
 *
 * @author Darren
 */
public class MagicMissile extends Skill {
    public MagicMissile() {
        super("Magic Missile", "A missile of pure magic", EntityClass.WIZARD, ActionType.MAGIC, 3,3,2,2);
    }
}
