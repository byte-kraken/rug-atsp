package observerPattern

import utils.Ballot
import utils.BallotStatus
import utils.BallotTemplate
import java.util.*

/**
 * Simulates an instantaneous MailService.
 *
 * @property observers the actors waiting for their mail
 */
class WormholeMailService(private val observers: MutableList<MailObserver> = mutableListOf()) : MailService {
    /**
     * Allows observers to register to this observable.
     *
     * @param mailObserver the observer registering
     */
    override fun observe(mailObserver: MailObserver) {
        observers.add(mailObserver)
    }

    /**
     * Creates a ballot from a template, sends it via mail to all observers that follow these properties:
     *
     * @param uuid the user id the mail is addressed to must match the mail
     * @param ballotTemplate the ballot the mail is concerned with must match the mail
     * @param hashedToken the secret token string generated for each ballot sent
     */
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
