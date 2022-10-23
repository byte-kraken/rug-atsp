package absenteeBallotFiller

import utils.AbsenteeBallotRequest

/**
 * Contracts functionality an actor filling out an Absentee Ballot must have.
 */
interface AbsenteeBallotFiller {
    /**
     * Every Absentee Ballot Request Form needs to be signed.
     *
     * @param absenteeBallotRequest the form to sign
     */
    fun signForm(absenteeBallotRequest: AbsenteeBallotRequest)
}