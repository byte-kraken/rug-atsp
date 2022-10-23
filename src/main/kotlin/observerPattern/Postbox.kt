package observerPattern

import utils.Ballot
import java.util.*

/**
 * An implementation of a MailObserver.
 *
 * @property uuid the user id the postbox is looking for
 * @property ballotID the ballot the postbox is looking for
 */
class PostBox(private val uuid: UUID, private val ballotID: Int) : MailObserver {
    /**
     * The ballot the postbox is looking for.
     */
    var ballot: Ballot? = null
        private set

    /**
     * Getter.
     *
     * @return the uuid the mail should be addressed to
     */
    override fun getUUID(): UUID {
        return uuid
    }

    /**
     * Getter.
     *
     * @return the ballot id the mail should be about
     */
    override fun getBallotID(): Int {
        return ballotID
    }

    /**
     * Updates the ballot once one is found.
     */
    override fun update(ballot: Ballot) {
        this.ballot = ballot
    }
}