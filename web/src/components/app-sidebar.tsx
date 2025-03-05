import { Sidebar, SidebarContent, SidebarGroup } from "@/components/ui/sidebar";
import { PlayerSheet } from "@/character-sheet";

export function AppSidebar() {
  return (
    <Sidebar>
      <SidebarContent>
        <PlayerSheet />
        <SidebarGroup />
        <SidebarGroup />
      </SidebarContent>
    </Sidebar>
  );
}
