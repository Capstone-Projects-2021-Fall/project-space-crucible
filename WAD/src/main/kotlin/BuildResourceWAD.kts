import net.mtrop.doom.WadEntry
import net.mtrop.doom.WadFile
import java.io.File
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

val wad = WadFile.createWadFile(File("../../../../assets/resource.wad"))
var dir: File
var list = LinkedList<File>()
var stream: DirectoryStream<Path>

val addFile = fun(file: File) {
    println(file.absolutePath)
    wad.addData(file.name.substring(0, 8.coerceAtMost(file.name.indexOf('.'))), file)
}

//Read graphics
dir = File("../resources/graphics")
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()

//Read text lumps
dir = File("../resources/textlmps")
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()

//Read levels
dir = File("../resources/levels")
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()

//Read music
dir = File("../resources/midis")
wad.addEntry(WadEntry.create("M_START"))
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()
wad.addEntry(WadEntry.create("M_END"))

//Read sounds
dir = File("../resources/sounds")
wad.addEntry(WadEntry.create("FX_START"))
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()
wad.addEntry(WadEntry.create("FX_END"))

//Read sprites
dir = File("../resources/sprites")
wad.addEntry(WadEntry.create("S_START"))
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()
wad.addEntry(WadEntry.create("S_END"))

//Read tiles
dir = File("../resources/tiles")
wad.addEntry(WadEntry.create("G_START"))
stream = Files.newDirectoryStream(dir.toPath())
stream.forEach {p -> list.add(p.toFile())}
stream.close()
list.sort()
list.forEach {f -> addFile(f)}
list.clear()
wad.addEntry(WadEntry.create("G_END"))

wad.close()