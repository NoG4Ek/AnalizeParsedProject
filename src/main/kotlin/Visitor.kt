import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

interface Visitor {
    fun accept(node: KotlinParseTree)
}

class VisitorAST : Visitor {
    data class Metrics(val maxDepthInheritance: Int = 0, val averageDepthInheritance: Double = 0.0,
                       val assignments: Int = 0, val branches: Int = 0, val conditions: Int = 0, val
                       averageOverrideMethods: Double = 0.0, val averageFieldClass: Double = 0.0)

    private var metrics: Metrics = Metrics()
    fun getMetrics(): Metrics {
        assembleMetrics()
        return metrics
    }

    private val classes = mutableMapOf<String, Pair<String, Int>>()
    private var assignments = 0
    private var branches = 0
    private var conditions = 0
    private var countOverrides = 0
    private var countProperties = 0

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
    override fun accept(node: KotlinParseTree) {
        when (node.name) {
            CLASS_DECLARATION -> {
                val className = findClassName(node)

                if (className != "") {
                    val del = findClassDelegation(node)
                    if (classes.containsKey(del)) {
                        classes[className] = Pair(del, classes[del]!!.second + 1)
                    } else {
                        if (del != "") {
                            classes[className] = Pair(del, 1)
                        } else {
                            classes[className] = Pair("", 0)
                        }
                    }
                }

                countProperties += findPropertyDeclaration(node)

            }

            in REG_ASSIGNMENT_UNARY -> {
                assignments += 1
            }

            FUNC_SUFFIX -> {
                branches += 1
            }

            in REG_IF_WHEN_EXPRESSIONS -> {
                conditions++
            }

            in REG_OVERRIDE -> {
                countOverrides++
            }
        }
    }

    fun draw() {
        assembleMetrics()

        //println("$classes \n")
        println()
        println("Max Class Depth Inheritance: ${metrics.maxDepthInheritance}")
        println("Average Class Depth Inheritance: ${metrics.averageDepthInheritance}")

        println("ABC metric:")
        println("\t Assignments: ${metrics.assignments}")
        println("\t Branches: ${metrics.branches}")
        println("\t Conditions: ${metrics.conditions}")
        println("Average Override Class Methods: ${metrics.averageOverrideMethods}")
        println("Average Field Class: ${metrics.averageFieldClass}")
    }

    private fun assembleMetrics() {
        metrics = Metrics(maxDeepDel(), averageDeepDel(), assignments, branches, conditions,
            countOverrides.toDouble() / classes.size.toDouble(),
            countProperties.toDouble() / classes.size.toDouble())
    }

    private fun maxDeepDel(): Int {
        var max = 0
        classes.forEach { cl ->
            if (cl.value.second > max) max = cl.value.second
        }
        return max
    }

    private fun averageDeepDel(): Double {
        var sum = 0.0
        var count = 0.0

        loop@ for (i in 0 until classes.size) {
            for (j in i + 1 until classes.size) {
                if (classes.keys.toList()[i] == classes[classes.keys.toList()[j]]!!.first) {
                    continue@loop
                }
            }
            count++
            sum += classes[classes.keys.toList()[i]]!!.second
        }
        return sum / count
    }


    private fun findClassName(node: KotlinParseTree): String {
        val cl = findChildren(node, "CLASS")
        if (cl.isNotEmpty())
            return findChildren(node, "simpleIdentifier")[0]!!.children[0].text!!
        return ""
    }

    private fun findClassDelegation(node: KotlinParseTree): String {
        val del = findChildren(node, "delegationSpecifiers")
        if (del.isNotEmpty()) {
            del[0]!!.children.forEach { child ->
                val constructor = findClosestChild(child, "constructorInvocation")
                if (constructor != null) {
                    return findClosestChild(child, "simpleIdentifier")!!.children[0].text!!
                }
            }
        }
        return ""
    }

    private fun findPropertyDeclaration(node: KotlinParseTree): Int {
        val body = findChildren(node, "classBody")
        var declarations = 0
        if (body.isNotEmpty()) {
            val declaS = findChildren(body[0]!!, "classMemberDeclarations")
            if (declaS.isNotEmpty()) {
                declaS[0]!!.children.forEach { child ->
                    var declaration = ""
                    if (child.children.isNotEmpty() && child.children[0].children.isNotEmpty())
                        declaration = child.children[0].children[0].name
                    if (declaration == "propertyDeclaration") {
                        declarations++
                    }
                }
            }
        }
        return declarations
    }


    private fun findClosestChild(node: KotlinParseTree, term: String): KotlinParseTree? {
        return findClosestChild(node, Regex(term))
    }

    private fun findClosestChild(node: KotlinParseTree, term: Regex): KotlinParseTree? {
        node.children.forEach { child ->
            if (term.matches(child.name)) {
                return child
            }
            val o = findClosestChild(child, term)
            if (o != null) {
                return o
            }
        }
        return null
    }

    private fun findChildInChildren(node: KotlinParseTree, term: String): KotlinParseTree? =
        findChildInChildren(node, Regex(term))

    private fun findChildInChildren(node: KotlinParseTree, term: Regex): KotlinParseTree? =
        findChildren(node, term).takeIf { it.isNotEmpty() }?.get(0)


    private fun findChildren(node: KotlinParseTree, term: String): List<KotlinParseTree?> {
        return findChildren(node, Regex(term))
    }

    private fun findChildren(node: KotlinParseTree, term: Regex): List<KotlinParseTree?> {
        val children = mutableListOf<KotlinParseTree?>()
        node.children.forEach { child ->
            if (term.matches(child.name)) {
                children.add(child)
            }
        }
        return children
    }
}