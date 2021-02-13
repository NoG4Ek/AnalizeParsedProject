import org.jetbrains.kotlin.spec.grammar.tools.*

fun main() {
    val files = ProjectParser().parseProject("Test.kt")
    val guest = Guest()

    files.forEach { f ->
        val tokens = tokenizeKotlinCode(f.readText())
        val parseTree = parseKotlinCode(tokens)
        println(parseTree)
        walkAround(parseTree, guest)
    }

    println("Total Kotlin Files: " + (files.size))
    files.forEach { f -> println("\t" + f.name)}
    guest.draw()
}

private fun walkAround(node: KotlinParseTree, guest: Guest) {
    guest.accept(node)
    node.children.forEach { child ->
        walkAround(child, guest)
    }
}

interface Visitor {
    fun accept(node: KotlinParseTree)
}

class Guest : Visitor {
    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

    override fun accept(node: KotlinParseTree) {
        when (node.name) {
            "classDeclaration" -> {
                val del = findDelegation(node)
                if (classes.containsKey(del)) {
                    classes[findClassName(node)] = Pair(del, classes[del]!!.second + 1)
                } else {
                    if (del != "") {
                        classes[findClassName(node)] = Pair(del, 1)
                    }else {
                        classes[findClassName(node)] = Pair("", 0)
                    }
                }
            }

            in Regex(".*(ASSIGNMENT).*") -> {
                assignments += 1
            }

            "callSuffix" -> {
                branches += 1
            }

            in Regex("ifExpression|whenExpression") -> {
                conditions++
            }

            in Regex(".*(OVERRIDE).*") -> {
                countOverrides++
            }

            "classMemberDeclaration"  -> {
                countProperties += findPropertyDeclaration(node)
            }
        }
    }

    private val classes = mutableMapOf<String, Pair<String, Int>>()
    private var assignments = 0
    private var branches = 0
    private var conditions = 0
    private var countOverrides = 0
    private var countProperties = 0



    fun draw() {
        println()

        println(classes)
        println("Максимальная глубина наследования: " + maxDeepDel())
        println("Средняя глубина наследования: " + averageDeepDel())

        println("Количество присваиваний значений переменной: $assignments")
        println("Количество вызовов методов: $branches")
        println("Количество пераций сравнения: $conditions")
        println("Средние количество переопределенных методов: " + countOverrides.toDouble() / classes.size.toDouble())
        println("Среднее число полей в классе: " + countProperties.toDouble() / classes.size.toDouble())
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
            for (j in i until classes.size) {
                if (classes.keys.toList()[i] == classes[classes.keys.toList()[j]]!!.first) {
                    continue@loop
                }
            }
            count++
            sum += classes[classes.keys.toList()[i]]!!.second
        }
        return sum / count
    }
}

private fun findClassName(node: KotlinParseTree) =
    findChild(node, "simpleIdentifier")!!.children[0].text!!

private fun findDelegation(node: KotlinParseTree): String {
    val del = findChild(node, "delegationSpecifiers")
    //for
    if (del != null) {
        return findChild(del, "simpleIdentifier")!!.children[0].text!!
    }
    return ""
}

private fun findPropertyDeclaration(node: KotlinParseTree): Int {
    //println(findChild(node, "propertyDeclaration"))
    return if (findChild(node, "propertyDeclaration") != null) 1 else 0
}



private fun findChild(node: KotlinParseTree, term: String): KotlinParseTree? {
    return findChild(node, Regex(term))
}
private fun findChild(node: KotlinParseTree, term: Regex): KotlinParseTree? {
    node.children.forEach { child ->
        if (term.matches(child.name)) {
            return child
        }
    }
    return null
}
