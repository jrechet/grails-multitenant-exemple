package exemple

import grails.gorm.MultiTenant
import grails.gorm.annotation.Entity
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import grails.spring.BeanBuilder
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.core.DatastoreUtils
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException
import org.grails.datastore.mapping.multitenancy.resolvers.SystemPropertyTenantResolver
import org.grails.orm.hibernate.HibernateDatastore
import org.hibernate.dialect.H2Dialect
import spock.lang.Specification

import javax.persistence.TransactionRequiredException

class TransactionalAnnotationTest extends Specification {

    BeanBuilder bb = new BeanBuilder()

    def setup() {
        System.setProperty(SystemPropertyTenantResolver.PROPERTY_NAME, "")
        Map config = [
                "grails.gorm.multiTenancy.mode"               : "SCHEMA",
                "grails.gorm.multiTenancy.tenantResolverClass": SimpleTenantResolver,
                'dataSource.url'                              : "jdbc:h2:mem:grailsDB;LOCK_TIMEOUT=10000",
                'dataSource.dbCreate'                         : 'update',
                'dataSource.dialect'                          : H2Dialect.name,
                'dataSource.formatSql'                        : 'true',
                'hibernate.flush.mode'                        : 'COMMIT',
                'hibernate.cache.queries'                     : 'true',
                'hibernate.hbm2ddl.auto'                      : 'create',
        ]

        HibernateDatastore datastore = new HibernateDatastore(DatastoreUtils.createPropertyResolver(config), Room)
    }

    def "shows that no transaction is handled by the @Transactional annotation in SCHEMA multitenancy mode on a @Transactional service"() {
        setup:
        bb.beans {
            roomService(RoomMultiTenantService)
            roomWithoutTenantService(RoomWithoutTenantService)
        }
        def appCtx = bb.createApplicationContext()
        def roomService = appCtx.getBean(RoomMultiTenantService)
        def roomWithoutTenantService = appCtx.getBean(RoomWithoutTenantService)

        when: "no tenant id is present"
        Room.list()

        then: "An exception is thrown"
        thrown(TenantNotFoundException)

        when: "no tenant id is present"
        new Room().save()

        then: "An exception is thrown"
        thrown(TenantNotFoundException)

        when: 'a tenant id is defined and we save a domain within a transaction'
        System.setProperty(SystemPropertyTenantResolver.PROPERTY_NAME, SimpleTenantResolver.tenants.first())
        Room room = null
        Room.withTransaction {
            room = new Room().save()
        }
        then: 'a domain is created'
        room?.id

        when: 'a tenant id is defined and we save a domain with a service annotated with @CurrentTenant and @Transactional annotations'
        roomService.create()

        then: 'it throws TransactionRequiredException'
        def e = thrown TransactionRequiredException
        e.message.contains('no transaction is in progress')

        when: 'a tenant id is defined and we save a domain with a service annotated with @WithoutTenant and @Transactional annotations'
        Room aRoom = roomWithoutTenantService.create()

        then:
        aRoom.id
    }

}

@Entity
class Room implements MultiTenant<Room>, GormEntity<Room> {
    Long id
}

@CurrentTenant
@Transactional
class RoomMultiTenantService {
    Room create() {
        new Room().save(flush: true)
    }
}

@WithoutTenant
@Transactional
class RoomWithoutTenantService {
    Room create() {
        new Room().save(flush: true)
    }
}
