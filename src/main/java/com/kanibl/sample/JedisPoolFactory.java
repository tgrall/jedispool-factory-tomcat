package com.kanibl.sample;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;

public class JedisPoolFactory implements ObjectFactory {


    @Override public Object getObjectInstance(Object obj, Name nameParam, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {


        URI redisUrl = null;
        GenericObjectPoolConfig poolConfig = new JedisPoolConfig();

        // Get factory configuration to initiate Jedis Pool
        // TODO: cleanup and add all pool properties
        //       check connections option for SSL & more
        Reference ref = (Reference) obj;
        Enumeration addrs = ref.getAll();
        while (addrs.hasMoreElements()) {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String name = addr.getType();
            String value = (String) addr.getContent();


            if (name.equals("maxTotal")) {
                poolConfig.setMaxTotal( Integer.parseInt(value) );
            }

            if (name.equals("minIdle")) {
                poolConfig.setMinIdle( Integer.parseInt(value) );
            }

            if (name.equals("maxIdle")) {
                poolConfig.setMaxIdle( Integer.parseInt(value) );
            }

            if (name.equals("maxWaitMillis")) {
                poolConfig.setMaxWaitMillis( Integer.parseInt(value) );
            }

            if (name.equals("url")) {
                redisUrl = new URI(value);
            }

        }


        System.out.println(poolConfig);

        JedisPool pool = new JedisPool(redisUrl);

        return pool;

    }
}
