import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router";
import { z } from "zod";
import cn from "./lib/cn";

const roomFormSchema = z.object({
  roomCode: z
    .string()
    .min(4, "must be 4 characters")
    .max(4, "must be 4 characters"),
});

function RoomForm({
  onSubmit,
}: {
  onSubmit: (data: { roomCode: string }) => void;
}) {
  const { register, handleSubmit, formState } = useForm({
    resolver: zodResolver(roomFormSchema),
  });

  const { errors, isDirty } = formState;

  return (
    <div>
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-row gap-2">
        <input
          {...register("roomCode")}
          placeholder="Room code"
          className="border border-gray-300 rounded p-2 flex-grow"
        />
        <button
          type="submit"
          className={cn(
            " text-white rounded p-2 px-4",
            isDirty
              ? "bg-blue-500 hover:bg-blue-600"
              : "bg-gray-400 cursor-not-allowed"
          )}>
          Join
        </button>
      </form>
      {errors.roomCode && (
        <p className="text-red-500 text-sm">{errors.roomCode.message}</p>
      )}
    </div>
  );
}

export default function Home() {
  const navigate = useNavigate();
  return (
    <div className="w-screen h-screen flex items-center justify-center">
      <div className="flex flex-col gap-4">
        <div className="flex flex-row gap-2">
          <img
            src="https://raw.githubusercontent.com/gist/hperantunes/f4c2fd407c2ea74c6c9d/raw/b26f8ddfa5d5d4855af70b7fe4efccf04995f53c/d20.svg"
            alt="Dice"
            className="w-16 h-16"
          />
          <div>
            <h1 className="text-4xl font-bold">dicerealm</h1>
            <p className="font-medium text-gray-600">
              Multiplayer DND, powered by AI
            </p>
          </div>
        </div>
        <RoomForm onSubmit={({ roomCode }) => navigate(`/room/${roomCode}`)} />
        <p className="text-gray-400 text-sm">Made with ❤️ by ateam</p>
      </div>
    </div>
  );
}
