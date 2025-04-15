import { useEffect, useMemo, useRef, useState } from "react";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";
import { DialogueTurn } from "./lib/room-state";
import { useParams } from "react-router";
import { z } from "zod";
import RoomClientProvider, {
  useRoomClientContext,
} from "@/components/room-client-provider";
import { Stats } from "./components/stats";
import Lobby from "./lobby";
import { Card, CardContent, CardHeader } from "./components/ui/card";
import { Brain } from "lucide-react";
import Combat from "./combat";
import { Button } from "./components/ui/button";
import OpenAI from "openai";

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

  const latestTurn = useMemo(() => {
    if (dialogueTurns.length === 0) return null;
    return dialogueTurns[dialogueTurns.length - 1];
  }, [dialogueTurns]);

  return (
    <div
      ref={scrollDivRef}
      className="p-4 flex-grow overflow-y-auto flex flex-col items-center justify-center gap-2">
      {latestTurn && <DialogueTurnRenderer turn={latestTurn} />}
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

const paramsSchema = z.object({
  roomCode: z.string().min(4).max(4),
});

function DialogueTurnRenderer({ turn }: { turn: DialogueTurn }) {
  const [isAudioPlaying, setIsAudioPlaying] = useState(false);
  const onAudioClick = async () => {
    let apiKey = window.localStorage.getItem("openai_api_key");
    if (!apiKey) {
      apiKey = prompt("Please enter your OpenAI API key:");
      if (!apiKey) {
        alert("No API key provided. Please enter a valid OpenAI API key.");
        return;
      }
      window.localStorage.setItem("openai_api_key", apiKey);
    }
    const openai = new OpenAI({
      // API key will never hit the server
      apiKey,
      dangerouslyAllowBrowser: true,
    });

    const response = await openai.audio.speech.create({
      model: "gpt-4o-mini-tts",
      voice: "fable",
      input: turn.dungeonMasterText,
      instructions: "Speak like a dnd dungeon master",
      response_format: "wav",
    });

    // Read the response body as a stream and convert to a blob
    const reader = response.body?.getReader();
    const chunks = [];

    if (!reader) {
      throw new Error("Failed to get reader from response body");
    }

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      chunks.push(value);
    }

    const audioBlob = new Blob(chunks, { type: "audio/wav" });
    const arrayBuffer = await audioBlob.arrayBuffer();

    // Use Web Audio API to decode and play
    const audioContext = new AudioContext();
    const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);

    const source = audioContext.createBufferSource();
    source.buffer = audioBuffer;
    source.connect(audioContext.destination);
    source.start();
  };

  console.log(window.localStorage);
  return (
    <Card>
      <CardHeader>
        <p className="text-xl font-bold text-center">Turn {turn.turnNumber}</p>
        <p className="text-5xl">{turn.dungeonMasterText}</p>
      </CardHeader>
      <CardContent className="grid grid-cols-3 gap-2">
        {Object.entries(turn.actions).map(([playerId, action]) => (
          <Card key={playerId}>
            <CardContent className="text-xl">
              {action.action}
              <Stats stats={action.skillCheck} omitZero omitPositive />
            </CardContent>
          </Card>
        ))}
      </CardContent>
      <Button
        disabled={isAudioPlaying}
        onClick={() => {
          setIsAudioPlaying(true);
          onAudioClick()
            .then(() => {
              setIsAudioPlaying(false);
            })
            .catch((error) => {
              console.error("Error playing audio:", error);
              setIsAudioPlaying(false);
            });
        }}>
        Audio
      </Button>
    </Card>
  );
}

function Chat({ roomCode }: { roomCode: string }) {
  const { messages, readyState } = useRoomClientContext();
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
    </div>
  );
}

type ParamProps = z.infer<typeof paramsSchema>;

function App({ roomCode }: ParamProps) {
  const { state } = useRoomClientContext();

  if (state === "DIALOGUE_TURN" || state === "DIALOGUE_PROCESSING") {
    return <Chat roomCode={roomCode} />;
  }

  if (state === "COMBAT") {
    return <Combat />;
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
