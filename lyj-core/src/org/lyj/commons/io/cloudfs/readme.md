#Cloud File System

The package `org.lyj.io.cloudfs` contains utility classes to manage a multi-disk storage with a specific disk  size.

Sample configuration:
```
   {
        "disks": [
            {
                "name":"./disk1",
                "size_mb":"10"
            },
            {
                "name":"./disk2",
                 "size_mb":"10"
            }      
        ]
   }
```

Configuration above declares two diskseach of 10Mb size.

##Disk Attributes
Sample:
```
   {
        "name":"./disk1",
         "size_mb":"10"
   }
```
###name
Disk name and path. Both absolute path and relative paths are supported.

###size_mb
Disk size in Mb. If size exceed the quota, main disk controller will look for a new disk.
If no more disks are allowed, main controller will throw an exception.