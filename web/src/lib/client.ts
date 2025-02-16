import { useEffect, useMemo, useState } from "react";
import useWebSocket from "react-use-websocket";
import { commandSchema } from "./command";
import { Player } from "./room-state";

const SERVER_URL = import.meta.env.VITE_SERVER_URL as string;

type Message = {
  message: string;
  senderName: string;
  messageId: string;
};

export function useRoomClient(
  roomId: string,
  options: { serverUrl?: string } = { serverUrl: SERVER_URL }
) {
  const wsUrl = useMemo(() => {
    const url = new URL(options.serverUrl ?? window.location.href);
    const currentUrl = new URL(window.location.href);
    if (currentUrl.protocol === "https:") {
      url.protocol = "wss:";
    } else {
      url.protocol = "ws:";
    }
    url.pathname = `/room/${roomId}`;
    return url.toString();
  }, [options.serverUrl, roomId]);

  const { sendJsonMessage, lastJsonMessage, readyState } = useWebSocket(wsUrl);

  const [messages, setMessages] = useState<Message[]>([]);
  const [players, setPlayers] = useState<Record<string, Player>>({});
  const [myId, setMyId] = useState<string | null>(null);
  const [actions, setActions] = useState<string[]>([]);

  const myPlayer = useMemo(() => {
    if (!myId) return null;
    return players[myId];
  }, [myId, players]);

  const sendTextMessage = (message: string) => {
    sendJsonMessage({ type: "MESSAGE", message });
  };

  useEffect(() => {
    const { data: command, error } = commandSchema.safeParse(lastJsonMessage);
    if (error) {
      console.error(lastJsonMessage, error);
      setMessages((messages) => [
        ...messages,
        {
          message: "Something went wrong",
          senderName: "System",
          messageId: crypto.randomUUID(),
        },
      ]);
      return;
    }
    switch (command.type) {
      case "MESSAGE_HISTORY":
        setMessages(command.messages);
        break;
      case "OUTGOING_MESSAGE":
        setMessages((messages) => [...messages, command]);
        break;
      case "PLAYER_JOIN":
        setMessages((messages) => [
          ...messages,
          {
            message: `Player joined: ${command.player.displayName}`,
            senderName: "System",
            messageId: crypto.randomUUID(),
          },
        ]);

        setPlayers((players) => ({
          ...players,
          [command.player.id]: command.player,
        }));
        break;
      case "PLAYER_LEAVE":
        setMessages((messages) => [
          ...messages,
          {
            message: "Player left",
            senderName: "System",
            messageId: crypto.randomUUID(),
          },
        ]);

        setPlayers((players) => {
          const newPlayers = { ...players };
          delete newPlayers[command.playerId];
          return newPlayers;
        });
        break;
      case "FULL_ROOM_STATE":
        setMessages(command.state.messages);
        setPlayers(command.state.playerMap);
        setMyId(command.myId);
        break;
      case "SHOW_PLAYER_ACTIONS":
        setActions(command.actions);
        break;
      default:
        console.warn("Unhandled command type", command);
    }
  }, [lastJsonMessage]);

  return {
    sendTextMessage,
    sendJsonMessage,
    lastJsonMessage,
    readyState,
    messages,
    players,
    myPlayer,
    myId,
    actions,
  };
}
