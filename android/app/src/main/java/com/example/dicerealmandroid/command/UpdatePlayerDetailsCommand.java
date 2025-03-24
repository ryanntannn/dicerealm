package com.example.dicerealmandroid.command;

import com.example.dicerealmandroid.core.entity.Entity;
import com.example.dicerealmandroid.core.item.InventoryOf;
import com.example.dicerealmandroid.core.item.Item;
import com.example.dicerealmandroid.core.player.Player;
import com.example.dicerealmandroid.core.skill.Skill;

public class UpdatePlayerDetailsCommand extends Command {
    public Player player;
    public UpdatePlayerDetailsCommand(Player player) {
        super("UPDATE_PLAYER_DETAILS");
        this.player = player;
    }
}

