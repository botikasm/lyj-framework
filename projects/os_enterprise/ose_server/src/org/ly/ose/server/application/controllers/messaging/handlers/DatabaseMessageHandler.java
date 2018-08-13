package org.ly.ose.server.application.controllers.messaging.handlers;

import org.json.JSONArray;
import org.ly.ose.commons.model.messaging.OSERequest;
import org.ly.ose.commons.model.messaging.OSEResponse;
import org.ly.ose.commons.model.messaging.payloads.OSEPayloadDatabase;
import org.ly.ose.server.application.persistence.DBController;
import org.ly.ose.server.application.persistence.DBHelper;
import org.ly.ose.server.application.persistence.PersistentModel;
import org.lyj.commons.util.ConversionUtils;
import org.lyj.commons.util.FormatUtils;
import org.lyj.commons.util.StringUtils;
import org.lyj.commons.util.converters.JsonConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseMessageHandler
        extends AbstractMessageHandler {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private final static String MACRO_PREFIX = "#";

    private final static String MACRO_UPSERT = "upsert";
    private final static String MACRO_REMOVE = "remove";

    private final static String MACRO_FIND_ONE_EQUAL = "findOneEqual";
    private final static String MACRO_FIND_ONE = "findOne";
    private final static String MACRO_FIND_EQUAL = "findEqual";
    private final static String MACRO_FIND_EQUAL_ASC = "findEqualAsc";
    private final static String MACRO_FIND_EQUAL_DESC = "findEqualDesc";
    private final static String MACRO_FIND_LIKE_OR_ASC = "findLikeOrAsc";
    private final static String MACRO_FIND_LIKE_OR_DESC = "findLikeOrDesc";

    private final static String MACRO_COUNT = "count";
    private final static String MACRO_COUNT_EQUAL = "countEqual";
    private final static String MACRO_COUNT_NOT_EQUAL = "countNotEqual";
    private final static String MACRO_COUNT_LIKE_OR = "countLikeOr";

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
                            final String query_or_macro,
                            final Map<String, ?> params_or_entity) {
        if (!query_or_macro.startsWith(MACRO_PREFIX)) {
            // direct command
            final String query = query_or_macro;
            final Collection<PersistentModel> response = collection.find(query, params_or_entity);
            return JsonConverter.toArray(response);
        } else {
            // macro name
            final String macro_name = query_or_macro.substring(1);
            // parameters
            final String query;
            final Collection<String> sort;
            final int skip;
            final int limit;
            final Map params;
            if (params_or_entity.containsKey("params")) {
                query = StringUtils.toString(params_or_entity.get("query"));
                sort = params_or_entity.containsKey("sort") ? (Collection) params_or_entity.get("sort") : new ArrayList<>();
                skip = ConversionUtils.toInteger(params_or_entity.get("skip"));
                limit = ConversionUtils.toInteger(params_or_entity.get("limit"));
                params = params_or_entity.containsKey("params") ? (Map) params_or_entity.get("params") : new HashMap();
            } else {
                query = "";
                sort = null;
                skip = 0;
                limit = 0;
                params = params_or_entity;
            }

            // macro parser
            if (macro_name.equalsIgnoreCase(MACRO_UPSERT)) {
                return JsonConverter.toArray(collection.upsert(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_REMOVE)) {
                if (StringUtils.hasText(query)) {
                    return JsonConverter.toArray(collection.remove(query, params));
                }
                return JsonConverter.toArray(collection.removeEqual(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_ONE)) {
                return JsonConverter.toArray(collection.findOne(query, params));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_ONE_EQUAL)) {
                return JsonConverter.toArray(collection.findOneEqual(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_EQUAL)) {
                return JsonConverter.toArray(collection.findEqual(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_EQUAL_ASC)) {
                if (skip > 0 || limit > 0) {
                    return JsonConverter.toArray(collection.findEqualAsc(params, sort, skip, limit));
                }
                return JsonConverter.toArray(collection.findEqualAsc(params, sort));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_EQUAL_DESC)) {
                if (skip > 0 || limit > 0) {
                    return JsonConverter.toArray(collection.findEqualDesc(params, sort, skip, limit));
                }
                return JsonConverter.toArray(collection.findEqualDesc(params, sort));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_LIKE_OR_ASC)) {
                if (skip > 0 || limit > 0) {
                    return JsonConverter.toArray(collection.findLikeOrAsc(params, sort, skip, limit));
                }
                return JsonConverter.toArray(collection.findLikeOrAsc(params, sort));
            } else if (macro_name.equalsIgnoreCase(MACRO_FIND_LIKE_OR_DESC)) {
                if (skip > 0 || limit > 0) {
                    return JsonConverter.toArray(collection.findLikeOrDesc(params, sort, skip, limit));
                }
                return JsonConverter.toArray(collection.findLikeOrDesc(params, sort));
            } else if (macro_name.equalsIgnoreCase(MACRO_COUNT)) {
                if (StringUtils.hasText(query)) {
                    return JsonConverter.toArray(collection.count(query, params));
                }
                return JsonConverter.toArray(collection.count());
            } else if (macro_name.equalsIgnoreCase(MACRO_COUNT_EQUAL)) {
                return JsonConverter.toArray(collection.countEqual(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_COUNT_NOT_EQUAL)) {
                return JsonConverter.toArray(collection.countNotEqual(params));
            } else if (macro_name.equalsIgnoreCase(MACRO_COUNT_LIKE_OR)) {
                return JsonConverter.toArray(collection.countLikeOr(params));
            }

            return null;
        }
    }

}
