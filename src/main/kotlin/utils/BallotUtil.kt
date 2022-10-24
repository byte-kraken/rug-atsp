package utils

import java.io.File
import java.util.*

/**
 * A template of a ballot that can later be personalized to a user.
 *
 * @property ID the ballot id (will match the final ballot later)
 * @property election the election the ballot is for
 * @property candidates the possible candidates that can be voted for
 */
data class BallotTemplate(
    val ID: Int,
    val election: Election,
    val candidates: List<Candidate>
)

/**
 * A full ballot used to cast a vote, specific to one user.
 *
 * @property ID the ballot id
 * @property uuid the user the ballot is for
 * @property election the election the ballot can be used in
 * @property hashedToken the hashed unique voting token of the user for this election
 * @property status the current status of the ballot
 * @property candidates the possible candidates that can be voted for
 * @property vote the actual vote cast by the voter (null initially)
 */
data class Ballot(
    val ID: Int,
    val uuid: UUID,
    val election: Election,
    val hashedToken: String,
    var status: BallotStatus = BallotStatus.Created,
    val candidates: List<Candidate>,
    var vote: Candidate?
)

/**
 * The current status of a Ballot
 */
enum class BallotStatus {
    Created, Delivered, Cast
}

/**
 * A request form for a ballot.
 *
 * @property template the template a full ballot is requested for
 */
data class AbsenteeBallotRequest(val template: BallotTemplate)

/**
 * A Candidate in an election.
 *
 * @property id the candidates unique id (e.g. the party symbol, name...)
 */
data class Candidate(val id: String)

/**
 * A scan of a ballot in the form of an image.
 *
 * @property image the scanned picture of the ballot
 */
class BallotScan(private val image: File) {
    /**
     * Creates a hash representation of the image.
     *
     * @return the image hash
     */
    fun hash(): String {
        return sha(image.toString())
    }

    companion object {
        /**
         * Creates an empty Dummy object of a scan.
         */
        fun dummy() = BallotScan(File(""))
    }
}