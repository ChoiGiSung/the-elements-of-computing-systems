


import java.text.MessageFormat

import java.util.Arrays

import java.io.IOException

import java.io.BufferedReader

import java.io.FileNotFoundException

import java.io.FileReader

import java.io.File

import java.util.LinkedHashMap

/**
 * from @ReionChan
 */
class Parser {

    companion object{
        val DEST_SEPARATOR = "="
        val JUMP_SEPARATOR = ";"
        val VM_SEPARATOR = " "

        val TYPE_ARI_LOG = arrayOf(
            "add", "sub", "neg", "eq",
            "gt", "lt", "and", "or", "not"
        )

        val TYPE_PUSH = "push"
        val TYPE_POP = "pop"
        val TYPE_LABEL = "label"
        val TYPE_GOTO = "goto"
        val TYPE_IF = "if-goto"
        val TYPE_FUNCTION = "function"
        val TYPE_RETURN = "return"
        val TYPE_CALL = "call"
    }


    enum class CommandType {
        C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
    }

    private var curInstruct: Array<String> = emptyArray()

    private var curInsStr: String? = null

    private var curInsNum = -1

    private var curType: CommandType? = null

    private var totalNum = -1

    private val insSeqs: MutableMap<Int, String> = LinkedHashMap()

    private var code: CodeWriter? = null

    private var funcName: String? = null

    fun setCode(code: CodeWriter?) {
        this.code = code
    }

    fun Parser(file: File?) {
        var fr: FileReader? = null
        var br: BufferedReader? = null
        try {
            fr = FileReader(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        br = BufferedReader(fr)

        var curStr: String? = null
        var lineNum = -1
        try {
            while (br.readLine().also { curStr = it } != null) {
                if (curStr!!.startsWith(CodeWriter.COMMENT_TAG)
                    || curStr!!.trim { it <= ' ' }.length < 1
                ) {
                    continue
                }
                if (curStr!!.indexOf(CodeWriter.COMMENT_TAG) > 0) {
                    curStr = curStr!!.substring(
                        0,
                        curStr!!.indexOf(CodeWriter.COMMENT_TAG)
                    ).trim { it <= ' ' }
                }
                curStr = curStr!!.replace("\\s+".toRegex(), VM_SEPARATOR).trim { it <= ' ' }
                insSeqs[++lineNum] = curStr!!
            }
            totalNum = insSeqs.size
            fr?.close()
            if (br != null) {
                br.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun hasMoreCommands(): Boolean {
        return totalNum - curInsNum - 1 > 0
    }

    fun advance() {
        if (!hasMoreCommands()) {
            return
        }
        curInsNum++
        curInsStr = insSeqs[curInsNum]
        curInstruct = insSeqs[curInsNum]!!.split(VM_SEPARATOR.toRegex()).toTypedArray()
        curType = commandType()
        if (curType!!.compareTo(CommandType.C_FUNCTION) == 0) {
            funcName = arg1()
        }
    }

    fun commandType(): CommandType? {
        if (Arrays.asList<Any>(TYPE_ARI_LOG).contains(curInstruct[0])) {
            return CommandType.C_ARITHMETIC
        } else if (TYPE_PUSH.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_PUSH
        } else if (TYPE_POP.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_POP
        } else if (TYPE_LABEL.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_LABEL
        } else if (TYPE_GOTO.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_GOTO
        } else if (TYPE_IF.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_IF
        } else if (TYPE_FUNCTION.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_FUNCTION
        } else if (TYPE_RETURN.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_RETURN
        } else if (TYPE_CALL.equals(curInstruct[0], ignoreCase = true)) {
            return CommandType.C_CALL
        }
        return null
    }

    fun arg1(): String? {
        if (curType!!.compareTo(CommandType.C_RETURN) == 0) {
            throw RuntimeException("No arg1 if type is C_RETURN!")
        }
        return if (curType!!.compareTo(CommandType.C_ARITHMETIC) == 0) {
            curInstruct.get(0)
        } else curInstruct.get(1)
    }


    fun arg2(): Int {
        return if ((curType!!.compareTo(CommandType.C_PUSH) == 0
                    ) || (curType!!.compareTo(CommandType.C_POP) == 0
                    ) || (curType!!.compareTo(CommandType.C_FUNCTION) == 0
                    ) || (curType!!.compareTo(CommandType.C_CALL) == 0)
        ) {
            curInstruct.get(2).toInt()
        } else {
            throw RuntimeException(
                ("No arg2 if type is "
                        + curType.toString())
            )
        }
    }

    fun trans() {
        while (hasMoreCommands()) {
            advance()
            code!!.writeComment(
                "'" + curInsStr + "'" + " (Line " + curInsNum
                        + ")"
            )
            if ((curType!!.compareTo(CommandType.C_PUSH) == 0
                        || curType!!.compareTo(CommandType.C_POP) == 0)
            ) {
                code!!.writePushPop(curInstruct[0], arg1()!!, arg2())
            } else if (curType!!.compareTo(CommandType.C_ARITHMETIC) == 0) {
                code!!.writeArithmetic(curInstruct[0])
            } else if (curType!!.compareTo(CommandType.C_LABEL) == 0) {
                code!!.writeLabel(MessageFormat.format(CodeWriter.LABEL_PATTEN1, arrayOf<Any?>(funcName, arg1())))
            } else if (curType!!.compareTo(CommandType.C_IF) == 0) {
                code!!.writeIf(MessageFormat.format(CodeWriter.LABEL_PATTEN1, arrayOf<Any?>(funcName, arg1())))
            } else if (curType!!.compareTo(CommandType.C_GOTO) == 0) {
                code!!.writeGoto(MessageFormat.format(CodeWriter.LABEL_PATTEN1, arrayOf<Any?>(funcName, arg1())))
            } else if (curType!!.compareTo(CommandType.C_FUNCTION) == 0) {
                code!!.writeFunction(arg1(), arg2())
            } else if (curType!!.compareTo(CommandType.C_RETURN) == 0) {
                code!!.writeReturn()
            } else if (curType!!.compareTo(CommandType.C_CALL) == 0) {
                code!!.writeCall(arg1(), arg2())
            }
        }
    }

}