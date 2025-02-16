import { z } from "zod";

export const bodyPartSchema = z.enum([
  "HEAD",
  "NECK",
  "TORSO",
  "LEGS",
  "LEFT_HAND",
  "RIGHT_HAND",
] as const);

export const itemSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  description: z.string(),
});

export const inventorySchema = z.object({
  inventorySize: z.number(),
  items: z.array(itemSchema),
});

export const playerSchema = z.object({
  id: z.string(),
  displayName: z.string(),
  equippedItems: z.map(bodyPartSchema, z.unknown()).or(z.object({})),
  health: z.number(),
  maxHealth: z.number(),
  inventory: inventorySchema,
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
