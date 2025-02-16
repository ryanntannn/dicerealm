import { useEffect, useRef } from "react";
import { useRoomClient } from "./lib/client";
import TextMessageForm from "./TextMessageForm";
import cn from "./lib/cn";
import { ReadyState } from "react-use-websocket";

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

function App() {
  const { messages, sendTextMessage, readyState } = useRoomClient("0000");

  return (
    <div className="h-screen flex flex-col">
      <div className="p-4 flex flex-row items-center justify-between border-b border-gray-300">
        <h1 className="font-medium text-lg flex flex-row items-center">
          DiceRealm <Chip>BETA</Chip>
        </h1>
        <ReadStateChip readyState={readyState} />
      </div>
      <Messages messages={messages} />
      <TextMessageForm onSend={sendTextMessage} />
    </div>
  );
}

export default App;
