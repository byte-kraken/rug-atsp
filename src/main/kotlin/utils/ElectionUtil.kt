package utils

import java.util.*

/**
 * An election that users can vote in. Can be set to only allow certain users to vote (e.g. a certain ZIP code).
 *
 * @property ID the unique id of the election
 * @property eligibleFunction determines who is allowed to vote (all if null)
 * @property status the current status of the election
 */
data class Election(
    val ID: Int,
    val name: String = "Presidential Election",
    val eligibleFunction: ((UUID) -> Boolean)?,
    var status: ElectionStatus = ElectionStatus.Open
)

/**
 * The status of an election. Voting on closed elections should be prevented.
 */
enum class ElectionStatus {
    Open, Closed
}