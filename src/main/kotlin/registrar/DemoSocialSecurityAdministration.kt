package registrar

/**
 * A demo of a social security administrator not too keen on keeping their job.
 */
class DemoSocialSecurityAdministration : SocialSecurityAdministration {
    /**
     * Should check a social security number for validity, accepts all however for demo purposes.
     *
     * @param ssn the social security number to check
     * @return true if a check is successful
     */
    override fun checkSSN(ssn: Int): Boolean = true
}