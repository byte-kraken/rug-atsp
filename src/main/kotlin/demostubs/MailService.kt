package demostubs

import BallotTemplate
import java.util.*

interface MailService {
    fun sendMail(uuid: UUID, ballotTemplate: BallotTemplate, hashedToken: String)
}