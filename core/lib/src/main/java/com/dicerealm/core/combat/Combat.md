### Combat Package Documentation

#### Overview
The `combat` package is responsible for managing and executing combat scenarios within the game. It includes classes and systems for handling combat actions, calculating hits and damage, managing combat logs, and sequencing combat turns.

#### Classes

1. **CombatManager**
    - **Description**: Manages the overall combat sequence, including turn order, combat actions, and determining when combat ends.
    - **Key Methods**:
        - `CombatManager(List<Entity> participants)`: Initializes the combat manager with a list of participants.
        - `startCombat(Object action)`: Starts the combat loop and processes each entity's turn.
        - `isCombatOver()`: Checks if the combat is over by verifying if all players or all monsters are dead.
        - `getTarget(Entity attacker)`: Determines the target for the current attacker.

2. **ActionManager**
    - **Description**: Handles the execution of combat actions, such as attacks and skill usage.
    - **Key Methods**:
        - `performAttack(Entity attacker, Entity target, Weapon weapon)`: Executes a weapon attack and calculates the hit and damage.
        - `performSkillAttack(Entity caster, Entity target, Skill skill)`: Executes a skill attack and calculates the hit and damage.
        - `rigDice(D20 riggedDice)`: Allows for rigging the dice for testing purposes.

3. **DamageCalculator**
    - **Description**: Calculates the damage dealt by weapons and skills, including critical hits.
    - **Key Methods**:
        - `applyWeaponDamage(Entity attacker, Entity target, Weapon weapon, boolean isCritHit)`: Calculates and applies weapon damage.
        - `applySkillDamage(Entity attacker, Entity target, Skill skill, boolean isCritHit)`: Calculates and applies skill damage.
        - `readout()`: Returns the damage log for the last calculated damage.

4. **HitCalculator**
    - **Description**: Determines whether an attack hits or misses based on various factors.
    - **Key Methods**:
        - `doesAttackHit(Entity attacker, Entity target, ActionType actionType)`: Calculates if an attack hits or misses.

5. **CombatLog**
    - **Description**: Logs combat events and actions for later review.
    - **Key Methods**:
        - `log(String message)`: Adds a message to the combat log.
        - `printAllLogs()`: Prints all logged messages.

6. **InitiativeCalculator**
    - **Description**: Calculates the initiative order for combat participants.
    - **Key Methods**:
        - `rollInitiative(Entity entity)`: Rolls initiative for a given entity.

### Result Objects 

1. **InitiativeResult**
    - **Description**: Stores the result of an initiative roll.
    - **Fields**:
        - `Entity entity`: The entity that rolled for initiative.
        - `int initiativeRoll`: The initiative roll value based on D20.
        - `int initiativeModifier`: The initiative modifier value based on DEX modifier. 
        - `int totalInitiative`: The total initiative value.

2. **HitResult**
    - **Description**: Stores the result of a hit calculation.
    - **Fields**:
        - `Entity attacker`: The entity that performed the attack.
        - `Entity target`: The entity that was targeted.
        - `AttackResult attackResult`: Result of the Hit calculation, either HIT, MISS, CRIT_HIT, CRIT_MISS

3. **DamageResult**
    - **Description**: Stores the result of a damage calculation.
    - **Fields**:
        - `Entity attacker`: The entity that performed the attack.
        - `Entity target`: The entity that was targeted.
        - `int damage`: The damage dealt by the attack.
        - `boolean isCritHit`: Indicates if the attack was a critical hit.

4. **AttackResult**
    - **Description**: Stores the result of an attack action.
    - **Fields**:
        - `Entity attacker`: The entity that performed the attack.
        - `Entity target`: The entity that was targeted.
        - `Weapon weapon`: The weapon used in the attack.
        - `boolean isHit`: Indicates if the attack hit the target.
        - `int damage`: The damage dealt by the attack.
        - `boolean isCritHit`: Indicates if the attack was a critical hit.

5. **CombatResult**
    - **Description**: Stores the result of a combat action.
    - **Fields**:
        - `Entity attacker`: The entity that performed the action.
        - `Entity target`: The entity that was targeted.
        - `ActionType actionType`: The type of action performed.
        - `AttackResult attackResult`: Result of the Hit calculation, either HIT, MISS, CRIT_HIT, CRIT_MISS
        - `int damage`: The damage dealt by the action.
        - `boolean isCritHit`: Indicates if the action was a critical hit.

#### Usage Example

```java

```

This example initializes a combat scenario with a player and a monster, then starts the combat with the player using a sword.

#### Notes
- Ensure all entities and items (weapons, skills) are properly initialized before starting combat.
- The `CombatManager` handles the main combat loop and should be the entry point for initiating combat scenarios.
- Use `ActionManager` for executing specific combat actions and calculating their outcomes.