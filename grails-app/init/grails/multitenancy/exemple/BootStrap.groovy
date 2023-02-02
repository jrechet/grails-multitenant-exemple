package grails.multitenancy.exemple

import exemple.SimpleTenantResolver
import org.grails.datastore.mapping.multitenancy.resolvers.SystemPropertyTenantResolver

class BootStrap {

    def init = { servletContext ->
        String defaultTenantId = System.getProperty(SystemPropertyTenantResolver.PROPERTY_NAME) ?: SimpleTenantResolver.tenants.first()
        System.setProperty(SystemPropertyTenantResolver.PROPERTY_NAME, defaultTenantId)
    }
    def destroy = {
    }
}
