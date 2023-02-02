package exemple

import org.grails.datastore.gorm.jdbc.schema.DefaultSchemaHandler

class H2SchemaHandler extends DefaultSchemaHandler {

    H2SchemaHandler() {
        super("SET SCHEMA %s", "CREATE SCHEMA %s", "PUBLIC")
    }

}
