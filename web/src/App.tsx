import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import TextMessageForm from "./TextMessageForm";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";
import { DialogueTurn, PlayerAction } from "./lib/room-state";
import { useParams } from "react-router";
import { z } from "zod";
import Layout from "./layout";
import RoomClientProvider, {
  useRoomClientContext,
} from "@/components/room-client-provider";
import { Stats } from "./components/stats";
import { SidebarTrigger } from "./components/ui/sidebar";
import Lobby from "./lobby";
import { Progress } from "./components/ui/progress";
import { Card, CardContent, CardHeader } from "./components/ui/card";
import { Brain } from "lucide-react";

function Messages({
  messages,
}: {
  messages: { message: string; senderName: string }[];
}) {
  const { dialogueTurns, state } = useRoomClientContext();
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
      {dialogueTurns.map((turn, index) => (
        <DialogueTurnRenderer key={index} turn={turn} />
      ))}
      {state === "DIALOGUE_PROCESSING" && (
        <div className="flex flex-row items-center justify-center p-8 border-4 border-dashed rounded-xl gap-1 animate-pulse">
          <Brain />
          Dungeon Master is Thinking...
        </div>
      )}
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
    <div className="grid grid-cols-3 gap-2 px-4">
      {actions.map((action) => (
        <Card
          onClick={() => onAction(action)}
          className="cursor-pointer hover:-translate-y-1 transform transition-transform">
          <CardHeader>{action.action}</CardHeader>
          <CardContent>
            <Stats stats={action.skillCheck} omitZero omitPositive />
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

const paramsSchema = z.object({
  roomCode: z.string().min(4).max(4),
});

function DialogueTurnRenderer({ turn }: { turn: DialogueTurn }) {
  const [progress, setProgress] = useState(0);

  const isEnded = useMemo(() => {
    return Date.now() > turn.endTime;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [turn, progress]);

  const updateProgress = useCallback(() => {
    const now = Date.now();
    const total = turn.endTime - turn.startTime;
    const elapsed = now - turn.startTime;
    const progress = (elapsed / total) * 100;
    console.log(progress);
    setProgress(progress);
  }, [turn]);

  useEffect(() => {
    if (isEnded) {
      setProgress(100);
      return;
    }
    const interval = setInterval(updateProgress, 1000);
    return () => clearInterval(interval);
  }, [updateProgress, isEnded]);

  return (
    <Card className={cn(isEnded && "bg-gray-100 text-gray-600")}>
      <CardHeader>
        <p>Turn {turn.turnNumber}</p>
        <p>Dungeon Master: {turn.dungeonMasterText}</p>
        {isEnded && <p>Turn Ended</p>}
        {!isEnded && <Progress value={progress} />}
      </CardHeader>
      <CardContent className="grid grid-cols-3 gap-2">
        {Object.entries(turn.actions).map(([playerId, action]) => (
          <Card
            key={playerId}
            className={cn(isEnded && "bg-gray-200 text-gray-600")}>
            <CardHeader>{playerId} is going to</CardHeader>
            <CardContent>
              {action.action}
              <Stats stats={action.skillCheck} omitZero omitPositive />
            </CardContent>
          </Card>
        ))}
      </CardContent>
    </Card>
  );
}

function Chat({ roomCode }: { roomCode: string }) {
  const {
    messages,
    sendTextMessage,
    readyState,
    actions,
    chooseAction,
    state,
  } = useRoomClientContext();
  return (
    <div className="h-dvh max-h-dvh w-full flex flex-col top-0">
      <div className="p-4 flex flex-row items-center justify-between border-b border-gray-300">
        <div className="flex flex-row items-center gap-2">
          <SidebarTrigger />
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
      <div className="gap-2 p-4"></div>
      <Actions
        actions={actions}
        onAction={(action) => chooseAction(action.action, action.skillCheck)}
      />
      <div className="flex flex-row items-center gap-4 p-4">
        {state === "DIALOGUE_TURN" && (
          <TextMessageForm onSend={sendTextMessage} />
        )}
      </div>
    </div>
  );
}

type ParamProps = z.infer<typeof paramsSchema>;

function App({ roomCode }: ParamProps) {
  const { state } = useRoomClientContext();

  if (state === "DIALOGUE_TURN" || state === "DIALOGUE_PROCESSING") {
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
