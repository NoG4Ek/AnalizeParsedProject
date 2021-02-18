import org.jetbrains.kotlin.spec.grammar.tools.*

fun main() {
    val files = ProjectParser().parseProject("test.kt")
    val guest = VisitorAST()

    files.forEach { f ->
        val tokens = tokenizeKotlinCode(f.readText())
        val parseTree = parseKotlinCode(tokens)
        println(parseTree)
        walkAround(parseTree, guest)
    }

    println("Total Kotlin Files: " + (files.size))
    files.forEach { f -> println("\t" + f.name)}
    guest.draw()

    val jB = JsonBuilder()
    jB.createFileJson(jB.createJSON(guest.getMetrics()), "metrics")
}

private fun walkAround(node: KotlinParseTree, guest: VisitorAST) {
    guest.accept(node)
    node.children.forEach { child ->
        walkAround(child, guest)
    }
}
