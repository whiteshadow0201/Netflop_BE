# Netflop Backend

This is our Backend source for Netflop - Yet another Netflix clone.
Some setups in Ubuntu 22.04 LTS:
1. Install Eclipse Temurin OpenJDK 21: [Follow this video](https://www.youtube.com/watch?v=FQshlECfJoY)
2. Install PostgreSQL 16: [Follow this link](https://dev.to/johndotowl/postgresql-16-installation-on-ubuntu-2204-51ia)
3. Install FFMPEG: Run these commands
- sudo apt update
- sudo apt install ffmpeg
- ffmpeg -version
4. Compile the code with maven-wrapper to build into JAR with the follow command:
-  ./mnvw 
5. Send the file to your server and run the follow command
-  java -jar "YOUR JAR FILE'S NAME" 
