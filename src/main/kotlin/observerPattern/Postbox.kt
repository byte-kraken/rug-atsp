package observerPattern

import utils.Ballot
import java.util.*

class PostBox(private val uuid: UUID, private val ballotID: Int) : MailObserver {
    var ballot: Ballot? = null
        private set

    override fun getUUID(): UUID {
        return uuid
    }

    override fun getBallotID(): Int {
        return ballotID
    }

    override fun update(ballot: Ballot) {
        this.ballot = ballot
    }
}