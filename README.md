# FactorioRatioAssistant
A program which calculates exact ratios for factorio assembling machines, such that the number of assembling machines is the lowest possible integer for optimal assembling. For a more thorough look at all the options, look at the [wiki](https://github.com/asampley/FactorioRatioAssistant/wiki)

## Why not a mod?
I've thought about making a mod for factorio, but the one problem with that is it disables achievements. I personally like to get achievements, so an external program is the solution, but perhaps a mod version will come eventually.

## Installation
Download everything in the build folder. This can be done by downloading the whole repository, which is a bit slower, or using this [DownGit link](https://minhaskamal.github.io/DownGit/#/home?url=https://github.com/asampley/FactorioRatioAssistant/tree/master/build). If you do not have Java 8 installed, install it.

## Usage
Run the FactorioRatioAssitant.jar by opening a command line and typing <code> java -jar FactorioRatioAssitant.jar ./0.15.11 </code>. If you are in a different directory than where the jar file is, this command will differ. The <code> ./0.15.11 </code> argument refers to where the program will read the recipes from, and can be changed to any directory which has a similar structure (this can be used by having different directories for different patches or mod configurations). An optional second argument can be supplied as an init file, such as the example on <code> java -jar FactorioRatioAssistant.jar ./0.15.11 ./init.txt </code> (for more details, see [wiki](https://github.com/asampley/FactorioRatioAssistant/wiki/Modification#init-file)).

Once the program is running, all you need to do is type the name of the item that you would like to find the correct ratio for. This will give you a tree structure with the number of machines needed for each component along the way (that there is a recipe for). By default this uses the Assembling Machine 1, but can be changed using some commands.

## Commands
There are a few useful commands to change how the program behaves. For a more extensive list, check out the [wiki](https://github.com/asampley/FactorioRatioAssistant/wiki)

<code>/level \<machine name> \<level> </code><br>
        Changes the level of a machine. Each machine can have different crafting speeds defined by their machine file.<br>
        e.g. <code>/level assembling machine 3</code>
        
<code>/raw \<item name></code><br>
        Change an item to be considered raw, so that if there is a recipe to craft it, it will not be used. This can be useful if you are transporting the item from another area, where you are making it en masse.<br>
        e.g. <code>/raw electronic circuit</code>
        
<code>/unraw \<item name></code><br>
        Change an item to be no longer considered raw, so that if there is a recipe to craft it, it will be used. By default nothing is explicitly considered raw, but this can be useful to undo the <code>/raw</code> command.<br>
        e.g. <code>/unraw electronic circuit</code>

<code>/craft \<item name></code><br>
        An explicit command, which does the same as just typing an item name directly.<br>
        e.g. <code>/craft science pack 1</code>
