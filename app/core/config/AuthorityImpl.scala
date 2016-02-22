package core.config

import auth.model.UserState

/**
 *  container for required access right
 */
case class AuthorityImpl(state: UserState = UserState.Active, isFan: Boolean = false, isArtist: Boolean = false, isAdmin: Boolean = false)