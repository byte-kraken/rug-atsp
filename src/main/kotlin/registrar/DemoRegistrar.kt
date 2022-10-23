package registrar

import observerPattern.MailService
import utils.Authorization
import utils.BallotTemplate
import utils.Election
import java.util.*

/**
 * A demo implementation of a Registrar that lacks critical analysis skill and blankly accepts all authorization request.
 */
class DemoRegistrar : Registrar {
    private val elections = mutableListOf<Election>()
    private val ballotTemplates = mutableListOf<BallotTemplate>()

    /**
     * Checks if a user is authorized to register for an election.
     * Our DemoRegistrar is not very good at their job and authorizes everybody.
     *
     * @param uuid the user
     * @param election the election
     * @return an Authorization object if the user should be authorized, otherwise null
     */
    override fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization? {
        // simulates manual authorization check
        if (true) return Authorization(this)
        else return null
    }

    /**
     * Creates and stores an election that can be voted on.
     *
     * @return the election created
     */
    override fun createElection(): Election {
        return Election(elections.size, null).also { elections.add(it) }
    }

    /**
     * Creates and stores a ballot template for all voters.
     * This template will later be personalized to each voter.
     *
     * @return the ballot template created
     */
    override fun createBallotTemplate(): BallotTemplate {
        return BallotTemplate(ballotTemplates.size, elections.last(), listOf()).also { ballotTemplates.add(it) }
    }

    /**
     * Is notified when a voter was successfully registered to the voter database.
     * On notification, creates a ballot for the mailservice to deliver to the voter.
     *
     * @param uuid the user that has been registered
     * @param election the election they have registered to
     * @param tokenHash the voter's unique ballot token (needed to send them the ballot)
     * @param mailService the mailservice the ballot should be sent with
     */
    override fun instructToSendBallot(uuid: UUID, election: Election, tokenHash: String, mailService: MailService) {
        val ballotTemplate = ballotTemplates.first { it.election == election }
        mailService.sendMail(uuid, ballotTemplate, tokenHash)
    }
}