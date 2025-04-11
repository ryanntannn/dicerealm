import { useRoomClientContext } from "./components/room-client-provider";

export default function Combat() {
  const { initiativeResults, currentCombatTurnId } = useRoomClientContext();
  return (
    <div className="h-dvh w-dvw flex flex-col items-center justify-center">
      <h1 className="text-3xl font-bold">Combat</h1>
      {initiativeResults.map((result) => (
        <div
          key={result.entityId}
          className={`p-4 m-2 rounded-lg border ${
            result.entityId === currentCombatTurnId
              ? "border-blue-500 bg-blue-100"
              : "border-gray-300"
          }`}>
          <h2 className="text-xl font-semibold">{result.displayName}</h2>
          <p>Initiative: {result.initiative}</p>
        </div>
      ))}
    </div>
  );
}
