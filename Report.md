# **Background**

**Problem Statement**
Tabletop Role Playing Games such as Dungeons and Dragons(DnD), are often perceived as difficult to jump into for new players. Many are interested in trying these games but are discouraged by the significant time investment required to learn the complex rules, purchase the necessary materials and organise group sessions. As a result, many people who are curious about Dungeons and Dragons never actually get the chance to experience playing them.

**Proposed Solution**
Our app makes it easy for anyone to jump straight into a DnD session without all the usual hassle. Users can instantly create or join an online game room with up to four friends, without needing to schedule in advance or prepare anything beforehand. Once in a room, the app uses OpenAI’s language model to generate dynamic locations and storylines that are influenced by players’ choices, ensuring no two sessions are ever the same. To help lower the learning curve, players are given suggested actions, narrative prompts and automatic dice roll results. This removes the need for players to memorize the complex rules or manually manage gameplay mechanics. Since the AI acts as the Dungeon Master, it makes the experience approachable for beginners and those curious to give this game a try.

**Resources used**
We used Github and Figma as our primary resources. Github was used for collaboration and code management, allowing for multiple team members to work together efficiently and to keep track of changes throughout the development process. It also helped us ensure code integrity across different iterations. Figma was used for designing the UI and UX flow as well as for our initial ideation and scheduling.

**How our solution addresses sustainability goals, diversity and inclusion**
By providing a fully digital experience, the app removes the need for physical game materials such as printed rulebooks, dice sets and miniatures, reducing resource consumption and minimizing environmental impact. The app is designed to be welcoming to both seasoned players and complete newcomers, regardless of their familiarity with DnD. By eliminating barriers such as knowledge of rulesets or access to expensive materials, it creates a more inclusive entry point for players of different ages, backgrounds and experience levels.

<div style="page-break-after: always;"></div>

## **Summary of Prototype**

This prototype is a multiplayer, AI-enhanced, story-driven tactical RPG designed to deliver a highly interactive and replayable experience. It supports online multiplayer for up to four players, where players can join a shared session by entering a unique room code. Players are able to select from a variety of character classes, each offering different equippable gear, usable items, and specialized skills to suit personalized playstyles. An AI-powered Dungeon Master dynamically generates a unique storyline and setting for every game session, ensuring that each playthrough feels fresh and varied. The Dialog Mode, presented through a text-based interface, allows players to select story-driven actions, with some choices involving skill checks that use dice rolls to introduce elements of randomness. Players can also view their current location, adjacent areas, and manage their inventory, equipping different pieces of armour and weapons as needed. The game features a tactical combat system where players can strategically target specific enemies using equipped weapons, spells with cooldown mechanics, and consumable potions. Combat encounters display the dynamic initiative order of both players and enemies, supporting strategic decision-making and enhancing the overall battle experience.

# System Design and Implementation

In the building of Dice Realm we implemented certain System Design choices. We will first give a general overview before zooming into each section respectively.

## **System Architecture (Overview)**

<div align="center">
<img alt="center" src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXdGHztD6oXloXX4HMfwDGu5SYFuMJdH2qZkcUh-4dlKbVTl13nfL5ucLfsK3Zc67yL6-GCLE4kYEg-s8NLYpYUmumyvkZvrKfpoSWywnyEbMt7fMoLh0E0AVYzuR1dELErYk-OF?key=uLeLIxj20T6aLSb7qVEY1yzm">
</div>
Our application was developed in a monorepo environment, where both our android app and server gradle projects import a compiled core library (.jar file). 
The core library contains the core data structures, data transfer objects and game logic that are essential to our game, and contains no external dependencies.
## **Architecture (Android)**
The architecture that we will be using in our android development is recommended by Android. Allowing the separation of purposes, ideal for medium to large scale projects. In this project I didn't use the domain layer as it's explicitly stated to be optional and due to time constraints I decided to not overcomplicate things and omit it. I decided to use the `StateHolder` primarily extending from the `ViewHolder`.
Because of the lack of a domain layer and the usage of the `StateHolder` extending from a `ViewHolder`. This architecture ends up looking similar to that of a MVVM + Repository Architecture. Which will be what we will be using in our Android Application, ensuring scalability. Through the separation of concerns from this Architecture, this ensures that we also adhere to the single responsibility principle.

