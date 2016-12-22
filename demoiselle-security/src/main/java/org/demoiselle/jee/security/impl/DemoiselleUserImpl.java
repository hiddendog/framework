/*
 * Demoiselle Framework
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later.
 * See the lgpl.txt file in the root directory or <https://www.gnu.org/licenses/lgpl.html>.
 */
package org.demoiselle.jee.security.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.demoiselle.jee.core.api.security.DemoiselleUser;

;

/**
 * TODO javadoc
 *
 * @author SERPRO
 */
@RequestScoped
public class DemoiselleUserImpl implements DemoiselleUser, Cloneable {

    @Inject
    private Logger logger;

    private String identity;
    private String name;
    private List<String> roles;
    private Map<String, List<String>> permissions;
    private Map<String, List<String>> params;

    @PostConstruct
    public void init() {
        this.roles = new ArrayList<>();
        this.permissions = new ConcurrentHashMap<>();
        this.params = new ConcurrentHashMap<>();
    }

    public DemoiselleUserImpl() {
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    @Override
    public Map<String, List<String>> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    @Override
    public Map<String, List<String>> getParams() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public void addRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    @Override
    public void removeRole(String role) {
        this.roles.remove(role);
    }

    @Override
    public List<String> getPermissions(String resource) {
        return permissions.get(resource);
    }

    @Override
    public void addPermission(String resource, String operation) {
        List<String> operations = permissions.get(resource);
        if (operations != null && !operations.isEmpty()) {
            if (!permissions.get(resource).contains(operation)) {
                permissions.get(resource).add(operation);
            }
        } else {
            List<String> newoperations = new ArrayList<>();
            newoperations.add(operation);
            permissions.put(resource, newoperations);
        }
    }

    @Override
    public void removePermission(String resource, String operation) {
        List<String> operations = permissions.get(resource);
        if (operations != null && !operations.isEmpty()) {
            if (permissions.get(resource).contains(operation)) {
                permissions.get(resource).remove(operation);
            }
            if (operations.isEmpty()) {
                permissions.remove(resource);
            }
        }
    }

    @Override
    public List<String> getParams(String key) {
        return params.get(key);
    }

    @Override
    public void addParam(String key, String value) {
        List<String> params = this.params.get(key);
        if (params != null && !params.isEmpty()) {
            if (!this.params.get(key).contains(value)) {
                this.params.get(key).add(value);
            }
        } else {
            List<String> newparams = new ArrayList<>();
            newparams.add(value);
            this.params.put(key, newparams);
        }
    }

    @Override
    public void removeParam(String key, String value) {
        List<String> params = this.params.get(key);
        if (params != null && !params.isEmpty()) {
            if (this.params.get(key).contains(value)) {
                this.params.get(key).remove(value);
            }
            if (params.isEmpty()) {
                this.params.remove(key);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.identity);
        hash = 19 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DemoiselleUserImpl other = (DemoiselleUserImpl) obj;
        return Objects.equals(this.identity, other.identity);
    }

    @Override
    public String toString() {
        return "{\"identity\":\"" + identity + "\", \"name\":\"" + name + "\"}";
    }

    @Override
    public DemoiselleUser clone() {
        try {
            return (DemoiselleUser) super.clone();
        } catch (CloneNotSupportedException ex) {
            logger.severe(ex.getLocalizedMessage());
        }
        return null;
    }

}
