package demostubs

interface ApplicationUser {
    fun enterUsername(): String
    fun enterPassword(): String
    fun enterSSN(): Int
    fun chooseElection(): Int
    fun enterYN(): Boolean
}