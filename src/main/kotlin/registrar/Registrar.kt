package registrar

import observerPattern.MailService
import utils.Authorization
import utils.BallotTemplate
import utils.Election
import utils.Token
import java.util.*

/**
 * Contracts functionality a government official registrar needs to have.
 */
interface Registrar {
    /**
     * Checks if a user is authorized to register for an election.
     *
     * @param uuid the user
     * @param election the election
     * @return an Authorization object if the user should be authorized, otherwise null
     */
    fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization?

    /**
     * Creates an election that can be voted on.
     *
     * @return the election created
     */
    fun createElection(election: Election): Boolean

    /**
     * Creates a ballot template for all voters.
     * This template will later be personalized to each voter.
     *
     * @return the ballot template created
     */
    fun createBallotTemplate(ballotTemplate: BallotTemplate): Boolean

    /**
     * Is notified when a voter was successfully registered to the voter database.
     * On notification, can create a ballot for the mailservice to deliver to the voter.
     *
     * @param uuid the user that has been registered
     * @param election the election they have registered to
     * @param mailService the mailservice the ballot should be sent with
     */
    fun instructToSendBallot(uuid: UUID, election: Election, mailService: MailService)

    /**
     * Stores a token in its vault.
     *
     * @param uuid the user id the token should be stored to
     * @param token the token that should be stored
     * @param election the election the token should be stored to
     */
    fun storeToken(uuid: UUID, election: Election, token: Token)

    /**
     * Verifies that the given token exists for the given user.
     *
     * @param uuid the user that should have the token
     * @param hashedToken a hash of the token the user should have
     * @return true if the token exists, false otherwise
     */
    fun verifyToken(uuid: UUID, hashedToken: String): Boolean
}