import { useNavigate } from "react-router";

import { motion } from "framer-motion";
import { Dice5, Swords, Shield, Scroll, Users } from "lucide-react";
import { Button } from "./components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "./components/ui/card";

function getRandomRoomCode() {
  const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  let result = "";
  for (let i = 0; i < 4; i++) {
    result += characters.charAt(Math.floor(Math.random() * characters.length));
  }
  return result;
}

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-b from-background to-muted flex flex-col">
      {/* Header */}
      <header className="container mx-auto py-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Dice5 className="h-8 w-8 text-primary" />
            <span className="text-2xl font-bold">dicerealm.ai</span>
          </div>
          <Button variant="outline">Github</Button>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 container mx-auto flex flex-col items-center justify-center px-4 py-12">
        <div className="w-full max-w-5xl grid md:grid-cols-2 gap-12 items-center">
          {/* Left Column - Hero Text */}
          <div className="space-y-6">
            <motion.h1
              className="text-4xl md:text-6xl font-extrabold tracking-tight"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}>
              Your Adventure <br />
              <span className="text-primary">Awaits</span>
            </motion.h1>

            <motion.p
              className="text-xl text-muted-foreground"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.2 }}>
              Join your friends in an epic D&D campaign. Enter your room code to
              continue your quest.
            </motion.p>

            <motion.div
              className="flex flex-wrap gap-6 pt-4"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.4 }}>
              <div className="flex items-center gap-2">
                <Swords className="h-5 w-5 text-primary" />
                <span>Epic Battles</span>
              </div>
              <div className="flex items-center gap-2">
                <Shield className="h-5 w-5 text-primary" />
                <span>Character Tracking</span>
              </div>
              <div className="flex items-center gap-2">
                <Scroll className="h-5 w-5 text-primary" />
                <span>Interactive Maps</span>
              </div>
              <div className="flex items-center gap-2">
                <Users className="h-5 w-5 text-primary" />
                <span>Multiplayer</span>
              </div>
            </motion.div>
          </div>

          {/* Right Column - Join Form */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}>
            <Card className="w-full max-w-md mx-auto">
              <CardHeader>
                <CardTitle>Create a New Room</CardTitle>
                <CardDescription>
                  Connect your mobile devices to join the game
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button
                  onClick={() => navigate(`/room/${getRandomRoomCode()}`)}>
                  Start a New Game
                </Button>
              </CardContent>
              <CardFooter className="flex justify-between pt-4"></CardFooter>
            </Card>
          </motion.div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t py-6">
        <div className="container mx-auto flex flex-col md:flex-row justify-between items-center">
          <p className="text-sm text-muted-foreground">Made with ❤️ by ateam</p>
          <div className="flex gap-4 mt-4 md:mt-0">
            <Button variant="ghost" size="sm">
              About
            </Button>
            <Button variant="ghost" size="sm">
              Help
            </Button>
            <Button variant="ghost" size="sm">
              Privacy
            </Button>
          </div>
        </div>
      </footer>
    </div>
  );
}
