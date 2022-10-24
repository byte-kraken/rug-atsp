package user

import utils.Election

/**
 * Contracts functionality a voter using the Application needs to have.
 */
interface ApplicationUser {
    /**
     * The user must be capable to choose and enter a desired username.
     *
     * @return the chosen username
     */
    fun enterUsername(): String

    /**
     * The user must be capable to choose and enter a desired password.
     *
     * @return the chosen password
     */
    fun enterPassword(): String

    /**
     * The user must be capable to enter their social security number.
     *
     * @return the user's social security number
     */
    fun enterSSN(): Int

    /**
     * If there are multiple elections the user could register to, they are asked to choose.
     *
     * @param possibleElections the election the user can choose from
     * @return the chosen election
     */
    fun chooseElection(possibleElections: List<Election>): Election?

    /**
     * Allows a user to agree or deny a prompt.
     *
     * @return the chosen option
     */
    fun enterYN(): Boolean
}