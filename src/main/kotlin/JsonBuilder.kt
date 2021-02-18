import com.google.gson.Gson
import java.io.File

class JsonBuilder {
    fun createJSON(metrics: VisitorAST.Metrics): String = """
   { "maxDepthInheritance": ${metrics.maxDepthInheritance},
     "averageDepthInheritance": ${metrics.averageDepthInheritance},
     "assignments": ${metrics.assignments},
     "branches": ${metrics.branches},
     "conditions": ${metrics.conditions},
     "averageOverrideMethods": ${metrics.averageOverrideMethods},
     "averageFieldClass": ${metrics.averageFieldClass}
   }
"""

    fun getMetricsFromJSON(json: String): VisitorAST.Metrics? =
        Gson().fromJson(json, VisitorAST.Metrics::class.java)

    fun createFileJson(json: String, fileName: String) {
        val file = File("./", "$fileName.txt")
        file.printWriter().use { out ->
            out.print(json)
        }
        file.renameTo(File("./$fileName.json"))
    }

}