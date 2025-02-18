import { z } from "zod";

export const bodyPartSchema = z.enum([
  "HEAD",
  "NECK",
  "TORSO",
  "LEGS",
  "LEFT_HAND",
  "RIGHT_HAND",
]);

export const statSchema = z.enum([
  "MAX_HEALTH",
  "ARMOUR_CLASS",
  "STRENGTH",
  "DEXTERITY",
  "CONSTITUTION",
  "INTELLIGENCE",
  "WISDOM",
  "CHARISMA",
]);

export const playerActionSchema = z.object({
  action: z.string(),
  skillCheck: z.record(z.number()),
});

export const itemSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  description: z.string(),
  suitableBodyParts: z.array(bodyPartSchema).optional(),
  stats: z.record(z.number()).optional(),
});

export const inventorySchema = z.object({
  inventorySize: z.number(),
  items: z.array(itemSchema),
});

export const playerSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  equippedItems: z.record(itemSchema),
  health: z.number(),
  inventory: inventorySchema,
  baseStats: z.record(z.number()),
  stats: z.record(z.number()),
});

export const roomStateSchema = z.object({
  playerMap: z.record(playerSchema),
  messages: z.array(
    z.object({
      messageId: z.string(),
      message: z.string(),
      senderName: z.string(),
    })
  ),
});

export type RoomState = z.infer<typeof roomStateSchema>;
export type Player = z.infer<typeof playerSchema>;
export type PlayerAction = z.infer<typeof playerActionSchema>;
