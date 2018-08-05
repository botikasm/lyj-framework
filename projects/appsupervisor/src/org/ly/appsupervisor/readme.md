App Supervisor
============
App Supervisor is a monitoring system able to check if an application is running and stop or start if needed.
You can use this to monitor and re-launch your application based on following events:
- Application is no more responding
- Date and Time planned re-launch
- Memory usage re-launch

App Supervisor has 2 main elements:
- Launcher
- Installer

The *Launcher* monitors application execution and stop or start in result of rules evaluations.

The *Installer* allow to overwrite executables during execution and re-launch them.
That's particularly useful when you need update an executable started from launcher.

Launcher.json
------------
Launcher.json file can contain a single launcher configuration or multiple launcher configurations.

SINGLE *launcher.json* configuration file.
```json
{
  "uid":"app_01",
  "exec": "/Users/angelogeminiani/conversacon_bin/run_server_debug.sh",
  "rules": [
    {
      "enabled": true,
      "name": "restart server if low memory",
      "type": "memory",
      "mu": "mb",
      "lower_than": 100,
      "action": "default"
    },
    {
      "enabled": true,
      "name": "restart server if down",
      "type": "ping",
      "host": "",
      "timeout_sec": 60,
      "action": "default"
    },
    {
      "enabled": false,
      "name": "start server at time",
      "type": "clock",
      "mu": "time",
      "lower_than": "",
      "action": "start"
    },
    {
      "enabled": false,
      "name": "stop server at time",
      "type": "clock",
      "mu": "time",
      "greater_than": "",
      "action": "stop"
    }
  ],
  "actions": {
    "default": {
      "commands": [
        "stop",
        "start"
      ],
      "email": {
        "connection": {
          "host": "smtp.mycompany.com",
          "port": "587",
          "is_tls": true,
          "is_ssl": false,
          "username": "support@mycompany.com",
          "password": "*******"
        },
        "target":["angelo.geminiani@gmail.com"],
        "message":"SERVER RESTARTED"
      }
    },
    "start": {
      "commands": [
        "start"
      ],
      "email": {}
    },
    "stop": {
      "commands": [
        "stop"
      ],
      "email": {}
    }
  }
}
```

Multiple configurations
```json
{
  "launchers":[
    {...launcher config here...}
  ]
}
```

**Fields Explanation**
- *exec* : Path of executable to launch
- *rules*: Array of rules/conditions to evaluate
- *actions*: Array of actions to perform

**Rules**

A "rule" is a condition to evaluate for action to be performed.
Declared rules can be "enabled" or "disabled". Disabled rules are not evaluated.

Sample rule:
```json
{
      "enabled": true,
      "name": "restart server if low memory",
      "type": "memory",
      "mu": "mb",
      "lower_than": 100,
      "action": "default"
}
```


Install.json
------------

Sample install.json file with absolute target path:
```json
{
    "target":"/target/directory/to/copy/files/",
    "action-before":"stop",
    "action-after":"start"
}
```
Sample install.json file with relative target path:
```json
{
    "target":"./sub_directory/to/copy/files/",
    "action-before":"stop",
    "action-after":"start"
}
```

To create an installation package you need a zip (.zip) archive containig an "install.json" file directives and 
files you need to replace in "target" directory.

Once you have the package, simply copy it into "install" directory on your server 
(ex: "/your-appsupervisor-folder/install"). App Supervisor will detect new package to install and proceed with installation.