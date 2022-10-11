import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Parser(fileName: String) {
    private var scanner: Scanner
    private var currentCommand: String? = null

    init {
        scanner = Scanner(File(fileName))
    }

    fun hasMoreCommands(): Boolean {
        return scanner.hasNextLine()
    }

    fun advance() {
        currentCommand = scanner.nextLine()
        currentCommand = currentCommand!!.replace("//.*|\\s|^$", "")
    }

    fun commandType(): CommandType? {
        return when {
            currentCommand!!.startsWith("@") -> CommandType.A_COMMAND
            currentCommand!!.startsWith("(") -> CommandType.L_COMMAND
            else -> CommandType.C_COMMAND
        }
    }

    fun symbol(): String? {
        val regex = "[a-zA-Z0-9_.$:]+"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(currentCommand)
        return if (matcher.find()) matcher.group() else throw Exception("No matches")
    }

    fun dest(): String {
        return if (currentCommand!!.contains("=")) currentCommand!!.split("=".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0] else ""
    }

    fun comp(): String {
        val s = currentCommand!!.split("\\=|\\;".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (dest().isEmpty()) s[0] else s[1]
    }

    fun jump(): String {
        return if (currentCommand!!.contains(";")) currentCommand!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1] else ""
    }
}