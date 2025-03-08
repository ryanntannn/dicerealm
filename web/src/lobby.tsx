import { ArrowRight, UserPlus } from "lucide-react";
import { Button } from "./components/ui/button";
import { Badge } from "./components/ui/badge";
import { Card, CardHeader, CardContent } from "./components/ui/card";
import { Separator } from "./components/ui/separator";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useRoomClientContext } from "./components/room-client-provider";
import { useMemo } from "react";
import QRCode from "react-qr-code";
import CustomizeCharacterForm from "./customize-character-form";

export default function Lobby({ roomCode }: { roomCode: string }) {
  const { players, myPlayer, myId, startGame, setPlayerDetails } =
    useRoomClientContext();

  const url = window.location.href;

  const localPlayers = useMemo(() => {
    return Object.values(players);
  }, [players]);

  return (
    <main className="flex-1 container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-bold">Game Lobby ({roomCode})</h1>
            <p className="text-muted-foreground">
              Waiting for all players to be ready
            </p>
          </div>
          <div className="flex gap-3">
            <Button className="gap-2" onClick={startGame}>
              Start Game
              <ArrowRight className="h-4 w-4" />
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <Card className="lg:col-span-2">
            <CardHeader className="pb-3">
              <div className="flex justify-between items-center">
                <h2 className="text-xl font-semibold">
                  Players ({localPlayers.length})
                </h2>
                <Button variant="outline" size="sm" className="gap-2">
                  <UserPlus className="h-4 w-4" />
                  Invite
                </Button>
              </div>
              <Separator />
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                {localPlayers.map((player) => (
                  <div
                    key={player.id}
                    className={`flex items-center gap-4 p-3 rounded-lg border ${
                      player.id === myId
                        ? "border-blue-500/50 bg-blue-500/5"
                        : "border-border"
                    }`}>
                    <Avatar className="h-12 w-12 border">
                      <AvatarFallback>
                        {player.displayName.substring(0, 2)}
                      </AvatarFallback>
                    </Avatar>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <span className="font-medium truncate">
                          {player.displayName}
                        </span>
                        {player.id === myId && (
                          <Badge variant="secondary" className="text-xs">
                            You
                          </Badge>
                        )}
                      </div>
                      <div className="text-xs font-medium text-muted-foreground">
                        {player.race}, {player.entityClass}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
          <Card className="p-4 col-span-1 ">
            <QRCode value={url} className="m-auto" />
            <div className="flex flex-col items-center text-muted-foreground">
              <p>Scan this QR code to join the lobby</p>
              <p className="text-sm">or</p>
              <p>Share this link (Click to copy):</p>
              <code
                className="text-sm cursor-pointer text-primary  hover:bg-muted border p-2 rounded"
                onClick={() => navigator.clipboard.writeText(url)}>
                {url}
              </code>
            </div>
          </Card>
          {myPlayer && (
            <Card className="lg:col-span-3 p-4">
              <CustomizeCharacterForm
                player={myPlayer}
                onSubmit={setPlayerDetails}
              />
            </Card>
          )}
        </div>
      </div>
    </main>
  );
}
