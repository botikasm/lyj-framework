# Programming witn OSE (OS-Enterprise)

Programming with OSE is essentially:

- Call server endpoints to perform some task
- Write and publish custom Programs (javascript) that are exposed under endpoints

Usually you have no need to write custom programs.

## Introduction

With OSE you can write your application without be aware of server configuration or writing server methods (in languages different than javascript) to access database or perform special server tasks.

OSE Application Server expose some endpoints you can invoke to store your data or call custom javascripts programs you deployed on server.

Here is a sample GET request you can call to invoke a simple test script on OSE that returns platform version:

```
https://localhost:4000/api/program/invoke/iuhdiu87w23ruh897dfyc2w3r/it/clientid-123/system.utils/version/null
```

OSE server handle both GET and POST request and responds with a JSON string like this:

```json
{
    "response": {
        "uid": "https://localhost:4000",
        "payload": [
            "1.0.2"
        ],
        "_key": "5eea7ffb12834be7b93e1dd1541c251f",
        "lang": "it",
        "type": "program",
        "client_id": "session_12234"
    }
}
```

OSE success responses write results into *payload* field.

OSE error responses write error message into "error" field.

```json
{
    "response": {
        "uid": "https://localhost:4000",
        "_key": "97487eceb57743b0aa02b726e86f8a74",
        "lang": "it",
        "type": "error",
        "error": "Missing method or handler. You invoked 'versionq', but I didn't find a script member with this name.",
        "client_id": "session_12234"
    }
}
```
With errors, also *type* field is changed and assigned to "error".

Errors may occurr because you asked for an endpoint that no longer exists or due an internal error.