# README #

lyj is a Java 8 library created to build both client side and server side applications.

### Why lyj ###

* Quick summary
* Version
* [Support](https://plus.google.com/u/0/+GianAngeloGeminiani)

#### Quick Summary ####
lyj is a light-weight java library (lyj-core) with some extensions and modules (projects).

Most important project is [DesktopGap](https://bitbucket.org/angelogeminiani/lyj/src/827bc77cf709fbfa3e0ac62d96ec57768f8c5551/projects/desktopgap/?at=master), that allow java developers to write awesome applications with an HTML gui.

lyj-ext-netty is a very light-weight web server (complete of routing) integrated in DesktopGap.

Another interesting project is [Automator](https://bitbucket.org/angelogeminiani/lyj/src/827bc77cf709fbfa3e0ac62d96ec57768f8c5551/projects/automator/?at=master), a modular application built to create web server stress tools.

For example, this simple script (json syntax) in Automator produce a web request in 3 threads every seconds and repeats 2 times, writing output in standard console.

```json
{
  "enabled": true,
  "module": {
    "type": "FixedRate",
    "unit": "millisecond",
    "delay": "1000",
    "count": "3",
    "repeat": "2",
    "next": [
      {
        "type": "WebRequest",
        "method": "GET",
        "url": "http://localhost:4000/api/debug/task/funny_gain_68j21/7b1ad7c0238c4cb36dea5875663a890a",
        "params": {},
        "next":[
          {
            "type":"SystemOut"
          }
        ]
      }
    ]
  }
}
```
#### Current Version ####
Current version is BETA 0.1.3

We are not ready for production and have a lack of documentation.

If you want contribute to libraries or documentation, you are welcome ([Write Me](https://plus.google.com/u/0/+GianAngeloGeminiani))

# TEAM #

**Gian Angelo Geminiani** (Project Administrator) -
Profile: [LinkedIn](https://www.linkedin.com/in/angelogeminiani/)

**Alexandro Scarnicchia** (Contributor) - 
Profile: [LinkedIn](https://www.linkedin.com/in/alexandro-scarnicchia-32142838/)

**Davide Brunetti** (Contributor) - 
Profile: [LinkedIn](https://www.linkedin.com/in/davide-brunetti-10487ba7/)

**Antonio Di Pinto** (Contributor) - 
Profile: [LinkedIn](https://www.linkedin.com/in/antoniodipinto/)

