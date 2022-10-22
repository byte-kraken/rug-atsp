package demostubs

import Authorization
import BallotTemplate
import Election
import java.util.*

interface Registrar {
    fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization?

    fun createElection(): Election

    fun createBallotTemplate(): BallotTemplate
    fun notify(uuid: UUID, election: Election, tokenHash: String, mailService: MailService)
}