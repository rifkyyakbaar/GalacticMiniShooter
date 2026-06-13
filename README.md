# Tank-Top : Galactic Mini Shooter

**Author:** Rifky Akbar Utomo Putra  
**Role:** Java Game Developer  

---

## About The Project
TankTop is a fast-paced, 2D top-down survival game built entirely using **Java Swing and AWT**. In this game, players control a green tank to defend against endless waves of red enemy blocks. 

The game features a custom game loop running at a steady 60 FPS, real-time collision detection, and a dynamic leveling system that increases difficulty over time. It culminates in an exciting Boss fight mechanic once the player reaches a specific score threshold.

## Key Features
* **Fluid Movement & Combat:** Real-time keyboard input handling for smooth player movement and shooting.
* **Dynamic Spawning System:** Enemies and power-up items (HP recovery and Bullet Speed boost) spawn algorithmically based on active frame counts.
* **Level Progression:** The game automatically scales in difficulty (increasing enemy speed) as the player's score increases.
* **Boss Fight Mechanic:** A massive boss appears at Level 3, requiring multiple hits to defeat and testing the player's dodging skills.
* **Custom Game Loop:** Utilizes `javax.swing.Timer` to maintain consistent logic updates and graphics rendering.
* **Interactive HUD:** Real-time display of player HP, current Score, and Level directly on the game screen.

## Tech Stack
* **Language:** Java (JDK 8 or higher)
* **GUI Framework:** Java Swing (`JPanel`, `JButton`, `Timer`)
* **Graphics & Events:** Java AWT (`Graphics`, `Rectangle`, `KeyListener`, `ActionListener`)

---

## Installation & Run Guide

Follow these steps to compile and run the game on your local machine:

### Prerequisites
* Java Development Kit (JDK) installed (Version 8 or higher).
* A terminal/command prompt or a standard Java IDE (such as IntelliJ IDEA, Eclipse, or VS Code).
* Git (optional, for cloning).

### Step-by-Step Execution (Via Terminal)

```bash
# 1. Clone the repository and navigate into the project directory
git clone [https://github.com/](https://github.com/)[username-github-kamu]/tanktop-game.git
cd tanktop-game

# 2. Compile all Java files in the directory
javac *.java

# 3. Run the Main application class 
java Main
```
---
### ✨ Thanks for dropping by!
Thank you so much for taking the time to explore the TankTop project. If you have any feedback, suggestions, or just want to chat about Java game dev, feel free to reach out. Have fun playing! 
