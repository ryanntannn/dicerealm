import { useEffect, useRef } from "react";
import { useRoomClient } from "./lib/client";
import TextMessageForm from "./TextMessageForm";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";
import { Player } from "./lib/room-state";

function Message({
  message,
  senderName,
}: {
  message: string;
  senderName: string;
}) {
  return (
    <div className="p-4 border border-gray-300 rounded">
      <p className="font-medium text-sm text-gray-500">{senderName}</p>
      <p>{message}</p>
    </div>
  );
}

function Messages({
  messages,
}: {
  messages: { message: string; senderName: string }[];
}) {
  const scrollDivRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (scrollDivRef.current) {
      scrollDivRef.current.scrollTop = scrollDivRef.current.scrollHeight;
    }
  }, [messages]);

  return (
    <div
      ref={scrollDivRef}
      className="p-4 flex-grow overflow-y-auto flex flex-col gap-2">
      {messages.map((message, index) => (
        <Message key={index} {...message} />
      ))}
    </div>
  );
}

function Chip({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <span
      className={cn(
        "bg-gray-200 font-medium text-gray-600 text-xs rounded-full px-2 py-0.5 ml-2",
        className
      )}>
      {children}
    </span>
  );
}

function ReadStateChip({ readyState }: { readyState: number }) {
  let color = "gray";
  let text = "Disconnected";
  switch (readyState) {
    case ReadyState.CONNECTING:
      color = "yellow";
      text = "Connecting";
      break;
    case ReadyState.OPEN:
      color = "green";
      text = "Connected";
      break;
    case ReadyState.CLOSING:
      color = "yellow";
      text = "Disconnecting";
      break;
    case ReadyState.CLOSED:
      color = "red";
      text = "Disconnected";
      break;
  }
  return <Chip className={`bg-${color}-200`}>{text}</Chip>;
}

function Players({
  players,
  myId,
}: {
  players: Record<string, Player>;
  myId?: string;
}) {
  return (
    <div className="p-4 border-b border-gray-300 rounded">
      <h2 className="font-medium text-lg">Players</h2>
      <ul>
        {Object.values(players).map((player) => (
          <li key={player.id}>
            {player.displayName} {player.id === myId && <Chip>Me</Chip>}
          </li>
        ))}
      </ul>
    </div>
  );
}

function Inventory({
  inventorySize,
  items,
}: {
  inventorySize: number;
  items: { displayName: string; description: string }[];
}) {
  return (
    <div className="p-4 border-b border-gray-300 rounded w-[400px] flex flex-col gap-4">
      <h2 className="font-medium text-lg">Inventory 💼</h2>
      <p>
        {items.length} / {inventorySize} items
      </p>
      <div className="grid grid-cols-2 gap-2">
        {items.map((item) => (
          <div
            key={item.displayName}
            className="border border-gray-300 rounded p-2">
            <p className="font-medium">{item.displayName}</p>
            <p>{item.description}</p>
          </div>
        ))}
        {Array(inventorySize - items.length)
          .fill(null)
          .map((_, index) => (
            <div
              key={index}
              className="border border-gray-300 rounded p-2 bg-gray-100">
              <p className="text-gray-400 text-xs">Empty Slot</p>
            </div>
          ))}
      </div>
    </div>
  );
}

function Actions({
  actions,
  onAction,
}: {
  actions: string[];
  onAction: (action: string) => void;
}) {
  return (
    <div className="flex flex-row gap-2 px-4">
      {actions.map((action) => (
        <button
          onClick={() => onAction(action)}
          key={action}
          className="bg-gray-100 px-4 rounded border border-gray-300">
          {action}
        </button>
      ))}
    </div>
  );
}

function App() {
  const {
    messages,
    sendTextMessage,
    readyState,
    players,
    myId,
    myPlayer,
    actions,
  } = useRoomClient("0000");

  return (
    <div className="h-screen flex flex-row">
      <div className="h-full">
        <Players players={players} myId={myId ?? undefined} />
        {myPlayer && <Inventory {...myPlayer.inventory} />}
      </div>
      <div className="border-r border-gray-200"></div>
      <div className="h-full flex flex-col flex-grow">
        <div className="p-4 flex flex-row items-center justify-between border-b border-gray-300">
          <h1 className="font-medium text-lg flex flex-row items-center">
            DiceRealm <Chip>BETA</Chip>
          </h1>
          <ReadStateChip readyState={readyState} />
        </div>
        <Messages messages={messages} />
        <Actions
          actions={actions}
          onAction={(action) => sendTextMessage(action)}
        />
        <TextMessageForm onSend={sendTextMessage} />
      </div>
    </div>
  );
}

export default App;
