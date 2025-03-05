export function Stats({
  stats,
  omitZero,
  omitPositive,
}: {
  stats: Record<string, number>;
  omitZero?: boolean;
  omitPositive?: boolean;
}) {
  return (
    <div className="border-gray-300 rounded py-2">
      <ul>
        {Object.entries(stats).map(([stat, value]) => {
          if (omitZero && value === 0) {
            return null;
          }
          return (
            <li className="text-sm text-gray-600 font-medium" key={stat}>
              {stat}: {!omitPositive && value > 0 && "+"}
              {value}
            </li>
          );
        })}
        {
          // If all stats are zero, show a message
          Object.values(stats).every((v) => v === 0) && (
            <li className="text-sm text-gray-400">No stats</li>
          )
        }
      </ul>
    </div>
  );
}