Fig 1.1.

<div align="center">
<img alt="center" src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXeDwXeASqb-u9xjO4xH998uTKwjNBkv18ZlVrgWEPxSB9cLuWja3MyBlQD6o1_nWMy3q3ZjYkugbSCP9HIPLFonrRINFk578PHBdOrn-K7A3c4DVIKK6MdqfXbTEcRMQpBcf7UG?key=uLeLIxj20T6aLSb7qVEY1yzm">
</div>
Referring to Fig 1.1, the architecture consists of those few components.

##### UI Screen (View)  
The UI Screen is defined using `.layout` files, which are responsible for displaying UI information to the user. These layouts determine how the interface appears visually.
##### Activity (View)  
Each screen is tied to an Activity, which handles the user interaction on the UI Screen. The Activity listens for user actions and updates the views based on logic provided by the State Holders.
**State Holders (ViewModel)**  
State Holders, implemented as ViewModels, interact with both the Activity and the Repository. They are designed to be generic, containing only the attributes and methods that the user needs to interact with, promoting reusability across different parts of the app. ViewModels are lifecycle-aware and scoped to the Activity they belong to. In this project, State Holders manage UI logic by filtering information so that irrelevant data is omitted from the display.
##### Repository  
Repositories act as intermediaries that can manage multiple data sources. They house the main business logic of the app. Typically, there is one Repository class for each type of data, such as `PlayerRepo` or `RoomRepo`. Although repositories could have multiple layers (with sub-repositories), this app does not implement that. Repositories are designed to be immutable, meaning they do not change values directly (e.g., in Firebase), but instead delegate changes to the appropriate Data Sources.
##### Data Sources  
Data Sources are only accessible through the Repository and are responsible for handling specific forms of data, such as files or network responses. In this project, Data Sources are implemented using the Singleton Design Pattern, ensuring that there is only one instance of each source throughout the app's lifecycle. This guarantees a consistent, single source of truth for the app’s data.
##### Other Components Used in the App
- **Models** are used to store data in structured classes, representing entities such as players or rooms.
- **Fragments** are employed to reduce clutter within a single Activity, making screens more modular and promoting reusability across different parts of the application.
- **RecyclerView** is utilized to allow the flexible and reusable design of individual list items, making the display of dynamic data more efficient and adaptable.

<div align="center">
<img alt="center" src="Blank diagram (3).png">
</div>
How the architecture works:
- A UI Screen has an Activity
- An Activity has many StateHolders and vice-versa
- A StateHolder has many Repository and vice-versa
- A Repository has many DataSources and vice-versa

Ensuring that each of the components communicates to its adjacent in a one way street. So an Activity can only communicate to the StateHolder and shouldn’t skip the hierarchy and communicate with the Repository instead, and so on and so forth. This ensures the separation of concerns, adhering to the Single Responsibility Principle (SRP)

### OOP Principles Used for Android side

##### Observer Design Pattern

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdqzMrEbv87WhaXkT2ic02BJOgdhc4oQCeak9hts0uAXmvOFhsZt9_FEMtukkpztvQjm8sD4scGt8bDkyLEyeFmEv0aPUiGxktbLD1V6L_T9FvPoT1pudDQI5OD40TiZ_RKzfLs?key=uLeLIxj20T6aLSb7qVEY1yzm)

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXfLL1TyKDJoxo7c0zR9o7mzOS-UQ42g2Fy1PZ8uEKOQ4Fh6dR5iqWVdTNJzDRzITgtXgbnsxeTKE_dfowZ5BfHK4iKfYyTwDD4zPruqNpSyrgbAZLlnycAU2H-CvAtDL8fyLXk?key=uLeLIxj20T6aLSb7qVEY1yzm)

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXd3SLFxkvGWWLNXmoQpESuxtp17VDoEm-5_0eCwk8hLzV47sCa7-_g8Zn5W8QRVM8P1WSkazlhK1BzoQRwUqnZAzG4ew9z1GJxTi_0DRlMRZ6Ym4R2BVGNQZjl06gCdjeSBjI4K?key=uLeLIxj20T6aLSb7qVEY1yzm)

