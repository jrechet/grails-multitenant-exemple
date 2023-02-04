package exemple

import example.AnotherBookService
import example.BookService
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Stepwise

import javax.persistence.TransactionRequiredException

@Integration
@Stepwise
class TransactionalAnnotationSpec extends Specification {

    AnotherBookService anotherBookService
    BookService bookService

    def "shows that no transaction is handled by the @Transactional annotation in SCHEMA multitenancy mode on a @Transactional service"() {

        when: 'we save a domain within a transaction'
        Book book = null
        Book.withTransaction {
            book = new Book(title: 'a good title').save(flush: true)
        }
        then: 'a domain is created'
        book?.id

        when: 'we save a domain with a service annotated with @CurrentTenant and @Transactional annotations'
        Book anotherBook = anotherBookService.saveBook('a book')

        then: 'it throws TransactionRequiredException'
        def e = thrown TransactionRequiredException
        e.message.contains('no transaction is in progress')

        when: 'we do the same thing with a @grails.gorm.services.Service'
        Book aBook = bookService.saveBook('a book')

        then: 'it throws TransactionRequiredException'
        e = thrown TransactionRequiredException
        e.message.contains('no transaction is in progress')

    }

    @Rollback
    def "shows that we can save a domain with a @grails.gorm.services.Service if we are already in a transactional context"() {

        when: 'we save a domain with a @grails.gorm.services.Service'
        Book aBook = bookService.saveBook('a book')

        then: 'a domain is created'
        aBook.id

        when: 'but a service annotated with @CurrentTenant and @Transactional annotations still fails'
        Book anotherBook = anotherBookService.saveBook('a book')

        then: 'it throws TransactionRequiredException'
        def e = thrown TransactionRequiredException
        e.message.contains('no transaction is in progress')

    }
}