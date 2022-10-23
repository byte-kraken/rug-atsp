package observerPattern

import utils.BallotTemplate
import java.util.*

interface MailService {
    fun sendMail(uuid: UUID, ballotTemplate: BallotTemplate, hashedToken: String)
}