##### Singleton Design Pattern through the usage of the LiveData and MutableLiveData

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXfwcl6VBd05K4n_XEFlG16_JSrol6mAf-qcESCFQaivIG6mMrSCzwkN_oRnQLI0MwGuZelfiacJuckAgbe5zfotWVclBkL6zzyPKdVSVN4spGfyIuDUqvnsJwugzVR-Qf_c_pB2?key=uLeLIxj20T6aLSb7qVEY1yzm)

##### **Single Responsibility Principle** through the usage of the MVVM + Repository Architecture

<div align="center">
    <img src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXdJwDTqHmeLSGpZ0JNpyG3cJw2Th4-za1SzH3g7UCDNGkMoqfcIBSKNZmj3Jht9A5LsMHtvLZCahYXMeF3mEy3AB1X8cKP20ZRy8KICeqYyb73s5q-cJpm7toYBhKNQprdysvcR?key=uLeLIxj20T6aLSb7qVEY1yzm" alt="Image 1" width="75%">
    <img src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXcb6puuc6g9r8SLertJhr29btdEyznFGW9zh5QDJDyaJ8jO48q351Mbe9FvjZ0A7lTNsK08qh_CGtuKfLyEIywtd5TQdFe12ERJ_wB2WC9DQTHxdk1wyqpwuPSHaqOabiJwvmjg?key=uLeLIxj20T6aLSb7qVEY1yzm" alt="Image 2" width="20%">
</div>
Dialog and Combat are part of the Game but are separated as each of them while similar have key differences in the logic. Separating them like this prevents the code from increasing too much in size. Furthermore, the picture on the left is inside the Combat `StateHolders` file depicting the separation of concerns by only encompassing UI logic as those only filtered relevant information without altering any of its data leaving it to the Combat Repo instead.



## **System Architecture (Server)**

<div align="center">
<img alt="center" src="Blank diagram (2).png">
</div>

The server uses spring web to provide a websocket API that the android app can connect to. 
It contains a single RoomRouter, which is responsible for routing connections and messages to RoomManagers based on the connected client’s room code. 
The logic for handling messages is contained within the Room, which is in the core library. A RoomManager is responsible for interfacing with a Room, including injecting required dependencies.

## **System Architecture (Core)**

The core library contains the Room class, which is responsible for holding all information about a game room, such as how messages are handled, and the state of the room.

### **Commands**

Commands are the primary way that the client and server communicate. Commands are JSON objects that have a `type` field that specifies the type of command, and additional fields that provide the necessary data for the command. The server processes commands by deserializing them and executing the appropriate command handler.
Example Command, `CHANGE_LOCATION`, sent from the server to a client, that changes the party's location to a tavern:

```
{
  "type": "CHANGE_LOCATION",
  "location": {
    "id": "<SOME-UUID>",
    "displayName": "Tavern",
    "description": "A cozy tavern with a warm fire and friendly patrons."
  }
}
```

### **Command Handler**

The command handler is responsible for processing commands received from the client. The command handler deserializes the command and executes the appropriate command handler based on the command type. The command handler is responsible for updating the game state and sending responses back to the client.
Each command handler should handle the command in the following way:

1. Validate the command. E.g. Can the player equip the item? Is the player in the correct location?
2. Update the game state via the `RoomContext`.
3. Broadcast the updated game state to all players in the room.
   Command handlers are registered with the `CommandRouter` in the `Room` class, but can also be used independently during testing.

**Example snippet:**

