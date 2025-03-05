import { SidebarProvider } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <SidebarProvider
      style={
        {
          "--sidebar-width": "25rem",
          "--sidebar-width-mobile": "100vw",
        } as React.CSSProperties
      }>
      <AppSidebar />
      <main className="h-dvh w-dvw">{children}</main>
    </SidebarProvider>
  );
}
