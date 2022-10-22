package demostubs

import Authorization
import BallotTemplate
import Election
import java.util.*

class DemoRegistrar : Registrar {
    private val elections = mutableListOf<Election>()
    private val ballotTemplates = mutableListOf<BallotTemplate>()

    override fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization? {
        // simulates manual authorization check
        if (true) return Authorization(this)
        else return null
    }

    override fun createElection(): Election {
        return Election(elections.size, null).also { elections.add(it) }
    }

    override fun createBallotTemplate(): BallotTemplate {
        return BallotTemplate(ballotTemplates.size, elections.last(), listOf()).also { ballotTemplates.add(it) }
    }

    override fun notify(uuid: UUID, election: Election, tokenHash: String, mailService: MailService) {
        val ballotTemplate = ballotTemplates.first { it.election == election }
        mailService.sendMail(uuid, ballotTemplate, tokenHash)
    }
}