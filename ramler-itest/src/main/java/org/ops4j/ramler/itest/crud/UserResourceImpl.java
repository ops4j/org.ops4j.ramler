/*
 * Copyright 2019 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.ramler.itest.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.ops4j.ramler.itest.crud.api.UserResource;
import org.ops4j.ramler.itest.crud.model.User;

@ApplicationScoped
public class UserResourceImpl implements UserResource {

    private Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers(String q, String sort) {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(int id) {
        return users.get(id);
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }

    @Override
    public User putUserById(User user, int id) {
        users.put(id, user);
        return user;
    }

    @Override
    public User patchUserById(String string, int id) {
        return null;
    }
}
