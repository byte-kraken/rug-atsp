package user

import utils.Election
import java.util.*

/**
 * A simulation of an application user using the console for communication.
 */
class ConsoleUser : ApplicationUser {

    /**
     * Allows the user to enter their username.
     *
     * @return the read input
     */
    override fun enterUsername(): String {
        print("Please enter your desired username: ")
        return readLine() ?: "Rick"
    }

    /**
     * Allows the user to enter their password.
     *
     * @return the read input
     */
    override fun enterPassword(): String {
        print("Please enter your desired password: ")
        return readLine() ?: "hunter3"
    }

    /**
     * Allows the user to enter their social security number.
     *
     * @return the read input, parsed as integer
     */
    override fun enterSSN(): Int {
        print("Please enter your Social Security Number: ")
        return getValidNumber(errorMessage = "Please enter numbers only!")
    }

    /**
     * Allows the user to choose an election by typing its number.
     *
     * @return the read input, parsed as integer
     */
    override fun chooseElection(possibleElections: List<Election>): Election? {
        if (possibleElections.isEmpty()) return null
        //if(possibleElections.size == 1) return possibleElections[0]

        println("Please pick an election: ")
        possibleElections.forEachIndexed { idx, str -> println("[$idx] ${str.name}") }

        return possibleElections[getValidNumber(possibleElections.size, "Illegal choice, pick one of the IDs!")]
    }

    /**
     * Allows a user to agree or deny a prompt.
     *
     * @return the chosen option
     */
    override fun enterYN(): Boolean {
        print("Enter Y for yes, N for no (default: Y): ")
        val input = readLine()
        return input?.uppercase(Locale.getDefault())?.get(0) != 'N'
    }

    companion object {
        /**
         * Reads input from console until a valid number is given.
         *
         * @param upperBound the upper bound of the number (lower bound = 0)
         * @param errorMessage the error message displayed on failure
         * @return the valid number
         */
        fun getValidNumber(
            upperBound: Int = Integer.MAX_VALUE,
            errorMessage: String = "Illegal choice, try again!",
        ): Int {
            return try {
                var choice = Integer.parseInt(readLine() ?: "0")

                while (choice >= upperBound || choice < 0) {
                    println(errorMessage)
                    choice = Integer.parseInt(readLine() ?: "0")
                }
                choice
            } catch (ex: NumberFormatException) {
                println(errorMessage)
                getValidNumber(upperBound, errorMessage)
            }
        }
    }
}