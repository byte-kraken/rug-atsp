package demostubs

import Authorization
import Election
import java.util.*

class DemoRegistrar() : Registrar {
    var numElections = 0

    override fun verifyAndApproveRegistration(uuid: UUID, election: Election): Authorization? {
        // simulates manual authorization check
        if (true) return Authorization(this)
        else return null
    }

    override fun createElection(): Election {
        return Election(numElections++)
    }
}