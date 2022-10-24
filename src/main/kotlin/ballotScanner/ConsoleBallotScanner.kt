package ballotScanner

import user.ConsoleUser.Companion.getValidNumber
import utils.Ballot
import utils.BallotScan

/**
 * A simulated scanner in console.
 */
class ConsoleBallotScanner : BallotScanner {
    /**
     * Fakes scanning a ballot by reading the console, updates the recorded information, returns a dummy image.
     *
     * @param ballot the ballot to scan
     * @return a dummy image
     */
    override fun scan(ballot: Ballot): BallotScan {
        println("${ballot.election.name}: Vote by typing your choice's number!")
        ballot.candidates.forEachIndexed { idx, str -> println("[$idx] ${str.id}") }

        val input = getValidNumber(ballot.candidates.size)
        ballot.vote = ballot.candidates[input]

        return BallotScan.dummy()
    }
}