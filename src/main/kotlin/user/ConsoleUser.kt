package user

import java.util.*

/**
 * A simulation of an application user using the console for communication.
 */
class ConsoleUser : ApplicationUser {
    /**
     * Reads input from console.
     *
     * @param secret if true, the input will be read as password and thus not be shown when typing.
     * @return the read input
     */
    private fun readInput(secret: Boolean = false): String {
        if (secret) return System.console().readPassword().toString()
        return System.console().readLine()
    }

    /**
     * Allows the user to enter their username.
     *
     * @return the read input
     */
    override fun enterUsername(): String {
        print("\nPlease enter your desired username: ")
        return readInput()
    }

    /**
     * Allows the user to enter their password.
     *
     * @return the read input
     */
    override fun enterPassword(): String {
        print("\nPlease enter your desired password: ")
        return readInput(true)
    }

    /**
     * Allows the user to enter their social security number.
     *
     * @return the read input, parsed as integer
     */
    override fun enterSSN(): Int {
        print("\nPlease enter your Social Security Number: ")
        return Integer.parseInt(readInput(true))
    }

    /**
     * Allows the user to choose an election by typing its number.
     *
     * @return the read input, parsed as integer
     */
    override fun chooseElection(): Int {
        print("\nPlease pick an election to register to: ")
        return Integer.parseInt(readInput(true))
    }

    /**
     * Allows a user to agree or deny a prompt.
     *
     * @return the chosen option
     */
    override fun enterYN(): Boolean {
        print("\nEnter Y for yes, N for no (default: Y): ")
        val input = readInput()
        return input.uppercase(Locale.getDefault())[0] != 'N'
    }
}