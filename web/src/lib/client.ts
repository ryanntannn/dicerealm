import { useEffect, useMemo, useState } from "react";
import useWebSocket from "react-use-websocket";
import { commandSchema } from "./command";

const SERVER_URL = import.meta.env.VITE_SERVER_URL as string;

type Message = {
  message: string;
  senderName: string;
  messageId: string;
};

export function useRoomClient(
  roomId: string,
  options: { serverUrl: string } = { serverUrl: SERVER_URL }
) {
  const wsUrl = useMemo(() => {
    const url = new URL(options.serverUrl);
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

  const sendTextMessage = (message: string) => {
    sendJsonMessage({ type: "MESSAGE", message });
  };

  useEffect(() => {
    const { data: command, error } = commandSchema.safeParse(lastJsonMessage);
    if (error) {
      console.error(lastJsonMessage, error);
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
            message: "Player joined",
            senderName: "System",
            messageId: crypto.randomUUID(),
          },
        ]);
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
  };
}
