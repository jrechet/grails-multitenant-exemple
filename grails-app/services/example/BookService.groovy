package example

import exemple.Book
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.services.Service

@Service(Book)
@CurrentTenant
interface BookService {
    Book saveBook(String title)
}
