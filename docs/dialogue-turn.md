# Dialogue Turn Based System

## Overview

The dialogue turn-based system is a system that allows players to batch their actions and execute them in a turn-based manner. This system is used in games where players need to make decisions and interact with the game world through dialogue.

## Sequence Diagram

The following sequence diagram illustrates the flow of interactions between the player, the game server, and the game world in a dialogue turn-based system:

```mermaid

sequenceDiagram
		participant Player
		participant Room
		participant GameWorld

		Room->>Player: Send START_TURN command
		Player->>Room: Player 1 sends PLAYER_ACTION command
		Player->>Room: Player 2 sends PLAYER_ACTION command
		Player->>Room: Player 3 sends PLAYER_ACTION command
		Room->>Room: Detect either all players have sent actions or the turn time has expired
		Room->>Player: Send END_TURN command
		Room->>DungeonMaster: Process turn actions
		DungeonMaster->>Room: Send responses
		Room->>Player: Send responses
		Room->>Player: Send START_TURN command

```
