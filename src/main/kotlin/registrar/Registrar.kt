package registrar

import observerPattern.MailService
import utils.Authorization
import utils.BallotTemplate
import utils.Election
import java.util.*

interface Registrar {
    fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization?

    fun createElection(): Election

    fun createBallotTemplate(): BallotTemplate

    fun notify(uuid: UUID, election: Election, tokenHash: String, mailService: MailService)
}