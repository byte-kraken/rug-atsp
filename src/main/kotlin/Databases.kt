import utils.BallotTemplate
import utils.Election
import utils.ElectionStatus
import java.util.*

class BallotDB {
    private val ballots = mutableListOf<BallotTemplate>()
    private val elections = mutableListOf<Election>()

    fun storeElectionRecord(election: Election) = elections.add(election)
    fun getBallot(id: Int) = ballots[id]
    fun storeBallotTemplate(ballotTemplate: BallotTemplate) = ballots.add(ballotTemplate)
    fun getOpenElections(): List<Election> {
        return elections.filter { it.status == ElectionStatus.Open }.toList()
    }
}

class RegisteredVoterDB(private val registeredVoters: MutableMap<Election, MutableList<UUID>> = mutableMapOf()) {
    fun checkExists(uuid: UUID, election: Election): Boolean {
        return registeredVoters[election]?.contains(uuid) ?: false
    }

    fun addElection(election: Election) {
        registeredVoters[election] = mutableListOf()
    }

    fun register(uuid: UUID, election: Election) {
        registeredVoters[election]?.add(uuid) ?: throw IllegalArgumentException("utils.Election is not registered.")
    }

    fun getAllElectionsRegistered(uuid: UUID): List<Election> {
        return registeredVoters.filter { it.value.contains(uuid) }.keys.toList()
    }
}