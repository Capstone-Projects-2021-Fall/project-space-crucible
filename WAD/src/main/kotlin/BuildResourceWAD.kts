import net.mtrop.doom.WadEntry
import net.mtrop.doom.WadFile
import java.io.File
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path

val wad = WadFile.createWadFile(File("../../../../assets/resource.wad"))
var stream: DirectoryStream<Path>

val addFile = fun(file: File) {
    println(file.absolutePath)
    wad.addData(file.name.substring(0, 8.coerceAtMost(file.name.indexOf('.'))), file)
}

//Read graphics
val graphics = File("../resources/graphics")
stream = Files.newDirectoryStream(graphics.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()

//Read text lumps
val textlmps = File("../resources/textlmps")
stream = Files.newDirectoryStream(textlmps.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()

//Read levels
val levels = File("../resources/levels")
stream = Files.newDirectoryStream(levels.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()
//Read music
var midis = File("../resources/midis")
wad.addEntry(WadEntry.create("M_START"))
stream = Files.newDirectoryStream(midis.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()
wad.addEntry(WadEntry.create("M_END"))

//Read sounds
var sounds = File("../resources/sounds")
wad.addEntry(WadEntry.create("FX_START"))
stream = Files.newDirectoryStream(sounds.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()
wad.addEntry(WadEntry.create("FX_END"))

//Read sprites
var sprites = File("../resources/sprites")
wad.addEntry(WadEntry.create("S_START"))
stream = Files.newDirectoryStream(sprites.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()
wad.addEntry(WadEntry.create("S_END"))

//Read tiles
var tiles = File("../resources/tiles")
wad.addEntry(WadEntry.create("G_START"))
stream = Files.newDirectoryStream(tiles.toPath())
stream.forEach {p -> addFile(p.toFile())}
stream.close()
wad.addEntry(WadEntry.create("G_END"))

wad.close()