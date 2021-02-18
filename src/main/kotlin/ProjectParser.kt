import org.kohsuke.args4j.Option
import java.io.File
import java.nio.file.Paths

class ProjectParser {
    private val ktFiles = mutableListOf<File>()

    fun parseProject(): List<File> {
        val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val resourcesPath = Paths.get(projectDirAbsolutePath, "./${Args.nameProjectFolder}")

        val dir = File(resourcesPath.toUri())
        dir.walk().forEach { f ->
            if(f.isFile && f.name.matches(Regex(".*.kt"))) {
                ktFiles.add(f)
            }
        }
        return ktFiles
    }
}