class BallotDB {
    private val ballots = listOf<Ballot>()
    private val elections = mutableListOf<Election>()

    fun storeElectionRecord(election: Election) = elections.add(election)
    fun getBallot(id: Int) = ballots[id]
}