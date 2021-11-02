import net.mtrop.doom.WadEntry
import net.mtrop.doom.WadFile
import java.io.File

val wad = WadFile.createWadFile(File("../../../../assets/resource.wad"))

val addFile = fun(file: File) {
    println(file.absolutePath)
    wad.addData(file.name.substring(0, 8), file)
}

//Read graphics
val graphics = File("../resources/graphics")
graphics.listFiles().forEach { f -> addFile(f) }

//Read text lumps
val textlmps = File("../resources/textlmps")
textlmps.listFiles().forEach {f -> addFile(f)}

//Read levels
val levels = File("../resources/levels")
levels.listFiles().forEach { f -> addFile(f) }

//Read music
var midis = File("../resources/midis")
wad.addEntry(WadEntry.create("M_START"))
midis.listFiles().forEach { f -> addFile(f) }
wad.addEntry(WadEntry.create("M_END"))

//Read sounds
var sounds = File("../resources/sounds")
wad.addEntry(WadEntry.create("FX_START"))
sounds.listFiles().forEach { f -> addFile(f) }
wad.addEntry(WadEntry.create("FX_END"))

//Read sprites
var sprites = File("../resources/sprites")
wad.addEntry(WadEntry.create("S_START"))
sprites.listFiles().forEach { f -> addFile(f) }
wad.addEntry(WadEntry.create("S_END"))

//Read tiles
var tiles = File("../resources/tiles")
wad.addEntry(WadEntry.create("G_START"))
tiles.listFiles().forEach { f -> addFile(f) }
wad.addEntry(WadEntry.create("G_END"))

wad.close()