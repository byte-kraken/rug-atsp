package demostubs

import Ballot
import java.util.*

interface MailObserver {
    fun getUUID(): UUID
    fun getBallotID(): Int
    fun update(ballot: Ballot)
}