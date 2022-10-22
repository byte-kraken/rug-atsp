package demostubs

import Authorization
import Election
import java.util.*

interface Registrar {
    fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization?

    fun createElection(): Election
}