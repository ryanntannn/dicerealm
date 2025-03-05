import { Separator } from "@radix-ui/react-separator";
import {
  Heart,
  Sparkles,
  Backpack,
  Brain,
  Shield,
  Swords,
  Zap,
  Orbit,
} from "lucide-react";
import { useRoomClientContext } from "./components/room-client-provider";
import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "./components/ui/sidebar";
import { Avatar, AvatarFallback, AvatarImage } from "./components/ui/avatar";
import { Progress } from "./components/ui/progress";
import { Button } from "./components/ui/button";

const statIcons = {
  STRENGTH: Swords,
  DEXTERITY: Zap,
  CONSTITUTION: Shield,
  INTELLIGENCE: Brain,
  WISDOM: Orbit,
  CHARISMA: Sparkles,
  MAX_HEALTH: Heart,
};

export function PlayerSheet() {
  const { myPlayer, equipItemRequest } = useRoomClientContext();

  if (!myPlayer) {
    return null;
  }

  return (
    <div className="space-y-2">
      {/* Character Header */}
      <div className="p-2 space-y-4">
        <div className="flex items-center space-x-3">
          <Avatar className="h-12 w-12 border">
            <AvatarImage
              src="/placeholder.svg?height=40&width=40"
              alt={myPlayer.displayName}
            />
            <AvatarFallback>
              {myPlayer.displayName.substring(0, 2)}
            </AvatarFallback>
          </Avatar>
          <div>
            <h2 className="text-lg font-bold">{myPlayer.displayName}</h2>
            <div className="flex items-center space-x-2">
              <Heart className="h-4 w-4 text-red-500" />
              <Progress
                value={(myPlayer.health / myPlayer.stats.MAX_HEALTH) * 100}
                className="h-2 w-24 rounded-full"
              />
              <span className="text-xs font-medium">
                {myPlayer.health}/{myPlayer.stats.MAX_HEALTH}
              </span>
            </div>
          </div>
        </div>
      </div>

      <Separator />

      {/* Stats Section */}
      <SidebarGroup>
        <SidebarGroupLabel>Character Stats</SidebarGroupLabel>
        <SidebarGroupContent>
          <div className="grid grid-cols-2 gap-2 p-2 bg-muted/50 rounded-md">
            {Object.entries(myPlayer.stats).map(([stat, value]) => {
              const baseValue = myPlayer.baseStats[stat] || 0;
              const StatIcon =
                statIcons[stat as keyof typeof statIcons] || Sparkles;
              const bonus = value - baseValue;

              return (
                <div
                  key={stat}
                  className="flex items-center justify-between p-1 rounded hover:bg-muted">
                  <div className="flex items-center space-x-2">
                    <StatIcon className="h-4 w-4 text-primary" />
                    <span className="capitalize text-sm">{stat}</span>
                  </div>
                  <div className="flex items-center">
                    <span className="font-medium">{value}</span>
                    {bonus > 0 && (
                      <span className="text-xs text-green-500 ml-1">
                        +{bonus}
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </SidebarGroupContent>
      </SidebarGroup>

      {/* Equipped Items */}
      <SidebarGroup>
        <SidebarGroupLabel asChild>
          <span>Equipped Items</span>
        </SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            {Object.entries(myPlayer.equippedItems).map(([slot, item]) => (
              <SidebarMenuItem key={item.id}>
                <SidebarMenuButton className="justify-between">
                  <div className="flex flex-col items-start">
                    <span className="capitalize text-xs text-muted-foreground">
                      {slot}
                    </span>
                    <span>{item.displayName}</span>
                  </div>
                  {item.stats && Object.keys(item.stats).length > 0 && (
                    <div className="flex items-center space-x-1">
                      {Object.entries(item.stats).map(([stat, bonus]) => {
                        const StatIcon =
                          statIcons[stat as keyof typeof statIcons] || Sparkles;
                        return (
                          <div
                            key={stat}
                            className="flex items-center text-xs text-green-500">
                            <StatIcon className="h-3 w-3 mr-0.5" />
                            <span>+{bonus}</span>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </SidebarMenuButton>
              </SidebarMenuItem>
            ))}
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      {/* Inventory */}
      <SidebarGroup>
        <SidebarGroupLabel asChild>
          <div className="flex items-center space-x-2">
            <span>Inventory</span>
            <div className="flex items-center text-xs text-muted-foreground">
              <Backpack className="h-3 w-3 mr-1" />
              <span>{myPlayer.inventory.items.length} items</span>
            </div>
          </div>
        </SidebarGroupLabel>
        <SidebarGroupContent className="flex flex-col gap-2">
          {myPlayer.inventory.items.map((item) => (
            <div key={item.id} className="flex flex-col items-start p-2 border">
              <span>{item.displayName}</span>
              {item.description && (
                <span className="text-xs text-muted-foreground">
                  {item.description}
                </span>
              )}
              {item.stats && Object.keys(item.stats).length > 0 && (
                <div className="flex items-center space-x-1">
                  {Object.entries(item.stats).map(([stat, bonus]) => {
                    const StatIcon =
                      statIcons[stat as keyof typeof statIcons] || Sparkles;
                    return (
                      <div
                        key={stat}
                        className="flex items-center text-xs text-green-500">
                        <StatIcon className="h-3 w-3 mr-0.5" />
                        <span>+{bonus}</span>
                      </div>
                    );
                  })}
                </div>
              )}
              {item.suitableBodyParts && (
                <div className="flex items-center space-x-1">
                  {item.suitableBodyParts.map((slot) => (
                    <Button
                      key={slot}
                      size="sm"
                      onClick={() => equipItemRequest(item.id, slot)}>
                      Equip to {slot}
                    </Button>
                  ))}
                </div>
              )}
            </div>
          ))}
        </SidebarGroupContent>
      </SidebarGroup>
    </div>
  );
}
