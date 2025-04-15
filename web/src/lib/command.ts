import { z } from "zod";
import {
  dialogueTurnSchema,
  entityClassSchema,
  itemSchema,
  playerActionSchema,
  playerSchema,
  raceSchema,
  roomStateSchema,
} from "./room-state";

export const outgoingMessageCommandSchema = z.object({
  type: z.literal("OUTGOING_MESSAGE"),
  message: z.string(),
  senderName: z.string(),
  messageId: z.string(),
});

export const messageHistoryCommandSchema = z.object({
  type: z.literal("MESSAGE_HISTORY"),
  messages: z.array(
    z.object({
      message: z.string(),
      senderName: z.string(),
      messageId: z.string(),
    })
  ),
});

export const playerJoinCommandSchema = z.object({
  type: z.literal("PLAYER_JOIN"),
  player: playerSchema,
});

export const playerLeaveCommandSchema = z.object({
  type: z.literal("PLAYER_LEAVE"),
  playerId: z.string(),
});

export const fullRoomStateCommandSchema = z.object({
  type: z.literal("FULL_ROOM_STATE"),
  state: roomStateSchema,
  myId: z.string(),
});

export const showPlayerActionsCommandSchema = z.object({
  type: z.literal("SHOW_PLAYER_ACTIONS"),
  actions: z.array(playerActionSchema),
});

export const playerEquipItemResponseSchema = z.object({
  type: z.literal("PLAYER_EQUIP_ITEM_RESPONSE"),
  playerId: z.string(),
  item: itemSchema,
  bodyPart: z.string(),
  updatedPlayerStats: z.record(z.number()),
});

export const changeLocationCommandSchema = z.object({
  type: z.literal("CHANGE_LOCATION"),
  location: z.object({
    displayName: z.string(),
    description: z.string(),
  }),
});

export const startGameCommandSchema = z.object({
  type: z.literal("START_GAME"),
});

export const setPlayerDetailsCommandSchema = z.object({
  type: z.literal("UPDATE_PLAYER_DETAILS_REQUEST"),
  displayName: z.string(),
  race: raceSchema,
  entityClass: entityClassSchema,
  baseStats: z.record(z.number()),
});

export const updatePlayerDetailsCommandSchema = z.object({
  type: z.literal("UPDATE_PLAYER_DETAILS"),
  player: playerSchema,
});

export const dialogueStartTurnCommandSchema = z.object({
  type: z.literal("DIALOGUE_START_TURN"),
  dialogueTurn: dialogueTurnSchema,
});

export const dialogueEndTurnCommandSchema = z.object({
  type: z.literal("DIALOGUE_END_TURN"),
  turnNumber: z.number().int(),
});

export const dialogueTurnActionCommandSchema = z.object({
  type: z.literal("DIALOGUE_TURN_ACTION"),
  turnNumber: z.number().int(),
  playerId: z.string(),
  action: z.string(),
  skillCheck: z.record(z.number()),
});

export const combatStartCommandSchema = z.object({
  type: z.literal("COMBAT_START"),
  initiativeResults: z.array(
    z.object({
      totalInitiative: z.number(),
      initiativeLog: z.string(),
      entity: z.object({
        id: z.string(),
        displayName: z.string(),
      }),
    })
  ),
});

export const combatStartTurnCommandSchema = z.object({
  type: z.literal("COMBAT_START_TURN"),
  currentTurnEntityId: z.string(),
});

export const combatEndCommandSchema = z.object({
  type: z.literal("COMBAT_END"),
});

export const updateThemeCommandSchema = z.object({
  type: z.literal("UPDATE_THEME"),
  theme: z.string(),
});

export const commandSchema = z.discriminatedUnion("type", [
  outgoingMessageCommandSchema,
  messageHistoryCommandSchema,
  playerJoinCommandSchema,
  playerLeaveCommandSchema,
  fullRoomStateCommandSchema,
  showPlayerActionsCommandSchema,
  playerEquipItemResponseSchema,
  changeLocationCommandSchema,
  startGameCommandSchema,
  setPlayerDetailsCommandSchema,
  updatePlayerDetailsCommandSchema,
  dialogueStartTurnCommandSchema,
  dialogueEndTurnCommandSchema,
  dialogueTurnActionCommandSchema,
  combatStartCommandSchema,
  combatEndCommandSchema,
  combatStartTurnCommandSchema,
  updateThemeCommandSchema,
]);
