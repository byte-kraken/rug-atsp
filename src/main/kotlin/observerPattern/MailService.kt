package observerPattern

import utils.BallotTemplate
import utils.Token
import java.util.*

/**
 * Implements an observable in an observer pattern. Mail carriers need to provide this functionality.
 */
interface MailService {
    /**
     * Allows observers to register to this observable.
     *
     * @param mailObserver the observer registering
     */
    fun observe(mailObserver: MailObserver)

    /**
     * Sends mail to all observers based on their properties.
     *
     * @param uuid the user id the mail is addressed to
     * @param ballotTemplate the ballot the mail is concerned with
     * @param token the secret token string generated for each ballot sent
     */
    fun sendMail(uuid: UUID, ballotTemplate: BallotTemplate, token: Token)
}