/*
 * This file is part of the User Module library.
 * Copyright (C) 2014 Jaroslav Brtiš
 *
 * User Module library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * User Module library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with User Module library. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jardoapps.usermodule;

import java.io.Serializable;

/**
 * This interface is used by {@link UserManager} to store data (like the
 * currently logged in user) in a server-side session.
 * 
 * @author Jaroslav Brtiš
 */
public interface SessionModel extends Serializable {

	/**
	 * Returns user who is currently logged in.
	 * 
	 * @return user who is currently logged in or null, if no user is logged in
	 * @see UserManager#logIn(String, String, String)
	 * @see UserManager#logOut()
	 */
	User getCurrentUser();

	/**
	 * Sets the user is currently logged in. If the user parameter is null, that
	 * means that no user is currently logged in.
	 * 
	 * @param user
	 *            user who is currently logged in, can be null
	 * @see UserManager#logIn(String, String, String)
	 * @see UserManager#logOut()
	 */
	void setCurrentUser(User user);

}
