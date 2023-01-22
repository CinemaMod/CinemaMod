<p align="center">
  <img src="https://user-images.githubusercontent.com/30220598/173697364-2bc49792-ad64-488a-99bf-9c28d7e91b6e.png" width="200px">
</p>

# CinemaMod-Fabric
Watch online videos with others in Minecraft. Developed with Fabric, currently supports Minecraft 1.18.2.

NOT RELEASED, IN DEVELOPMENT

## Discussion
https://discord.gg/rNrh5kW8Ty

## Development Environment Setup
CinemaMod requires a build of Java Chromium Embedded Framework to work - it uses the embedded browser in-game to render the videos. The java-cef builds are large binaries and are not included in this repo. I host builds of java-cef specifically for use with this mod on [my cloud storage](https://ewr1.vultrobjects.com/cinemamod-libraries/). When a release is made, .jars are created for each platform that contain the java-cef binaries. I.e. there is a .jar for Windows, Mac, Linux; the .jars will only work on their respective platform.

During development, it is necessary to have a build of java-cef for the mod to work properly. There is a gradle task to download the java-cef binaries into your gradle `build` directory.

First, clone this repo:
```
$ git clone https://github.com/CinemaMod/CinemaMod-Fabric
```

Then, clone the java-cef submodule (brings in the java-cef code)
```
$ git submodule update --init --recursive
```

Finally, download the java-cef builds (this may take a while)
```
$ ./gradlew downloadJcef
```

You should be able to edit the code and run the client from here, try
```
$ ./gradlew runClient
```

## Screenshots
<p align="center">
  <img src="https://user-images.githubusercontent.com/30220598/173701573-0106d561-d70a-483c-bc35-b2bb0236459d.jpg" width="400px">
  <img src="https://user-images.githubusercontent.com/30220598/173701578-a168513c-c4d8-4a18-b83b-86f8592eb28b.jpg" width="400px">
  <img src="https://user-images.githubusercontent.com/30220598/173701585-72fa00e4-5905-4d2d-9165-1f9bd79c0778.jpg" width="400px">
  <img src="https://user-images.githubusercontent.com/30220598/173701589-b093e08b-7568-465e-87c3-14574d645c1f.jpg" width="400px">
  <img src="https://user-images.githubusercontent.com/30220598/173701595-76db736e-4aab-4e0f-8272-e50de0a4d871.jpg" width="400px">
  <img src="https://user-images.githubusercontent.com/30220598/173701601-12ca7059-6c65-4fbc-97ed-1b60a2edee32.jpg" width="400px">
</p>
