import { useEffect, useRef, useState } from "react";
import { useRoomClient } from "./lib/client";
import TextMessageForm from "./TextMessageForm";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";
import { Player, PlayerAction } from "./lib/room-state";
import { useParams } from "react-router";
import { z } from "zod";

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
  equipItemRequest,
}: {
  inventorySize: number;
  items: {
    id: string;
    displayName: string;
    description: string;
    suitableBodyParts?: string[];
    stats?: Record<string, number>;
  }[];
  equipItemRequest: (itemId: string, bodyPart: string) => void;
}) {
  return (
    <div className="p-4 border-b border-gray-300 flex flex-col gap-4">
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
            {item?.stats && <Stats stats={item.stats} />}
            {item.suitableBodyParts &&
              item.suitableBodyParts.map((bodyPart) => (
                <button
                  onClick={() => equipItemRequest(item.id, bodyPart)}
                  key={bodyPart}
                  className="bg-gray-100 px-4 rounded border border-gray-300">
                  Equip to {bodyPart}
                </button>
              ))}
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

function EquippedItems({
  items,
}: {
  items: {
    bodyPart: string;
    displayName: string;
    description: string;
    stats?: Record<string, number>;
  }[];
}) {
  return (
    <div className="p-4 border-b border-gray-300 rounded">
      <h2 className="font-medium text-lg">Equipped Items 🛡️</h2>
      <ul>
        {items.map((item) => (
          <li
            key={item.displayName}
            className="border border-gray-300 rounded p-2">
            {item.bodyPart} - {item.displayName} - {item.description}
            {item?.stats && <Stats stats={item.stats} />}
          </li>
        ))}
      </ul>
    </div>
  );
}

function Actions({
  actions,
  onAction,
}: {
  actions: PlayerAction[];
  onAction: (action: PlayerAction) => void;
}) {
  return (
    <div className="flex flex-row gap-2 px-4">
      {actions.map((action) => (
        <button
          onClick={() => onAction(action)}
          className="bg-gray-100 px-4 rounded border border-gray-300">
          {action.action}
          <Stats stats={action.skillCheck} omitZero omitPositive />
        </button>
      ))}
    </div>
  );
}

function Stats({
  stats,
  omitZero,
  omitPositive,
}: {
  stats: Record<string, number>;
  omitZero?: boolean;
  omitPositive?: boolean;
}) {
  return (
    <div className="border-gray-300 rounded py-2">
      <ul>
        {Object.entries(stats).map(([stat, value]) => {
          if (omitZero && value === 0) {
            return null;
          }
          return (
            <li className="text-sm text-gray-600 font-medium" key={stat}>
              {stat}: {!omitPositive && value > 0 && "+"}
              {value}
            </li>
          );
        })}
        {
          // If all stats are zero, show a message
          Object.values(stats).every((v) => v === 0) && (
            <li className="text-sm text-gray-400">No stats</li>
          )
        }
      </ul>
    </div>
  );
}

const paramsSchema = z.object({
  roomCode: z.string().min(4).max(4),
});

type ParamProps = z.infer<typeof paramsSchema>;

function App({ roomCode }: ParamProps) {
  const {
    messages,
    sendTextMessage,
    readyState,
    players,
    myId,
    myPlayer,
    actions,
    equipItemRequest,
    chooseAction,
  } = useRoomClient(roomCode);

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <div className="h-screen flex flex-row">
      <div
        className={cn(
          "h-full w-screen md:w-[400px] md:min-w-[400px] md:relative flex-col",
          isSidebarOpen ? "absolute flex bg-white" : "hidden"
        )}>
        <button
          className="absolute right-4 top-4 text-gray-500 font-medium text-xs"
          onClick={() => setIsSidebarOpen(false)}>
          Close
        </button>
        <Players players={players} myId={myId ?? undefined} />

        {myPlayer && (
          <div className="p-4 border-b border-gray-300 ">
            <h1 className="font-medium text-lg ">Character Sheet</h1>
            <Stats stats={myPlayer.stats} />
          </div>
        )}
        {myPlayer && (
          <EquippedItems
            items={Object.entries(myPlayer.equippedItems).map(([k, v]) => ({
              bodyPart: k,
              ...v,
            }))}
          />
        )}
        {myPlayer && (
          <Inventory
            equipItemRequest={equipItemRequest}
            {...myPlayer.inventory}
          />
        )}
      </div>
      <div className="border-r border-gray-200"></div>
      <div className="h-full flex flex-col flex-grow">
        <div className="p-4 flex flex-row items-center justify-between border-b border-gray-300">
          <h1 className="font-medium text-lg flex flex-row items-center">
            DiceRealm <Chip>BETA</Chip>
          </h1>
          <div>
            Room Code: {roomCode}
            <ReadStateChip readyState={readyState} />
          </div>
        </div>
        <Messages messages={messages} />
        <Actions
          actions={actions}
          onAction={(action) => chooseAction(action.action, action.skillCheck)}
        />
        <div className="flex flex-row gap-2 items-center">
          <button
            className="ml-4"
            onClick={() => setIsSidebarOpen((prev) => !prev)}>
            ☰
          </button>
          <TextMessageForm onSend={sendTextMessage} />
        </div>
      </div>
    </div>
  );
}

function UnsafeApp() {
  const params = useParams();

  const { data, error } = paramsSchema.safeParse(params);

  if (error) {
    return <div>Invalid room code</div>;
  }

  return <App {...data} />;
}

export default UnsafeApp;
