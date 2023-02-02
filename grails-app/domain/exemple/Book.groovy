package exemple

import grails.gorm.MultiTenant

class Book implements MultiTenant<Book> {

    String title

    static constraints = {
        title blank: false
    }
}
