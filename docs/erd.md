# Entity Relationship Diagram

The entity relationship diagram is a visual representation of the relationships between the entities in the game. The diagram is generated using the [Mermaid](https://mermaid-js.github.io/mermaid/#/) library.

The entity relationship diagram is different from the class diagram in that it focuses on the relationships between the entities rather than the attributes and methods of the entities. You may omit non-essential entities such as builders and helpers from the diagram to keep it simple and easy to understand.

Please update the diagram as the relationships between the entities change.

```mermaid
erDiagram
    RoomRouter ||--|{ RoomManager : "has multiple"
    RoomManager ||--|| Room : "manages"
    Room ||--|| RoomState: "manages"
    RoomState ||--|| BroadcasterStrategy: "uses"
    RoomState ||--|{ Player: "contains"
    Player ||--|| Entity: "inherits"
    Entity ||--|| ItemInventory: "contains"
    Entity ||--|| StatsMap: "contains"
    ItemInventory ||--|{ Item: "contains"
    Entity ||--|| SkillsInventory: "contains"
    SkillsInventory ||--|{ Skill: "contains"
    RoomState ||--|{ Message: "contains"
    RoomState ||--|| LocationGraph: "contains"
    LocationGraph ||--|{ Location: "contains"
    Location ||--|{ Entity: "contains"
    LocationGraph ||--|{ Path: "contains"
    Path ||--|{ Location: "joins"
    Room ||--|| DungeonMaster: "uses"
    DungeonMaster ||--|| LLMStrategy: "uses"
    Room ||--|| CombatManager: "uses"
    Room ||--|| CommandDeserializer: "uses"
    CommandDeserializer ||--|| JsonSerializationStrategy: "uses"
    CommandDeserializer ||--|{ Command: "deserializes"
```
