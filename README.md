<p align="center">
  <img width="930" height="63.6" src="https://github.com/user-attachments/assets/208313e3-16a4-48da-b871-38ca00e9a868"> </p>

> [!WARNING]
> This version REQUIRES a JDK 8 and GRADLE 2.10, also this particular modified version DO NOT have HTML build attached due to missing repositories. This is modified only to run Desktop.

**Mindustry v1 originated by Anuke from GDL - Metal Monstrosity Jam**
![0226 - frame at 0m0s](https://github.com/user-attachments/assets/7ff8567f-4ec0-42d1-88be-f3b63abb6fe3)
Moment is an early version based on this [commit](https://github.com/Anuken/Mindustry/tree/f5e30f53e00dde2c72e446f9193d28150e6627b1) released in April 30th, 2017 just 2 days after release in Itch.io, it's the only working version without extensive bugfixes. Created by [Anuken](https://github.com/Anuken) and was for [GDL - Metal Monstrosity Jam](https://itch.io/jam/gdl---metal-monstrosity-jam) ranked 1st place out of the rest.

## Building

> [!NOTE]
> Before building, running, or packing Moment.jar, please do note, install GRADLE 2.10 or run with GRADLEW -V, if it fails try using (VIA LOCALLY) the gradle-2.10-rc-2-all.zip to the gradle installation folder or where it failed to unzip the file.

### Windows

_Running:_ `gradlew desktop:run`  
_Building:_ `gradlew desktop:dist`  

### Linux/Mac OS

> [!NOTE]
> FOR LINUX users, run **chmod +x gradlew** as if you try to run it normally it would result **permission denied** error, also LINUX cannot run at all IF it was builded, but can able to able to use **desktop:run**.

_Running:_ `./gradlew desktop:run`  
_Building:_ `./gradlew desktop:dist` 
