import org.kohsuke.args4j.Option

object Args {

    @Option(name = "-n", usage = "NameFileForJSON", required = true)
    var fileName: String = ""

    @Option(name = "-p", usage = "NameProjectFolder", required = true)
    var nameProjectFolder: String = ""
}