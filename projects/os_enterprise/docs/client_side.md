#Client Side Programming

Client SIde programming is simple as invoke a GET or POST method on OSE endpoints.

## Database Endpoint

Database endpoint URL: *./api/database/invoke*

###POST

```
https://localhost:4000/api/database/invoke
```
**Parameters**

- **app_token**: [required] Authorization token. 
- **lang**: (optional) Client language. If omitted is assigned from system. This may be useful if response is an error with a translated message text.
- **client_id**: (optional) Client unique identifier. If omitted, is assigned from system
- **database**: [required] Name of Database to connect to. If database does not exists, is auto-created.
- **collection**: [required] Name of collection into database to work on. Collections are auto-created if not exists.
- **query**: [required] Query or Macro (#upsert, #findOne, #findEqual, etc...)
- **params**: [required] Parameters to use as a filter, entity to store o query parameters

###GET

```
https://localhost:4000/api/database//invoke/:app_token/:lang/:client_id/:database/:collection/:query/:params
```

## Macro
Macro names use a sharp # as prefix and are stored into "query" field.

"params" can be written in siple text format or json format.

###Upsert (#upsert) 

Sample using flat params:

```json
{
    "app_token":"sample_token",
    "client_id":"1234",
    "lang":"it",
    "database":"my_users",
    "collection":"users",
    "query":"#upsert",
    "params":"_key=1234&name=Mario&surname=Rossi&gender=male",
}
```

Sample using JSON params:

```json
{
    "app_token":"sample_token",
    "client_id":"1234",
    "lang":"it",
    "database":"my_users",
    "collection":"users",
    "query":"#upsert",
    "params":{
    	"_key":"1234",
    	"name":"Mario",
    	"surname": "Rossi",
    	 "gender": "m"
    	}
}
```



