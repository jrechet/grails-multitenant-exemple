package example

import exemple.Book
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@CurrentTenant
class AnotherBookService {

    @Transactional
    Book saveBook(String title) {
        new Book(title: title).save(flush: true)
    }

    @ReadOnly
    int countBooks() {
        Book.count()
    }
}
