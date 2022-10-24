import utils.BallotScan
import utils.BallotTemplate
import utils.Election
import utils.ElectionStatus
import java.util.*

/**
 * An off-chain database for ballot templates, filled out ballots and elections.
 *
 * [NOTE] Ballot scans are also stored as hash on the blockchain (and can thus not be easily modified here).
 *   While it might therforemake sense to not store the full image on a blockchain for cost reasons,
 *   there is little reason to do the same for elections (thus creating two duplicate lists, both here and on-chain).
 */
class BallotDB {
    private val ballotScans = mutableListOf<BallotScan>()
    private val ballots = mutableListOf<BallotTemplate>()
    private val elections = mutableListOf<Election>()

    /**
     * Stores new election records created by a registrar.
     *
     * @param election the new election to store
     */
    fun storeElectionRecord(election: Election) = elections.add(election)

    /**
     * Fetches a ballot template for the given election, if one exists.
     *
     * @param election the election a ballot is needed for
     * @return the ballot template if found, null otherwise
     */
    fun getBallot(election: Election): BallotTemplate? = ballots.firstOrNull { it.election == election }

    /**
     * Stores a ballot template created by a registrar.
     *
     * @param ballotTemplate the created template
     */
    fun storeBallotTemplate(ballotTemplate: BallotTemplate) = ballots.add(ballotTemplate)

    /**
     * Stores a scan of a filled ballot created by a user.
     * A reference to a scan and its hash is also stored in the blockchain.
     *
     * @param ballotScan the scan of a filled ballot
     */
    fun storeBallotScan(ballotScan: BallotScan) = ballotScans.add(ballotScan)

    /**
     * Gets all currently open elections.
     *
     * @return a list of elections
     */
    fun getOpenElections(): List<Election> {
        return elections.filter { it.status == ElectionStatus.Open }.toList()
    }
}

/**
 * An off-chain database of all registered voters, operated by a registrar.
 * [NOTE] The existence of this database is somewhat curious, since all items are duplicated to the blockchain.
 *
 * @property registeredVoters a map of elections and the users that are registered for them
 */
class RegisteredVoterDB(private val registeredVoters: MutableMap<Election, MutableList<UUID>> = mutableMapOf()) {
    /**
     * Checks if a user is registered for an election.
     *
     * @param uuid the user
     * @param election the election
     * @return true if the user is registered, false otherwise
     */
    fun checkExists(uuid: UUID, election: Election): Boolean {
        return registeredVoters[election]?.contains(uuid) ?: false
    }

    /**
     * Adds an election to the database.
     *
     * @param election the election to add
     */
    fun addElection(election: Election) {
        registeredVoters[election] = mutableListOf()
    }

    /**
     * Allows a user to register to an election (following approval of registrar).
     *
     * @param uuid the user
     * @param election the election
     */
    fun register(uuid: UUID, election: Election) {
        registeredVoters[election]?.add(uuid) ?: throw IllegalArgumentException("Election is not registered.")
    }

    /**
     * Fetches all elections a user is registered to.
     *
     * @param uuid the user
     * @return a list of elections
     */
    fun getAllElectionsRegistered(uuid: UUID): List<Election> {
        return registeredVoters.filter { it.value.contains(uuid) }.keys.toList()
    }
}