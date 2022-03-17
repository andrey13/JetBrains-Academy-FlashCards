package flashcards

import java.io.File
import kotlin.math.max
import kotlin.random.Random

data class Card(val term: String, var def: String, var err: Int = 0)

//

val cards = mutableListOf<Card>()
val logs = mutableListOf<String>()

fun printlnLog(s: String) {
    println(s)
    logs.add(s)
}

fun printLog(s: String) {
    print(s)
    logs.add(s)
}

fun readLineLog(): String {
    val s = readLine()!!
    logs.add(s)
    return s
}

fun containTerm(t: String): Boolean {
    for (card in cards) if (card.term == t) return true
    return false
}

fun containDef(d: String): Boolean {
    for (card in cards) if (card.def == d) return true
    return false
}

fun indexTerm(t: String): Int {
    var index = 0
    for (card in cards) {
        if (card.term == t) return index
        ++index
    }
    return -1
}

fun testCard(card: Card): Boolean {
    printlnLog("Print the definition of \"${card.term}\"")
    val ans = readLineLog()
    if (ans == card.def) {
        printlnLog("Correct!")
        return true
    } else {
        var str = "Wrong. The right answer is \"${card.def}\""
        for (c in cards) {
            if (c.term != card.term && c.def == ans) str += ", but your definition is correct for \"${c.term}\""
        }
        printlnLog(str + ".")
        return false
    }
}

fun addCard() {
    printlnLog("The card:")
    val term  = readLineLog()
    if (containTerm(term)) {
        printlnLog("The card \"$term\" already exists.")
        return
    }
    printlnLog("The definition of the card:")
    val def = readLineLog()
    if (containDef(def)) {
        printlnLog("The definition \"$def\" already exists.")
        return
    }
    cards.add(Card(term, def))
    printlnLog("The pair (\"$term\":\"$def\") has been added.")
}

fun removeTerm(t: String) {
    cards.forEach {
        if (it.term == t) {
            cards.remove(it)
            printlnLog("The card has been removed.")
            return
        }
    }
}

fun removeCard() {
    printlnLog("Which card?")
    val term  = readLineLog()
    if (containTerm(term)) {
        removeTerm(term)
    } else {
        printlnLog("Can't remove \"$term\": there is no such card.")
    }

}

fun exportCard(fileName: String) {
    val file = File(fileName)
    file.writeText("")
    cards.forEach { file.appendText("${it.term}|${it.def}|${it.err}\n") }
    printlnLog("${cards.size} cards have been saved.")
}

fun importCard(fileName: String) {
    val file = File(fileName)
    var nCard = 0
    if (file.exists()) {
        File(fileName).forEachLine {
            val (term, def, err) = it.split("|")
            val index = indexTerm(term)
            if (index != -1) {
                cards[index] = Card(term, def, err.toInt())
            } else {
                cards.add(Card(term, def, err.toInt()))
            }
            ++nCard
        }
        printlnLog("$nCard cards have been loaded.")
    } else {
        printlnLog("File not found.")
    }
}

fun askCard() {
    printlnLog("How many times to ask?")
    val nAsk = readLineLog().toInt()
    val defaultGenerator = Random.Default
    repeat(nAsk) {
        val iCard = defaultGenerator.nextInt(0, cards.size)
        if (!testCard(cards[iCard])) {
            ++cards[iCard].err
        }
    }
}

fun resetStat() {
    cards.forEach { it.err = 0 }
    printlnLog("Card statistics have been reset.")
}

fun hardestCard() {
    val errorCards = mutableListOf<String>()
    var errMax = 0
    cards.forEach { errMax = max(errMax, it.err) }
    cards.forEach { if (it.err == errMax) errorCards.add(it.term) }
    if (errMax == 0) {
        printlnLog("There are no cards with errors.")
        return
    }
    when (errorCards.size) {
        0 -> printlnLog("There are no cards with errors.")
        1 -> printlnLog("The hardest card is \"${errorCards[0]}\". You have ${errMax} errors answering it.")
        else -> {
            var terms = ""
            errorCards.forEach { terms += " \"$it\"," }
            terms = terms.substring(1 until terms.length-1)
            printlnLog("The hardest cards are $terms. You have ${errMax} errors answering them.")
        }
    }
}

fun logCard() {
    printlnLog("File name:")
    val fileName = readLineLog()
    val file = File(fileName)
    file.writeText("")
    logs.forEach { file.appendText("$it\n") }
    printlnLog("The log has been saved.")
}

fun inputData(args: Array<String>) {
    var command = ""
    var fileName = ""
    args.forEach {
        if (command == "-import") {
            fileName = it
            command = ""
        }
        command = if (it == "-import") it else command
    }
    if (fileName != "") importCard(fileName)
}

fun outputData(args: Array<String>) {
    var command = ""
    var fileName = ""
    args.forEach {
        if (command == "-export") {
            fileName = it
            command = ""
        }
        command = if (it == "-export") it else command
    }
    if (fileName != "") exportCard(fileName)
}

fun main(args: Array<String>) {
    inputData(args)
    while(true) {
        printlnLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val ans = readLineLog()
        when(ans) {
            "add" -> addCard()
            "remove" -> removeCard()
            "export" -> {
                printlnLog("File name:")
                val fileName = readLineLog()
                exportCard(fileName)
            }
            "import" -> {
                printlnLog("File name:")
                val fileName = readLineLog()
                importCard(fileName)
            }
            "ask" -> askCard()
            "exit" -> break
            "log" -> logCard()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStat()
            else -> {}
        }
    }
    printlnLog("Bye bye!")
    outputData(args)
    return
}