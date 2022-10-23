package registrar

/**
 * A contract for a governmental social security administration capable of authenticating voters.
 */
interface SocialSecurityAdministration {
    /**
     * Checks a social security number for validity.
     *
     * @param ssn the number to check
     * @return true if the social security number is valid
     */
    fun checkSSN(ssn: Int): Boolean
}