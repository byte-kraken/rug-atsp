class BlockchainAccessLayer {
    lateinit var mobileCode: String

    fun receiveInputFromMobile(mobileCode: String, ballotSelection:, electionID: String): VoteID {
        this.mobileCode = mobileCode
    }

    fun receiveInputFromEOS(ballot: Ballot, electionID: String) {
        this.mobileCode = mobileCode
    }
}

class FirstDatabase(ballotSelection: Ballot[], eSignatureBlockchainAccessLayer: BlockchainAccessLayerString)

class SecondDatabase() {
    lateinit var voteID: VoteID
    lateinit var pointerBallotSelectionFirstDatabase: Ballot
    lateinit var pointereSignatureFirstDatabase: BlockchainAccessLayerString
}

class BlockchainDatabase