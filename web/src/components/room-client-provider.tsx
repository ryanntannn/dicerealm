import { createContext, useContext } from "react";
import { useRoomClient } from "../lib/client";

const roomClientContext = createContext<ReturnType<
  typeof useRoomClient
> | null>(null);

export default function RoomClientProvider({
  roomId,
  children,
}: {
  roomId: string;
  children: React.ReactNode;
}) {
  const value = useRoomClient(roomId);
  return (
    <roomClientContext.Provider value={value}>
      {children}
    </roomClientContext.Provider>
  );
}

export function useRoomClientContext() {
  const context = useContext(roomClientContext);
  if (!context) {
    throw new Error(
      "useRoomClientContext must be used within a RoomClientProvider"
    );
  }
  return context;
}
