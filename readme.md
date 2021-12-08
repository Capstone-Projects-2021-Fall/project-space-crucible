 # Space Crucible

## Project Overview:
The proposed application, Space Crucible, is a two-dimensional, top-down perspective action-puzzle game with support for both single player and multiplayer. Levels will use a tile-based format though the action will be in real-time and motion will not be locked to the grid. The combat will be in real-time and take place on tile-based levels similar to the game “Hammerwatch.” However, Space Crucible will have a science fiction theme rather than fantasy, will not likely include role-playing elements, and will emphasize run-and-gun combat rather than a hack-and-slash style.

The objective is to explore and find the exit in order to proceed to the next level, while eliminating monsters and dodging traps along the way.  Multiplayer is a key feature- levels can be designed that require more than one player to complete (in fact, the multiplayer-oriented level design will be prioritized). A level editor will be included so users can create their own scenarios and save them in a simple text-based format. Levels can be compiled into level packs using a simple archive format called a “.WAD”, which also contains and organizes any necessary graphics, sounds, and/or music.

Players will receive a launcher upon starting the game. The launcher will allow the user to pick a level pack, a specific level from the pack, choose the difficulty, and either launch a single-player session or join a multiplayer session. A player can either create a lobby or join a lobby by entering the lobby code if launching multiplayer mode. Real-time action will use WASD or arrow keys to move the player, while the player can simultaneously aim using the mouse. Certain map tiles or objects can be interacted with to proceed in the level, or trigger traps. An in-game chat will allow players to communicate. A very simple light system will allow tiles far from light “sources” to darken, obscuring important puzzle components or hiding sneak attacks, including bridges that the players can pass over or under. MIDI files will be used as background music.

## HOW TO BUILD, CONFIGURE AND RUN
Running the application from the ide will give you a better experience of what we have created. That is due to inconsistencies while creating a jar and turning them into a executable file. <br>
If you want to run the game inside an ide, we recommend you use intellij as that was the ide we used to develop this game. <br>
Also, you will require a Java JDK 11 or 15 to compile and execute.
1. Download the source code from the GitHub
2. Open the project folder using the Intellij ide.
3. The Intellij ide should automatically recognize it's a gradle project and load everything. But if not then go to build.gradle file and load the gradle project from there.
4. Loading the dependencies could take a little time depending on the performance of your pc.
5. Once everything is loaded, you will want to create a resource.wad file.
6. To do that go to the WAD folder in the project folder and navigate to Wad/src/main/kotlin
7. Inside the kotlin folder you should see BuildResourceWAD.kts
8. Right-click on the file and click run, it will build a resource.wad file for you automatically.
9. Once built successfully you are ready to run the application.
10. Navigate to lwjgl3/src/main/java/core.gdx.wad.lwjgl3
11. Inside that folder you will find all the launchers for the game
12. To run the game application <br> 
- Windows & Linux <br> 
   - Run Lwjgl3Launcher 
- MacOS 
   - Run LegacyLauncher <br>
- To allow multiple instances of the game you will have to go to the top-right corner of the screen and select edit the configuration and inside the run and edit configuration window click on modify option and allow multiple instances.
13. To access co-op mode/ multiplayer, run the MasterServerLauncher, once booted run ServerLauncher.
- At first the build will not be successful because you haven't configured the program arguments, but you will have the configuration in order to run and add program arguments 
  - To add program arguments click on the configuration menu on the top left in intellij and select edit configuaration.
  - MasterServerLauncher requires the min port, max port, and password, recommended args are "27980 27990 asd" without quotes.
- To run the ServerLauncher you will also need to give it the port number as args value, keep it between the specified min and max.
  - ServerLauncher requires the port number that the server is going to run on. Recommended is "27980" without quotes.
    After executing, don't worry about the warnings as they are part of the kryonet library.
    "WARNING: An illegal reflective access operation has occurred"...
