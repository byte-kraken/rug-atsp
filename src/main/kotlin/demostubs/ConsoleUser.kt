package demostubs

import java.util.*

class ConsoleUser : ApplicationUser {
    private fun readInput(secret: Boolean = false): String {
        if (secret) return System.console().readPassword().toString()
        return System.console().readLine()
    }

    override fun enterUsername(): String {
        print("\nPlease enter your desired username: ")
        return readInput()
    }

    override fun enterPassword(): String {
        print("\nPlease enter your desired password: ")
        return readInput(true)
    }

    override fun enterSSN(): Int {
        print("\nPlease enter your Social Security Number: ")
        return Integer.parseInt(readInput(true))
    }

    override fun chooseElection(): Int {
        print("\nPlease pick an election to register to: ")
        return Integer.parseInt(readInput(true))
    }

    override fun enterYN(): Boolean {
        print("\nEnter Y for yes, N for no (default: Y): ")
        val input = readInput()
        return input.uppercase(Locale.getDefault())[0] != 'N'
    }
}