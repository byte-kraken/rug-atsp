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