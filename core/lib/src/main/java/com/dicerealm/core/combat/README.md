# Combat Package Documentation

## Overview
The `combat` package is responsible for managing and executing combat scenarios within the game. It includes classes and systems for handling combat actions, calculating hits and damage, managing combat logs, and sequencing combat turns
- **`CombatManager`**: Handles the main combat commands
- **`ActionManager`**: Executes specific combat actions and calculates their outcomes
- **`MonsterAI`**: Determines the Actions of the Monster during Monster's turn

---

## Core Manager Classes

### 1. CombatManager
- **Description**: Manages the overall combat sequence, including turn order, combat actions, and determining when combat ends
- **Key Methods**:
    - `CombatManager(List<Entity> participants)`: Initializes the combat manager with a list of participants
    - `initializeTurnQueue(List<Entity> participants)`: Initializes the TurnQueue sorted via Highest to Lowest Initiative Roll
    - `startCombat(Object action)`: Starts the combat loop and processes each entity's turn. Returns a CombatResult Obj for each Iteratation
    - `executeCombatTurn(Entity attacker, Entity target, Object action)`: Executes The Combat Turn Based on If its a Player Entity or Monster Entity
    - `performPlayerAction(Entity player, Entity target, Object action)`: Executes The Combat Turn Based on if Action is Skill Obj or a Weapon Obj
    - `performMonsterAction(Monster monster, Entity target, Object action)`: Executes The Combat Turn Based based on the Monster Action from MonsterAI
    - `isValidAction(Entity attacker)`: Determines if the Action is made is valid, ie action being made by the turn of the current Player
    - `isCombatOver()`: Checks if the combat is over by verifying if all players or all monsters 
    are dead
    - `reduceAllSkillCooldowns()`: Reduces the cooldown of all skills for all participants at the end of a round.
    - `removePlayerFromCombat(UUID player)`: removes specific player from combat if they leave the game.
    - `removeDeadEntities()`: removes entities from turnOrder if they are dead.
    - `startTurn()`: Sets currentEntity as the entity who's current turn it is based on the currentTurnIndex.
    - `startRound()`: Resets currentTurnIndex to 0.
    - `endTurn()`: Ends the current turn of the player, then removes dead entities and checks if a round of combat has passed if it has it calls startRound() and reduces skill cooldowns




### 2. ActionManager
- **Description**: Handles the execution of combat actions, such as attacks and skill usage
- **Key Methods**:
    - `performAttack(Entity attacker, Entity target, Weapon weapon)`: Executes a weapon attack and calculates the hit and damage, returns CombatResult Obj
    - `performSkillAttack(Entity caster, Entity target, Skill skill)`: Executes a skill attack and calculates the hit and damage, returns CombatResult Obj
    - `usePotion(Player player, Potion potion)`: Applies the effects of a potion to the player.
    - `useScroll(Player player, Entity target, Scroll scroll)`: Applies the effects of a scroll to the target.
    - `rigDice(D20 riggedDice)`: Allows for rigging the dice for testing purposes.

### 3. MonsterAI
- **Description**: Handles the logic for monster actions during combat.
- **Key Methods**:
    - `handleMonsterTurn(List<Entity> participants, Entity monster)`: Determines the target and action for a monster's turn and executes the action.
    - `setCombatManager(CombatManager combatManager)`: Sets the combat manager for the AI.


---

## Calculation Systems

### 1. HitCalculator
- **Description**: Determines whether an attack hits or misses based on various factors
- **Key Methods**:
    - `doesAttackHit(Entity attacker, Entity target, ActionType actionType)`: Calculates if an attack hits or misses

### 2. AttackBonusCalculator
- **Description**: Calculates the damage dealt by weapons and skills, including critical hits.
- **Key Methods**:
    - `applyWeaponDamage(Entity attacker, Entity target, Weapon weapon, boolean isCritHit)`: Calculates and applies weapon damage
    - `applySkillDamage(Entity attacker, Entity target, Skill skill, boolean isCritHit)`: Calculates and applies skill damage

### 3. DamageCalculator
- **Description**: Calculates the damage dealt by weapons and skills, including critical hits.
- **Key Methods**:
    - `applyWeaponDamage(Entity attacker, Entity target, Weapon weapon, boolean isCritHit)`: Calculates and applies weapon damage
    - `applySkillDamage(Entity attacker, Entity target, Skill skill, boolean isCritHit)`: Calculates and applies skill damage

### 4. InitiativeCalculator
- **Description**: Calculates the initiative order for combat participants
- **Key Methods**:
    - `rollInitiative(Entity entity)`: Rolls initiative for a given entity

