package demostubs

import Ballot
import BallotStatus
import BallotTemplate
import java.util.*

class WormholeMailService(private val observers: MutableList<MailObserver> = mutableListOf()) : MailService {
    fun observe(mailObserver: MailObserver) {
        observers.add(mailObserver)
    }

    override fun sendMail(uuid: UUID, ballotTemplate: BallotTemplate, hashedToken: String) {
        observers.filter { uuid == it.getUUID() && ballotTemplate.ID == it.getBallotID() }.forEach {
            it.update(
                Ballot(
                    ballotTemplate.ID,
                    uuid,
                    ballotTemplate.election,
                    hashedToken,
                    BallotStatus.Created,
                    ballotTemplate.candidates,
                    null
                )
            )
        }
    }
}
