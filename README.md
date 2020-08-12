# JedisPool Factory for Tomcat

This project shows how to create a Resource Factory for a Web Application and configure the JedisPool from there.


The implementation is based on the following documentation:

* [Adding Custom Resource Factories](http://www.ymlkhh.com.tw/docs/jndi-resources-howto.html#Adding_Custom_Resource_Factories)


In this demonstration for simplicity I put everything in the Web application, for production environment 
the resource configuration should be externalized, for this you need to put the `com.kanibl.sample.JedisPoolFactory` 
class to Tomcat itself.

## Configuration

Open the `src/main/webapp/META-INF/context.xml` file and adapt the configuration to your environment

```xml
    <Resource name="jedis/JedisPoolFactory" auth="Container"
              type="redis.clients.jedis.JedisPool"
              factory="com.kanibl.sample.JedisPoolFactory"
              singleton="true"
              url="redis://localhost:6379"
              minIdle="1"
              maxIdle="5
              maxTotal="11"
              maxWaitMillis="1000"

    />
```
For this example the choice is to use a simple URI to define the connection,
 with the format `redis://[:password@]host[:port][/db-number][?option=value]`

## Build the application

```
 mvn clean package
```

This generate a WAR file that you can drop in your Tomcat.


## Run the application

1. You are a Redis instance running
1. Download & decompress Tomcat 8 or later
1. Start tomcat `./bin/startup.sh`
1. Copy `/target/sample-web-app.war` to Tomcat `/webapps` directory to deploy the application
1. Access the application using: http://localhost:8080/hello or http://localhost:8080/goodbye 

You will see that the 2 servlets are using the same JedisPool instance.

## Configuration & Code

``src/main/webapp/META-INF/context.xml**``

This file contains the resource configuration and its JNDI name: `jedis/JedisPoolFactory`

This is used in the application using a resource reference in the web.xml

```xml
    <Resource name="jedis/JedisPoolFactory" auth="Container"
              type="redis.clients.jedis.JedisPool"
              factory="com.kanibl.sample.JedisPoolFactory"
              singleton="true"
              url="redis://localhost:6379"
              minIdle="1"
              maxIdle="5
              maxTotal="11"
              maxWaitMillis="1000"

    />
```

---
``src/main/webapp/WEB-INF/web.xml``

This define the name and type type to use.

The application will refer to the JNDI name `java:comp/env/jedis/JedisPoolFactory`.

The root context for environment is `java:comp/env`, then the name of the resource.

```xml
    <resource-env-ref>
        <description>
            Object factory for Jedis Pool.
        </description>
        <resource-env-ref-name>
            jedis/JedisPoolFactory
        </resource-env-ref-name>
        <resource-env-ref-type>
            redis.clients.jedis.JedisPool
        </resource-env-ref-type>
    </resource-env-ref>
```


---
``com.kanibl.sample.JedisPoolFactory``

This class instantiates the JedisPool.

The configuration is received from the XML configuration, each parameters are read to create:
* the PoolConfig
* the JedisPool itself

*Note: this approach will only create a new Pool and will not manage it (closing for example)*

---
```com.kanibl.sample.HelloServlet```

In the `Init` of the servlet we get a reference to the pool and use it in the various methods.

```java
        try {
            Context initCtx =  new InitialContext();
            jedisPool = (JedisPool) initCtx .lookup("java:comp/env/jedis/JedisPoolFactory");
        } catch (NamingException e) {
            e.printStackTrace();
        }
```

It is also possible to do a lookup by name using the root name, look in the `com.kanibl.sample.GoodbyeServlet` Servlet code.