---

## Result Objects

### 1. InitiativeResult
- **Description**: Stores the result of an initiative roll
- **Fields**:
    - `Entity entity`: The entity that rolled for initiative
    - `int initiativeRoll`: The initiative roll value based on D20
    - `int initiativeModifier`: The initiative modifier value based on DEX modifier
    - `int totalInitiative`: The total initiative value

### 2. HitResult
- **Description**: Stores the result of a hit calculation.
- **Fields**:
    - `AttackResult attackResult`: Result of the hit calculation, either `HIT`, `MISS`, `CRIT_HIT`, or `CRIT_MISS`
    - `int attackRoll`: The D20 Die Roll for HitRoll
    - `int attackBonus`: The Ability Modifier Bonus for Hit Roll
    - `String hitLog`: The message generated from the HitCalculator

### 3. DamageResult
- **Description**: Stores the result of a damage calculation
- **Fields**:
    - `int damageRoll`: The damage dealt by the attack
    - `String damageLog`: The message generated from the DamageCalculator

### 4. CombatResult
- **Description**: Stores the result of a combat action, including details about the attacker, target, attack roll, damage roll, and logs.
- **Fields**:
    - `Entity attacker`: The entity that performed the action.
    - `Entity target`: The entity that was targeted.
    - `UUID attackerID`: The unique identifier of the attacker.
    - `UUID targetID`: The unique identifier of the target.
    - `AttackResult attackResult`: The result of the attack, such as `HIT`, `MISS`, `CRIT_HIT`, or `CRIT_MISS`.
    - `int attackRoll`: The value of the attack roll (D20 roll).
    - `int attackBonus`: The bonus applied to the attack roll.
    - `int targetAC`: The target's armor class.
    - `int damageRoll`: The value of the damage roll.
    - `String hitLog`: A log message describing the result of the hit calculation.
    - `String damageLog`: A log message describing the result of the damage calculation.
    - `String potionLog`: A log message describing the result of the potion usage
    - `Dice damageDice`: The dice used for calculating damage.
    - `Weapon weapon`: The weapon used in the attack (if applicable).
    - `Skill skill`: The skill used in the attack (if applicable).
    - `Scroll scroll`: The scroll used in the attack (if applicable).
    - `Potion potion`: The potion used in the turn (if applicable).


- **Key Methods**:
    - **Constructors**:
        - `CombatResult()`: Default constructor that initializes an empty `CombatResult` object.
        - `CombatResult(Entity attacker, Entity target, Weapon weapon)`: Initializes a `CombatResult` object for a weapon-based attack.
        - `CombatResult(Entity attacker, Entity target, Skill skill)`: Initializes a `CombatResult` object for a skill-based attack.
        - `CombatResult(Entity attacker, Entity target, Potion potion)`: Initializes a `CombatResult` object for usage of a Potion.
        - `CombatResult(Entity attacker, Entity target, Scroll scroll)`: Initializes a `CombatResult` object for a scroll-based attack.

    - **Data Population**:
        - `fromHitResult(HitResult hitResult)`: Populates the `CombatResult` object with data from a `HitResult` object, including attack result, attack roll, attack bonus, and hit log.
        - `fromDamageResult(DamageResult damageResult)`: Populates the `CombatResult` object with data from a `DamageResult` object, including damage roll and damage log.

    - **Setters**:
        - `setAttackRoll(int attackRoll)`: Sets the attack roll value for the `CombatResult`.
        - `setAttackBonus(int attackBonus)`: Sets the attack bonus value for the `CombatResult`.

    - **Getters**:
        - `Entity getAttacker()`: Retrieves the attacker entity.
        - `Entity getTarget()`: Retrieves the target entity.
        - `UUID getAttackerID()`: Retrieves the unique identifier (UUID) of the attacker.
        - `UUID getTargetID()`: Retrieves the unique identifier (UUID) of the target.
        - `Dice getDamageDice()`: Retrieves the dice used for calculating damage.
        - `int getDamageRoll()`: Retrieves the roll the dice made.
        - `String getHitLog()`: Retrieves the log message generated during the hit calculation.
        - `String getDamageLog()`: Retrieves the log message generated during the damage calculation.
        - `String getPotionLog()`: Retrieves the log message generated during the Potion usage.
        
        

### 5. CombatLog
- **Description**: Logs combat events and actions for later review
- **Key Methods**:
    - `log(String message)`: Adds a message to the combat log
    - `printAllLogs()`: Prints all logged messages
    - `clearLogs()`: Clears all logged messages
    - `getLogs()`: Retrieves all logged messages as a list











