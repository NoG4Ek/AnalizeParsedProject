import com.google.gson.Gson
import java.io.File
import java.nio.file.Paths

class JsonBuilder {

    fun createJSON(metrics: VisitorAST.Metrics): String = """
   { "maxDepthInheritance": ${metrics.maxDepthInheritance},
     "averageDepthInheritance": ${metrics.averageDepthInheritance},
     "abc": ${metrics.abc},
     "assignments": ${metrics.assignments},
     "branches": ${metrics.branches},
     "conditions": ${metrics.conditions},
     "averageOverrideMethods": ${metrics.averageOverrideMethods},
     "averageFieldClass": ${metrics.averageFieldClass}
   }
"""

    fun getMetricsFromJSON(json: String): VisitorAST.Metrics? =
        Gson().fromJson(json, VisitorAST.Metrics::class.java)

    fun createFileJson(json: String) {
        val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
        val resourcesPath = Paths.get(projectDirAbsolutePath, "./${Args.fileName}.json")

        val exFile = File(resourcesPath.toUri())
        if (exFile.exists()) exFile.delete()
        val file = File("./", "${Args.fileName}.txt")
        file.printWriter().use { out ->
            out.print(json)
        }
        file.renameTo(File("./${Args.fileName}.json"))
    }

}