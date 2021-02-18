import org.jetbrains.kotlin.spec.grammar.tools.*
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser

fun main(args : Array<String>) {
    val a = Args

    val parser = CmdLineParser(a)
    try {
        parser.parseArgument(*args)
    } catch (e: CmdLineException) {
        System.err.println(e.message)
        parser.printUsage(System.err)
    }

    val files = ProjectParser().parseProject()
    println("Total Kotlin Files: " + (files.size))
    if (files.isNotEmpty()) {
        files.forEach { f -> println("\t" + f.name) }

        val guest = VisitorAST()
        files.forEach { f ->
            val tokens = tokenizeKotlinCode(f.readText())
            val parseTree = parseKotlinCode(tokens)

            walkAround(parseTree, guest)
        }

        guest.draw()

        val jB = JsonBuilder()
        if (Args.fileName != "")
            jB.createFileJson(jB.createJSON(guest.getMetrics()))
    }
}

private fun walkAround(node: KotlinParseTree, guest: VisitorAST) {
    guest.accept(node)
    node.children.forEach { child ->
        walkAround(child, guest)
    }
}
