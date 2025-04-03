package com.dicerealm.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dicerealm.core.command.combat.CombatTurnActionCommand;
import com.dicerealm.core.item.Potion;
import com.dicerealm.core.item.Scroll;
import com.dicerealm.core.item.Weapon;
import com.dicerealm.core.item.weapons.IronSword;
import com.dicerealm.core.monster.Monster;
import com.dicerealm.core.player.Player;
import com.dicerealm.core.skills.Fireball;
import com.dicerealm.core.skills.Skill;
import com.dicerealm.server.strategy.GsonSerializer;

@SpringBootTest
class ServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test 
	void canSerializeAndDeserializeCommands() {
		GsonSerializer serializer = new GsonSerializer();
		Player testPlayer = new Player();
		Monster testEnemy = new Monster();
		Skill testSkill = new Fireball();
		testPlayer.addSkill(testSkill);
		String json = serializer.serialize(new CombatTurnActionCommand(testPlayer, testEnemy, testSkill, CombatTurnActionCommand.ActionType.SKILL));
		CombatTurnActionCommand command = serializer.deserialize(json, CombatTurnActionCommand.class);
		assert command.getAttacker().getId().equals(testPlayer.getId());
		assert command.getTarget().getId().equals(testEnemy.getId());
		assert command.getAction() instanceof Skill;

		Weapon testWeapon = new IronSword(1);

		json = serializer.serialize(new CombatTurnActionCommand(testPlayer, testEnemy, testWeapon, CombatTurnActionCommand.ActionType.WEAPON));

		command = serializer.deserialize(json, CombatTurnActionCommand.class);

		assert command.getAttacker().getId().equals(testPlayer.getId());
		assert command.getTarget().getId().equals(testEnemy.getId());
		assert command.getAction() instanceof Weapon;

		Scroll scroll = new Scroll("Fireball", "Spit a fireball", 1, 2);
		json = serializer.serialize(new CombatTurnActionCommand(testPlayer, testEnemy, scroll, CombatTurnActionCommand.ActionType.SCROLL));
		command = serializer.deserialize(json, CombatTurnActionCommand.class);
		assert command.getAttacker().getId().equals(testPlayer.getId());
		assert command.getTarget().getId().equals(testEnemy.getId());
		assert command.getAction() instanceof Scroll;
		assert command.getActionType() == CombatTurnActionCommand.ActionType.SCROLL;

		Potion potion = new Potion("Healing Potion", "Heals 10 HP", 1, 2);
		json = serializer.serialize(new CombatTurnActionCommand(testPlayer, testEnemy, potion, CombatTurnActionCommand.ActionType.POTION));
		command = serializer.deserialize(json, CombatTurnActionCommand.class);
		assert command.getAttacker().getId().equals(testPlayer.getId());
		assert command.getTarget().getId().equals(testEnemy.getId());
		assert command.getAction() instanceof Potion;
		assert command.getActionType() == CombatTurnActionCommand.ActionType.POTION;
	}
}
