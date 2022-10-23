package ballotScanner

import utils.Ballot
import utils.BallotScan

/**
 * Implements functionality to scan a ballot.
 */
interface BallotScanner {
    /**
     * Scans a ballot, updates the recorded information and returns the scan
     */
    fun scan(ballot: Ballot): BallotScan
}