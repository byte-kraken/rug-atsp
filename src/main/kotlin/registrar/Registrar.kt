package registrar

import observerPattern.MailService
import utils.Authorization
import utils.BallotTemplate
import utils.Election
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
    fun createElection(): Election

    /**
     * Creates a ballot template for all voters.
     * This template will later be personalized to each voter.
     *
     * @return the ballot template created
     */
    fun createBallotTemplate(): BallotTemplate

    /**
     * Is notified when a voter was successfully registered to the voter database.
     * On notification, can create a ballot for the mailservice to deliver to the voter.
     *
     * @param uuid the user that has been registered
     * @param election the election they have registered to
     * @param tokenHash the voter's unique ballot token (needed to send them the ballot)
     * @param mailService the mailservice the ballot should be sent with
     */
    fun instructToSendBallot(uuid: UUID, election: Election, tokenHash: String, mailService: MailService)
}