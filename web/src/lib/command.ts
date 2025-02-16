import { z } from "zod";

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
  playerId: z.string(),
});

export const playerLeaveCommandSchema = z.object({
  type: z.literal("PLAYER_LEAVE"),
  playerId: z.string(),
});

export const commandSchema = z.discriminatedUnion("type", [
  outgoingMessageCommandSchema,
  messageHistoryCommandSchema,
  playerJoinCommandSchema,
  playerLeaveCommandSchema,
]);