- If you want to run the server on localhost Follow these steps: 
  - navigate to project-space-crucible-0.4.0\core\src\main\java\core\server\SpaceServer.java
  - Inside SpaceServer.java change the IP to "localhost".
  - Navigate to project-space-crucible-0.4.0\core\src\main\java\core\server\SpaceClient.java and also change the IP to "localhost"
- If you want to host your own server then you will need to replace the IP with your own IP and forward the ports in your network.

14. To run the Remote connection, run RCONLauncher.

## HOW TO RUN USING RELEASE EXECUTABLES
The final release's executable files do not have support for macOSes due to an error with OpenGL while creating jar files. But if you wish to run the application on macOS you can do so by running the source code in the ide. <br>
We are providing a .zip file which will contain all the files and folders required to run this multiplayer game. In the crucible-windows.zip, there will be a SpaceCrucible.exe that players using the Windows OS can use. Linux OS users can download the crucible-linux.tar.gz file and run the SpaceCrucible file. If the Space Crucible file is not executable then you will need to make the file executable using chmod in console.

## HOW TO PLAY
To play single-player mode: <br>
1: Click on Start <br>
2: Choose the difficulty you want to play at <br>
3: Click go to start the game <br>
4: Once in-game use WASD to move around, mouse to look around, and left click to shoot. <br>
5: You can use esc menu to restart level or exit to main menu.
 
To play Co-op mode: <br>
1: Click on co-op, if the server is running you will load into the create lobby and join lobby menu otherwise an error will popup <br>
2: To Host your own lobby click on create lobby. If create lobby does not do anything that mean the game server is not running or all of them are occupied. <br>
3: To join someone's lobby click on join lobby and enter the 4-digit code to enter the lobby. <br>
4: The Multiplayer has rejoin feature, join midgame feature, and bot/AI feature.

To load custom level: <br>
1: Click on settings, addons and navigate to a .wad file and click add to load it <br>
2: Once loaded, it can be played in single player mode, and multiplayer mode <br> 
3: In the multiplayer mode the server will automatically download and load the wad file for the users trying to join the lobby. <br>
4: In order to not play the custom level you will have to restart the game application.

## Testing Doc
**[Link](https://www.dropbox.com/s/444101l5moxaws5/Acceptance%20QA%20Testing%20doc.xlsx?dl=0)**

## Demo 3 Release Link
**[Link](https://github.com/Capstone-Projects-2021-Fall/project-space-crucible/releases/tag/0.3.0)**

## Final Demo Release
### Features included in this release

- Single-player mode
- Co-op mode
- Level Editor
- Settings
- Change player name
- Volume settings
- Fullscreen options
- Persistent data for settings menu options
- Five difficulty modes: Very easy, Easy, Medium, Hard, and Nightmare
- Lobby based co-op mode
- A lobby code can be used to join a lobby
- Leaving a co-op mode game will replace the player with a bot
- New players and player who left can rejoin using lobby code
- A Minimap
- Create custom levels using level editor
- Load the custom level using addons menu in settings option
- Server will automatically download custom map for a new player before joining
- UI rework

### Known Bugs in this release

- Run from Source code
  - When too many disconnection happen the server might freeze
    - Although we were not able to implement a permanent fix due to time constraints, we have handled it so the users can still exit to main menu using the ESC menu, and after everyone has exited, the server will reset and be ready to use again.
- Run from Executable file
  - A player leaving co-op mode seems to cause some error making players lose control.
    - This error only occurs in the executable files and is probably caused by error in creating jar files from the source code.

**[Click Here For The Final Demo Release Page](https://github.com/Capstone-Projects-2021-Fall/project-space-crucible/releases/tag/0.4.0)**

## Contributors: 
  Isaac Colon <br>
  Parth Patel <br>
  Kwadwo Gyasi-Danquah <br>
  Meshwa Patel <br>
  Yifan Zhang <br>
  
