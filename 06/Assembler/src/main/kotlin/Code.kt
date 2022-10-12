import java.util.Collections
import java.util.function.Function

import java.util.stream.Collectors

import java.util.stream.Stream


object Code {
    private val comp = mapOf<String, String>(
        "0" to "101010",
        "1" to "111111",
        "-1" to "111010",
        "D" to "001100",
        "A" to "110000",
        "M" to "110000",
        "!D" to "001101",
        "!A" to "110001",
        "!M" to "110001",
        "-D" to "001111",
        "-A" to "110011",
        "-M" to "110011",
        "D+1" to "011111",
        "A+1" to "110111",
        "M+1" to "110111",
        "D-1" to "001110",
        "A-1" to "110010",
        "M-1" to "110010",
        "D+A" to "000010",
        "D+M" to "000010",
        "D-A" to "010011",
        "D-M" to "010011",
        "A-D" to "000111",
        "M-D" to "000111",
        "D&A" to "000000",
        "D&M" to "000000",
        "D|A" to "010101",
        "D|M" to "010101"
    )

    private val dest = mapOf(
        "M" to "001",
        "D" to "010",
        "MD" to "011",
        "A" to "100",
        "AM" to "101",
        "AD" to "110",
        "AMD" to "111"
    )
    private val jump = mapOf(
        "JGT" to "001",
        "JEQ" to "010",
        "JGE" to "011",
        "JLT" to "100",
        "JNE" to "101",
        "JLE" to "110",
        "JMP" to "111"
    )

    fun dest(instruction: String): String? {
        return if (instruction.isEmpty()) "000" else dest[instruction]
    }

    fun comp(instruction: String): String? {
        return if (instruction.isEmpty()) "000" else comp[instruction]
    }

    fun jump(instruction: String): String? {
        return if (instruction.isEmpty()) "000" else jump[instruction]
    }
}