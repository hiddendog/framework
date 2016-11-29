/*
 * Demoiselle Framework
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later.
 * See the lgpl.txt file in the root directory or <https://www.gnu.org/licenses/lgpl.html>.
 */
package org.demoiselle.jee.multitenancy.hibernate.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.demoiselle.jee.multitenancy.hibernate.configuration.MultiTenancyConfiguration;
import org.demoiselle.jee.multitenancy.hibernate.context.MultiTenantContext;
import org.demoiselle.jee.multitenancy.hibernate.dao.context.EntityManagerMaster;
import org.demoiselle.jee.multitenancy.hibernate.entity.Tenant;

@Provider
@PreMatching
public class TenantSelectorFilter implements ContainerRequestFilter {

	@Inject
	private Logger log;

	@Inject
	private MultiTenancyConfiguration configuration;

	@Inject
	private EntityManagerMaster entityManagerMaster;

	@Inject
	private MultiTenantContext multitenancyContext;

	@PostConstruct
	public void init() {
		log.info("Demoiselle Module - Multi Tenancy");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String tenantNameUrl = requestContext.getUriInfo().getPathSegments().get(0).toString();
		Tenant tenant = null;

		// Pega sempre o tenant do banco pois existem informações que podem ser
		// alteradas e precisam ser propagadas rapidamente

		// Pega os tenants do banco de dados
		Query query = entityManagerMaster.getEntityManager().createQuery("select u from Tenant u where u.name = :value",
				Tenant.class);
		query.setParameter("value", tenantNameUrl);

		// Cache da consulta
		// query.setHint("org.hibernate.cacheable", "true");
		// // Cache de 60s (60000ms)
		// query.setHint("javax.persistence.query.timeout", 60000);

		List<Tenant> list = query.getResultList();

		if (list.size() == 1) {

			tenant = list.get(0);

			// Change URI removing tenant name
			String newURi = "";
			for (int i = 1; i < requestContext.getUriInfo().getPathSegments().size(); i++) {
				newURi += requestContext.getUriInfo().getPathSegments().get(i).toString() + "/";
			}

			try {
				// Set new URI path
				requestContext.setRequestUri(new URI(newURi));
			} catch (URISyntaxException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}

			log.log(Level.FINEST, "Path changed [" + tenantNameUrl + "]: " + requestContext.getUriInfo().getPath());

		} else {

			log.log(Level.FINEST, "Go to normal path: " + requestContext.getUriInfo().getPath());

			tenant = new Tenant(configuration.getMultiTenancyMasterDatabase());
		}

		multitenancyContext.setTenant(tenant);

	}

}