package observerPattern

import utils.Ballot
import java.util.*

/**
 * Implements an observer in an observer pattern for mail being sent by a mail service.
 */
interface MailObserver {
    /**
     * Every observer must look for mail addressed to a specific user.
     *
     * @return the uuid the mail is addressed to
     */
    fun getUUID(): UUID

    /**
     * Every observer must look for mail regarding a specific ballot.
     *
     * @return the ballot ID that is observed
     */
    fun getBallotID(): Int

    /**
     * The update that should be triggered once matching mail is found.
     *
     * @param ballot the ballot that was found.
     */
    fun update(ballot: Ballot)
}