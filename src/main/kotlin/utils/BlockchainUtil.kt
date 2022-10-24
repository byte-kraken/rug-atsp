package utils

import java.sql.Timestamp
import java.time.Instant
import java.util.*

/**
 * A class representing the functionality of a blockchain.
 *
 * @property transactions the list of transactions created
 */
class Blockchain(private val transactions: MutableList<Transaction> = mutableListOf()) {

    /**
     * Creates a transaction when a voter registers for an election.
     *
     * @param uuid the voter's id
     * @param election the election they register to
     */
    fun recordVoterRegistersForElection(uuid: UUID, election: Election) {
        transactions.add(RegisterElectionTransaction(uuid, election))
    }

    /**
     * Creates a transaction when an absentee ballot is requested.
     *
     * @param uuid the user requesting the ballot
     * @param ballot the filled out ballot request form
     */
    fun recordAbsenteeBallotIsOrdered(uuid: UUID, ballot: AbsenteeBallotRequest) {
        transactions.add(AbsenteeBallotOrderTransaction(uuid, ballot))
    }

    /**
     * Creates a transaction when a registrar approves a registration to an election.
     *
     * @param uuid the user that seeked approval
     * @param election the election they were approved for
     * @param auth the authorization given
     */
    fun recordRegistrarApprovesRegistrationToElection(uuid: UUID, election: Election, auth: Authorization) {
        transactions.add(AuthorizeRegistrationTransaction(uuid, election, auth))
    }

    /**
     * Creates two transactions when a voter fills out a ballot.
     * One showing the that the user id filled out a ballot for this election.
     * And one showing that someone filled out a ballot and voted in a certain way.
     *
     * [NOTE] The patent is vulnerable to a timing attack here.
     *  Token and ID are stored at the same time, enabling matching.
     *
     * @param filledBallot the vote cast
     * @param scan a scan of the vote cast
     */
    fun recordBallotFilledByVoter(filledBallot: Ballot, scan: BallotScan) {
        transactions.apply {
            add(BallotUserTransaction(filledBallot.uuid, filledBallot.election, Timestamp.from(Instant.now())))
            add(BallotTokenTransaction(filledBallot.hashedToken, filledBallot.election, scan.hash(), scan))
        }
    }

    /**
     * Creates a transaction showing that an election was started by a registrar.
     *
     * @param election the election created
     */
    fun recordElectionCreatedByRegistrar(election: Election) {
        transactions.add(ElectionCreationTransaction(election))
    }

    /**
     * Creates a transaction showing that a ballot template was created by a registrar.
     *
     * @param ballotTemplate the template for a ballot
     */
    fun recordBallotTemplateCreatedByRegistrar(ballotTemplate: BallotTemplate) {
        transactions.add(BallotTemplateCreationTransaction(ballotTemplate))
    }

    /**
     * Fetches a copy of all transactions.
     */
    fun getTransactions(): List<Transaction> {
        return transactions.toList()
    }
}

/**
 * An abstract Transaction class. Transactions can be stored in a blockchain.
 */
abstract class Transaction

/**
 * A transaction showing that a user registered for an election.
 *
 * @property uuid the user registering
 * @property election the election registered to
 */
data class RegisterElectionTransaction(val uuid: UUID, val election: Election) : Transaction()

/**
 * A transaction showing that an absentee ballot was ordered.
 *
 * @property uuid the user ordering the ballot
 * @property ballot the ballot requested
 */
data class AbsenteeBallotOrderTransaction(val uuid: UUID, val ballot: AbsenteeBallotRequest) : Transaction()

/**
 * A transaction showing that a registration to an election was authorized.
 *
 * @property uuid the user registering
 * @property election the election they are registering to
 * @property auth the authentication given
 */
data class AuthorizeRegistrationTransaction(val uuid: UUID, val election: Election, val auth: Authorization) :
    Transaction()

/**
 * A transaction showing that a user submitted a ballot.
 *
 * @property uuid the user voting
 * @property election the election they voted in
 * @property timestamp the time of voting
 */
data class BallotUserTransaction(
    val uuid: UUID, val election: Election, val timestamp: Timestamp = Timestamp.from(Instant.now())
) : Transaction()

/**
 * A transaction showing that someone with a certain token voted in a certain way at an election.
 *
 * @property token the token unique to a user and election
 * @property election the election that was voted in
 * @property scanHash the hash of a scan of the ballot stored off-chain
 * @property scanRef the reference to a scan of the ballot stored off-chain
 * @property timestamp the time of voting
 */
data class BallotTokenTransaction(
    val token: String,
    val election: Election,
    val scanHash: String,
    val scanRef: BallotScan, // should actually be reference to BallotDatabase
    val timestamp: Timestamp = Timestamp.from(Instant.now())
) : Transaction()

/**
 * A transaction showing that an election was created.
 *
 * @property election the election created
 */
data class ElectionCreationTransaction(val election: Election) : Transaction()

/**
 * A transaction showing that a ballot template was created.
 *
 * @property ballotTemplate the ballot template created
 */
data class BallotTemplateCreationTransaction(val ballotTemplate: BallotTemplate) : Transaction()