```java
public class StartGameHandler extends CommandHandler<StartGameCommand> {
 PlayerMessageHandler playerMessageHandler = new PlayerMessageHandler();
 public StartGameHandler() {
   super("START_GAME");
 }
 
 @Override
 public void handle(UUID playerId, StartGameCommand command, RoomContext context) {
   // STEP 1: Validate the command
   if (context.getRoomState().getState() != RoomState.State.LOBBY) {
     throw new IllegalStateException("Game has already started");
   }
   // STEP 2: Update the game state
   context.getRoomState().setState(RoomState.State.DIALOGUE);
   // STEP 3: Broadcast the updated game state
   context.getBroadcastStrategy().sendToAllPlayers(command);
   playerMessageHandler.handleNormalMessage(playerId, "Let's start the adventure!", context);
 }
}

```

### **Room Context**

The `RoomContext` is a container that holds the game state and provides access to the various components of the game. The `RoomContext` is passed to the command handler, allowing the handler to access the game state and update it as needed.
The `RoomContext` provides access to the following components:

- `RoomState`: The current state of the room.
- `BroadcasterStrategy`: The strategy used to broadcast messages to players.
- `DungeonMaster`: The AI-powered DM that can perform state transitions.
- `CombatManager`: The manager that handles combat between players and monsters.

### **OOP Principles Used for Server Side**

##### Interface Segregation
DiceRealm demonstrates the Interface Segregation Principle by splitting item behavior into focused, specialized class hierarchies. Instead of forcing all items to implement a broad set of unrelated methods, UsableItem defines the useOn() behavior specifically for usable items like scrolls and potions, while EquippableItem defines equipment-specific attributes like suitableBodyParts and stats. Even within usable items, Scrolls and Potions manage their unique dice effects separately through damageDice and effectDice. This design ensures that each class only implements methods relevant to its purpose, preventing unnecessary dependencies and keeping item types lightweight and maintainable

##### Open-Closed Principle 
In DiceRealm, the design of the Item system strongly follows the Open/Closed Principle. The core base classes like Item, UsableItem, and EquippableItem were established early in the project and have remained unchanged since. As new functionality was needed, such as supporting scrolls, potions, and weapons with specific behaviors like rollDamage() to determine damage roll, and useOn() to determine whether the item was usable on the specified target, we extended the system through inheritance rather than modifying existing classes. This allowed the codebase to stay stable and reliable while still accommodating new types of items and behaviors, showcasing a clear commitment to keeping classes open for extension but closed for modification.

Class extensions:
<div align="center">
<img alt="center" src="https://lh7-rt.googleusercontent.com/docsz/AD_4nXeH6mP4cgF7hZmmEW1UEPFTDQOxcAVxubR2fGUW4jS-pWCxWebexxCTAutCDXowktKmdJ7XyIOuYJ_G2tyUqMZcOZIgOubKbm0B5L3ZDr_ZNmFDOwZ4SCPQGFyipzzUj4P68YS6?key=uLeLIxj20T6aLSb7qVEY1yzm" width="400">
</div>
##### Single Responsibility Principle
Each `Command` subclass is responsible for storing information about a single action/state update, and each `CommandHandler` is responsible for handling one `Command` subclass. For example, a `PlayerEquipItemCommand` is responsible for storing information about a player’s request to equip an item, and the `PlayerEquipItemHandler` is responsible for processing `PlayerEquipItemCommand`.
##### Liskov Substitution
We have implemented a Graph class that contains a `shortestPath` method, that uses BFS to find the shortest path, as we assume Graphs to be bidirectional, without weights in this implementation. 
A DistanceGraph subclass was implemented that extends Graph, created to represent locations and the roads between these locations. We override the `shortestPath` method with an implementation of Dikstra’s algorithm to account for the edge weights (distances).
##### Dependency Inversion
We designed a core library independent of third-party libraries, which are instead dependent on interfaces called Strategies. These Strategies are injected into a Room via the RoomBuilder. This ensures that if a third-party software becomes obsolete, we can seamlessly replace it without impacting the core functionality. We may inject Mock Strategies for unit and integration tests, as well as debugging.

##### **Big Picture Mode**

