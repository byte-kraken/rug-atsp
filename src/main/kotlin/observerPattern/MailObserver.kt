package observerPattern

import utils.Ballot
import java.util.*

interface MailObserver {
    fun getUUID(): UUID
    fun getBallotID(): Int
    fun update(ballot: Ballot)
}