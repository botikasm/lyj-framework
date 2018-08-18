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
## Query

Query language uses Arango AQL sysntax.
For further details, please visit [Arango AQL Web site](https://docs.arangodb.com/3.3/AQL/Tutorial/).

Sample Query:
```
FOR u IN users
  FILTER u.active == true && u.age < 39
  RETURN u
```

Sample Query with PARAMETERS:

```
FOR u IN users
  FILTER u.active == @active && u.age < @age
  RETURN u
```

Vaues of parameters "@active" and "@age" should be passed in request body in "params" field.


```json
{
    "app_token":"sample_token",
    "client_id":"1234",
    "lang":"it",
    "database":"my_users",
    "collection":"users",
    "query":"FOR u IN users FILTER u.active == @active && u.age < @age RETURN u",
    "params":"active=true&age=40",
}
```

Sample response:

```json
{
    "response": {
        "uid": "https://localhost:4000",
        "payload": [
            {
               "name": "John Smith",
              "age": 32
            },
            {
               "name": "James Hendrix",
               "age": 34
            },
            {
               "name": "Katie Foster",
               "age": 27
            }
        ],
        "_key": "5eea7ffb12834be7b93e1dd1541c251f",
        "lang": "it",
        "type": "database",
        "client_id": "123"
    }
}
```

Data are stored into "payload" response field.


## Macro
Macro names use a sharp # as prefix and are stored into "query" field.

"params" can be written in siple text format or json format.
	
### Introduction to usage of "params" field.

**"params"** can be written in flat text or JSON format.

Sample flat text:
```
"params":"_key=1234&name=Mario&surname=Rossi&gender=male"
```

Sample JSON format:

```json
"params" : {
	 "_key":"1234",
    	"name":"Mario",
    	"surname": "Rossi",
    	 "gender": "m"}
```

Furthermore "params" allow **"nested params"** for a more detailed query submission passing for example "skip", "limit" and more.

#### Standard "params" definition:

```json
"params" : {
	 "_key":"1234",
    	"name":"Mario",
    	"surname": "Rossi",
    	 "gender": "m"}
```
Above definition is a simple key-value pair list used from database query language engine as a simple "equals" (==) comparation.
That quite useful for most cases, but what happen when you need more complex queries?

For all this cases we can use NESTED PARAMS.

#### Nested "params" definition:
```json
"params" : {
	"query":"FOR u IN users FILTER u.age < @age RETURN u",
	 "params": {"age":25},
	 "skip":0,
	 "limit":5}
```


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

### Remove (#remove) 

Remove all items equal to:

```json
{
    "app_token":"sample_token",
    "client_id":"1234",
    "lang":"it",
    "database":"my_users",
    "collection":"users",
    "query":"#remove",
    "params":{
    	"name":"Mario",
    	"surname": "Rossi"
    	}
}
```

Remove items usig a custom AQL query with parameters:

```json
{
    "app_token":"sample_token",
    "client_id":"1234",
    "lang":"it",
    "database":"my_users",
    "collection":"users",
    "query":"#remove",
    "params":{
    	"query":"FOR u IN users FILTER u.age < @age RETURN u",
    	"params": {"age":25}
    	}
}
```



