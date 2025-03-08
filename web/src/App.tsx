import { useEffect, useRef } from "react";
import TextMessageForm from "./TextMessageForm";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";
import { PlayerAction } from "./lib/room-state";
import { useParams } from "react-router";
import { z } from "zod";
import Layout from "./layout";
import RoomClientProvider, {
  useRoomClientContext,
} from "@/components/room-client-provider";
import { Stats } from "./components/stats";
import { SidebarTrigger } from "./components/ui/sidebar";
import Lobby from "./lobby";

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

const paramsSchema = z.object({
  roomCode: z.string().min(4).max(4),
});

function Chat({ roomCode }: { roomCode: string }) {
  const { messages, sendTextMessage, readyState, actions, chooseAction } =
    useRoomClientContext();
  return (
    <div className="h-dvh max-h-dvh w-full flex flex-col top-0">
      <div className="p-4 flex flex-row items-center justify-between border-b border-gray-300">
        <div className="flex flex-row items-center gap-2">
          <h1 className="font-medium text-lg flex flex-row items-center">
            DiceRealm
          </h1>
          <Chip>BETA</Chip>
        </div>
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
      <div className="flex flex-row items-center gap-4 p-4">
        <SidebarTrigger />
        <TextMessageForm onSend={sendTextMessage} />
      </div>
    </div>
  );
}

type ParamProps = z.infer<typeof paramsSchema>;

function App({ roomCode }: ParamProps) {
  const { state } = useRoomClientContext();

  if (state === "DIALOGUE") {
    return (
      <Layout>
        <Chat roomCode={roomCode} />
      </Layout>
    );
  }

  return <Lobby roomCode={roomCode} />;
}

function UnsafeApp() {
  const params = useParams();

  const { data, error } = paramsSchema.safeParse(params);

  if (error) {
    return <div>Invalid room code</div>;
  }

  return (
    <RoomClientProvider roomId={data?.roomCode}>
      <App {...data} />
    </RoomClientProvider>
  );
}

export default UnsafeApp;
