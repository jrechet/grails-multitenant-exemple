package exemple

import org.grails.datastore.mapping.multitenancy.AllTenantsResolver
import org.grails.datastore.mapping.multitenancy.resolvers.SystemPropertyTenantResolver

class SimpleTenantResolver extends SystemPropertyTenantResolver implements AllTenantsResolver {

    static final List<String> tenants = ['a', 'b']

    @Override
    Iterable<Serializable> resolveTenantIds() {
        return tenants
    }
}
