package absenteeBallotFiller

import utils.AbsenteeBallotRequest

/**
 * Simulates a person filling out an Absentee Ballot on paper by allowing signing in console for demo purposes.
 */
class ConsoleAbsenteeBallotFiller : AbsenteeBallotFiller {
    /**
     * Every Absentee Ballot Request Form needs to be signed.
     * Takes any console input as signature.
     *
     * @param absenteeBallotRequest the form to sign
     */
    override fun signForm(absenteeBallotRequest: AbsenteeBallotRequest) {
        print("\nPlease sign here: ")
        readLine()
    }
}