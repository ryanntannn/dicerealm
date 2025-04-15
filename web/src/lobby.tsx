import { Badge } from "./components/ui/badge";
import { Card, CardHeader, CardContent } from "./components/ui/card";
import { Separator } from "./components/ui/separator";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useRoomClientContext } from "./components/room-client-provider";
import { useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

export default function Lobby({ roomCode }: { roomCode: string }) {
  const { players, myId } = useRoomClientContext();

  const localPlayers = useMemo(() => {
    return Object.values(players);
  }, [players]);

  return (
    <main className="flex-1 container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-bold">Room Code: ({roomCode})</h1>
            <p className="text-muted-foreground">
              Waiting for all players to be ready
            </p>
          </div>
        </div>

        <ThemeForm />
        <Card className="lg:col-span-2">
          <CardHeader className="pb-3">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-semibold">
                Players ({localPlayers.length}/4)
              </h2>
            </div>
            <Separator />
          </CardHeader>
          <CardContent>
            {localPlayers.length <= 0 && (
              <div className="text-muted-foreground">No Players Yet</div>
            )}
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
      </div>
    </main>
  );
}

function ThemeForm() {
  const { theme, updateTheme } = useRoomClientContext();

  const form = useForm({
    resolver: zodResolver(
      z.object({
        theme: z.string().min(1, "Theme is required"),
      })
    ),
    defaultValues: theme ? { theme } : { theme: "" },
  });

  const { register, handleSubmit } = form;
  const onSubmit = handleSubmit(({ theme }) => {
    updateTheme(theme);
  });

  useEffect(() => {
    if (theme) {
      form.setValue("theme", theme);
      form.reset({ theme });
    }
  }, [theme, form]);

  return (
    <form className="gap-2" onSubmit={onSubmit}>
      <label htmlFor="theme" className="text-sm font-medium">
        Theme:
      </label>
      <textarea
        rows={3}
        id="theme"
        className="border border-gray-300 rounded-md p-2 resize-none w-full"
        placeholder="Enter theme"
        {...register("theme", { required: true })}
      />
      <div className="flex justify-end">
        <button
          type="submit"
          disabled={!form.formState.isDirty}
          className="bg-blue-500 text-white px-4 py-2 rounded-md cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed">
          Update Theme
        </button>
      </div>
      <p className="text-xs text-muted-foreground">
        This will be used to generate the game world.
      </p>
    </form>
  );
}
