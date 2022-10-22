import java.sql.Timestamp
import java.time.Instant
import java.util.*

class Blockchain(private val transactions: MutableList<Transaction> = mutableListOf()) {

    fun recordVoterRegistersForElection(uuid: UUID, election: Election) {
        transactions.add(RegisterElectionTransaction(uuid, election))
    }

    fun recordAbsenteeBallotIsOrdered(uuid: UUID, ballot: Ballot) {
        transactions.add(AbsenteeBallotOrderTransaction(uuid, ballot))
    }

    fun recordRegistrarApprovesRegistrationToElection(uuid: UUID, election: Election, auth: Authorization) {
        transactions.add(AuthorizeRegistrationTransaction(uuid, election, auth))
    }

    fun recordBallotFilledByVoter(filledBallot: Ballot, scan: BallotScan) {
        // TODO: Vulnerability: Token and ID are stored at the same time, enabling matching:
        transactions.apply {
            add(RecordBallotUserTransaction(filledBallot.uuid, filledBallot.election, Timestamp.from(Instant.now())))
            add(RecordBallotTokenTransaction(filledBallot.hashedToken, filledBallot.election, scan.hash(), scan))
        }
    }

    fun recordElectionCreatedByRegistrar(election: Election) {
        transactions.add(RecordElectionCreationTransaction(election))
    }


}


open class Transaction

class RegisterElectionTransaction(val uuid: UUID, val election: Election) : Transaction()
class AbsenteeBallotOrderTransaction(val uuid: UUID, val ballot: Ballot) : Transaction()
class AuthorizeRegistrationTransaction(val uuid: UUID, val election: Election, val auth: Authorization) : Transaction()
class RecordBallotUserTransaction(
    val uuid: UUID, val election: Election, val timestamp: Timestamp = Timestamp.from(Instant.now())
) : Transaction()

class RecordBallotTokenTransaction(
    val token: String,
    val election: Election,
    val scanHash: String,
    val scanRef: BallotScan, // TODO: should actually be reference to BallotDatabase
    val timestamp: Timestamp = Timestamp.from(Instant.now())
) : Transaction()

data class RecordElectionCreationTransaction(val election: Election) : Transaction()