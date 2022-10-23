package ballotScanner

import utils.Ballot
import utils.BallotScan

class ConsoleBallotScanner : BallotScanner {
    /**
     * Fakes scanning a ballot by reading the console, updates the recorded information, returns a dummy image
     */
    override fun scan(ballot: Ballot): BallotScan {
        println("Which candidate would you like to choose?")
        ballot.candidates.joinToString(separator = "\n - ", prefix = " - ") { "${it.name} (${it.id})" }
        val input = System.console().readLine()

        while (ballot.vote == null) {
            ballot.vote = ballot.candidates.find { it.id == input }
            ballot.vote ?: println("Invalid input, try again")
        }
        return BallotScan.dummy()
    }
}