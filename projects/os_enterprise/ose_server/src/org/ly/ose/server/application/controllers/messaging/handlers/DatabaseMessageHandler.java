package org.ly.ose.server.application.controllers.messaging.handlers;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadDatabase;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.persistence.DBHelper;
import org.ly.ose.server.application.persistence.PersistentModel;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.converters.JsonConverter;

import java.util.Collection;
import java.util.Map;

public class DatabaseMessageHandler
        extends AbstractMessageHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static String MACRO_PREFIX = "#";

    private final static String MACRO_UPSERT = "upsert";
    private final static String MACRO_FIND_ONE = "findOne";
    private final static String MACRO_FIND_ALL = "findAll";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public DatabaseMessageHandler() {

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    @Override
    protected void handleRequest(final OSERequest request,
                                 final OSEResponse response) throws Exception {
        if (request.hasPayload()) {
            final OSEPayloadDatabase payload = new OSEPayloadDatabase(request.payload());
            final String db_name = DBController.DBNameProgram(payload.database());
            final String coll_name = payload.collection();
            final String query = payload.query();
            final Map<String, ?> params = payload.params();

            final DBHelper.CollectionWrapper collection = new DBHelper.CollectionWrapper(DBHelper.instance().collection(db_name, coll_name));
            if (collection.connected()) {
                final JSONArray query_result = this.execute(collection, query, params);
                response.payload(query_result);
            } else {
                throw new Exception(FormatUtils.format("Collection '%s' in database '%s' not found.", coll_name, db_name));
            }
        } else {
            throw new Exception("Malformed request: Missing Payload");
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    final JSONArray execute(final DBHelper.CollectionWrapper collection,
                            final String query,
                            final Map<String, ?> params_or_entity) {
        if (!query.startsWith(MACRO_PREFIX)) {
            // direct command
            final Collection<PersistentModel> response = collection.find(query, params_or_entity);
            return JsonConverter.toArray(response);
        } else {
            final String macro_name = query.substring(1);

            // macro parser
            if(macro_name.equalsIgnoreCase(MACRO_UPSERT)){
                return JsonConverter.toArray(collection.upsert(params_or_entity));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_ALL)){
                return JsonConverter.toArray(collection.findEqual(params_or_entity));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_ONE)){

            }

            return null;
        }
    }

}
