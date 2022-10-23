package ballotScanner

import utils.Ballot
import utils.BallotScan

/**
 * Implements functionality to scan a ballot.
 */
interface BallotScanner {
    /**
     * Scans a ballot, updates the recorded information and returns the scan.
     *
     * @param ballot the filled out ballot that is being scanned.
     * @return the ballot scan as image
     */
    fun scan(ballot: Ballot): BallotScan
}