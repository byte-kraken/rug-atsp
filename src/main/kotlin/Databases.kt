import java.util.*

class Blockchain(private val nodes: MutableList<Node> = mutableListOf()) {
    /*get() = nodes.toMutableList()*/

    fun add(node: Node) {
        nodes.add(node)
    }


    fun register(uuid: UUID, election: Election) {
        TODO("no clue how this is supposed to be stored?")
    }

    fun approve(uuid: UUID, election: Election, auth: Authorization) {
        TODO("Not yet implemented")
    }
}

data class Node(val id: UUID, var content: Any)