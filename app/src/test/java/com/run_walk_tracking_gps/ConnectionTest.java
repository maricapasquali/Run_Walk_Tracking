package com.run_walk_tracking_gps;


import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionTest {

    @Test
    public void connection(){
        // First get InetAddress for the machine, here localhost
        InetAddress myIP = null;
        try {
            myIP = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // getHostAddress() returns IP address of a machine
        String IPAddress = myIP.getHostAddress();

        // getHostname returns DNS hostname of a machine
        String hostname = myIP.getHostName();


        System.out.printf("IP address of Localhost is %s %n", IPAddress);
        System.out.printf("Host name of your machine is %s %n", hostname);
    }
}