The Big Screen Mode is a Single Page Application (SPA) that allows users to play Dicerealm on a big screen, like Kahoot or Jackbox. It was written in React and Typescript, and was originally used as a test client for the server before we started work on the android app. It was later repurposed for the Big Screen Mode. This is an optional feature, and does not affect the core functionality of the app.

<div style="page-break-after: always;"></div>


## **2D Aspect**

We have implemented Dijkstra's algorithm in our `DistanceGraph` class to determine the shortest path between locations in our game.

<div align="center">
<img alt="center" src="Pasted image 20250427053357.png">
</div>
<div align="center">
<img alt="center" src="Pasted image 20250427053430.png">
</div>
<div align="center">
<img alt="center" src="Pasted image 20250427053451.png">
</div>

## **Project Management**

<div align="center">
  <table>
    <tr>
      <th>Responsibility</th>
      <th>Name</th>
    </tr>
    <tr>
      <td>Backend server setup & implementation for Online Multiplayer</td>
      <td>Ryan, Kendrix</td>
    </tr>
        <tr>
      <td>AI Prompting & Implementation</td>
      <td>Gerald, Donovan</td>
    </tr>
        <tr>
      <td>Android Frontend & Backend Implementation</td>
      <td>Jing Jie, Shana, Donovan</td>
    </tr>
        <tr>
      <td>Android User Interface</td>
      <td>Shana, Donovan</td>
    </tr>
        <tr>
      <td>Combat System</td>
      <td>Darren</td>

  </table>
</div>

We used FigJam to allocate tasks based on the P0/P1/P2 priority scale and the MVP concept, and to plan the overall weekly schedule with submission deadlines.
Figma was used to create wireframes for key UI components to guide development.
Jira was set up with a kanban board to manage the timeline for every task, tracking task progression and ownership among team members.

## **Financial Report**

<div align="center">
  <table>
    <tr>
      <th>Component</th>
      <th>Expenditure ($)</th>
    </tr>
    <tr>
      <td>50.002 Project</td>
      <td>~20</td>
    </tr>
        <tr>
      <td>LLM API Usage</td>
      <td>~40</td>
    </tr>
        <tr>
      <td>Container Hosting (Server)</td>
      <td>~4</td>
      </tr>

  </table>
</div>
## **Conclusion and Future Work**

##### Custom character creation

Registration, sign in feature (save game state)
Dice roll
Ability to choose the theme of the Campaign
Implement a win/loss screen with item drops after a battle

- Players can choose 1 of the 2 items shown on the screen
  Add a campaign end screen
  UI Refinement for all screens for a better user experience

##### Conclusion
In this project, we successfully developed Dice Realm, an interactive online multiplier LLM application that allows users to play with friends across the world while easing new players into the game. Through the process, we applied various key concepts that was taught in the course such as the Single Responsibility Principle, Observer and Singleton Design Pattern

Additionally, we adopted industry standard practices by using Spring boot for backend development and implementing Dependency Injection to manage component interactions efficiently.These practices that we use not only enhance the scalability and maintainability of our application but also align with real-world software engineering workflow.

The app Dice Realm demonstrates a strong potential for future enhancement, such as customizable character selection, expansion into web platforms or the addition of a sign in feature to allow players to save their character and retain game states from previous sessions. Overall, this project allows us to strengthen both our technical foundations and collaboration skills, preparing us for future professional software development projects.

[https://github.com/ryanntannn/dicerealm](https://github.com/ryanntannn/dicerealm)

Reference:
[https://developer.android.com/topic/architecture?gclid=CjwKCAjw6raYBhB7EiwABge5Klm_5PN8nJF0Jrb_ymrPP0JAEsbmemmGv_nsn0nBQKQtQMCBuvjehRoC7qcQAvD_BwE&gclsrc=aw.ds#recommended-app-arch](https://developer.android.com/topic/architecture?gclid=CjwKCAjw6raYBhB7EiwABge5Klm_5PN8nJF0Jrb_ymrPP0JAEsbmemmGv_nsn0nBQKQtQMCBuvjehRoC7qcQAvD_BwE&gclsrc=aw.ds#recommended-app-arch)

[^1]:
