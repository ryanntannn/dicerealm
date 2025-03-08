import { useEffect, useMemo, useState } from "react";
import useWebSocket from "react-use-websocket";
import { commandSchema } from "./command";
import { Player, PlayerAction, RoomStateState } from "./room-state";

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

  const { sendJsonMessage, lastMessage, lastJsonMessage, readyState } =
    useWebSocket(wsUrl);

  const [state, setState] = useState<RoomStateState | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [players, setPlayers] = useState<Record<string, Player>>({});
  const [myId, setMyId] = useState<string | null>(null);
  const [actions, setActions] = useState<PlayerAction[]>([]);

  const myPlayer = useMemo(() => {
    if (!myId) return null;
    return players[myId];
  }, [myId, players]);

  const sendTextMessage = (message: string) => {
    sendJsonMessage({ type: "MESSAGE", message });
  };

  const equipItemRequest = (itemId: string, bodyPart: string) => {
    sendJsonMessage({
      type: "PLAYER_EQUIP_ITEM_REQUEST",
      itemId,
      bodyPart,
    });
  };

  const chooseAction = (action: string, skillCheck: Record<string, number>) => {
    sendJsonMessage({
      type: "PLAYER_ACTION",
      action,
      skillCheck,
    });
  };

  const startGame = () => {
    sendJsonMessage({
      type: "START_GAME",
    });
  };

  const setPlayerDetails = (player: Player) => {
    sendJsonMessage({
      type: "UPDATE_PLAYER_DETAILS_REQUEST",
      ...player,
    });
  };

  useEffect(() => {
    if (!lastMessage) return;
    console.log(lastMessage);
  }, [lastMessage]);

  useEffect(() => {
    const { data: command, error } = commandSchema.safeParse(lastJsonMessage);
    console.log(lastJsonMessage);
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
        setState(command.state.state);
        setMyId(command.myId);
        break;
      case "SHOW_PLAYER_ACTIONS":
        setActions(command.actions);
        break;
      case "PLAYER_EQUIP_ITEM_RESPONSE":
        setPlayers((players) => {
          const newPlayers = { ...players };
          const player = newPlayers[command.playerId];
          if (!player) {
            console.error("Player not found", command.playerId);
            return newPlayers;
          }

          const prevItem = player.equippedItems[command.bodyPart];
          player.equippedItems[command.bodyPart] = command.item;

          if (prevItem) {
            player.inventory.items.push(prevItem);
          }

          player.inventory.items = player.inventory.items.filter(
            (item) => item.id !== command.item.id
          );

          player.stats = command.updatedPlayerStats;

          return newPlayers;
        });
        setMessages((messages) => [
          ...messages,
          {
            message: `Player equipped item ${command.item.displayName} to ${command.bodyPart}`,
            senderName: "System",
            messageId: crypto.randomUUID(),
          },
        ]);
        break;
      case "CHANGE_LOCATION":
        setMessages((messages) => [
          ...messages,
          {
            message: `Party moved to ${command.location.displayName}`,
            senderName: "System",
            messageId: crypto.randomUUID(),
          },
        ]);
        break;
      case "START_GAME":
        setState("DIALOGUE");
        break;
      case "UPDATE_PLAYER_DETAILS":
        setPlayers((players) => {
          const newPlayers = { ...players };
          const player = newPlayers[command.player.id];
          if (!player) {
            console.error("Player not found", command.player.id, newPlayers);
            return newPlayers;
          }
          newPlayers[command.player.id] = command.player;
          return newPlayers;
        });
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
    equipItemRequest,
    chooseAction,
    state,
    startGame,
    setPlayerDetails,
  };
}
