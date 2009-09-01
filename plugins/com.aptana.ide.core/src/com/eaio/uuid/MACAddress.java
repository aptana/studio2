/* 
 * MACAddress.java
 * 
 * Created on 09.01.2007 by Aptana, Inc.
 * 
 * Base on:
 * 
 * UUIDGen.java
 * 
 * Created on 09.08.2003.
 *
 * eaio: UUID - an implementation of the UUID specification
 * Copyright (c) 2003-2008 Johann Burkard (jb@eaio.com) http://eaio.com.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.eaio.uuid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @author Paul Colton
 */
public class MACAddress
{
	private static String macAddress = null;

    static {

        /* We won't use this for now, required Java 1.6 to compile
        try {
            Class.forName("java.net.InterfaceAddress");
            macAddress = Class.forName("com.eaio.uuid.UUIDGen$HardwareAddressLookup").newInstance().toString();
        }
        catch (ExceptionInInitializerError err) {
            // Ignored.
        }
        catch (ClassNotFoundException ex) {
            // Ignored.
        }
        catch (LinkageError err) {
            // Ignored.
        }
        catch (IllegalAccessException ex) {
            // Ignored.
        }
        catch (InstantiationException ex) {
            // Ignored.
        }
        catch (SecurityException ex) {
            // Ignored.
        }
        */

        if (macAddress == null) 
        {
            Process p = null;
            BufferedReader in = null;

            try {
                String osname = System.getProperty("os.name", ""); //$NON-NLS-1$ //$NON-NLS-2$

                if (osname.startsWith("Windows")) { //$NON-NLS-1$
                    p = Runtime.getRuntime().exec(
                            new String[] { "ipconfig", "/all" }, null); //$NON-NLS-1$ //$NON-NLS-2$
                }
                // Solaris code must appear before the generic code 
                else if (osname.startsWith("Solaris") //$NON-NLS-1$
                        || osname.startsWith("SunOS")) { //$NON-NLS-1$
                    String hostName = getFirstLineOfCommand(new String[] {
                            "uname", "-n" }); //$NON-NLS-1$ //$NON-NLS-2$
                    if (hostName != null) {
                        p = Runtime.getRuntime().exec(
                                new String[] { "/usr/sbin/arp", hostName }, //$NON-NLS-1$
                                null);
                    }
                }
                else if (new File("/usr/sbin/lanscan").exists()) { //$NON-NLS-1$
                    p = Runtime.getRuntime().exec(
                            new String[] { "/usr/sbin/lanscan" }, null); //$NON-NLS-1$
                }
                else if (new File("/sbin/ifconfig").exists()) { //$NON-NLS-1$
                    p = Runtime.getRuntime().exec(
                            new String[] { "/sbin/ifconfig", "-a" }, null); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (p != null) {
                    in = new BufferedReader(new InputStreamReader(
                            p.getInputStream()), 128);
                    String l = null;
                    while ((l = in.readLine()) != null) {
                        macAddress = MACAddressParser.parse(l);
                        if (macAddress != null
                                && Hex.parseShort(macAddress) != 0xff) {
                            break;
                        }
                    }
                }

            }
            catch (SecurityException ex) {
                // Ignore it.
            }
            catch (IOException ex) {
                // Ignore it.
            }
            finally {
                if (p != null) {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (IOException ex) {
                            // Ignore it.
                        }
                    }
                    try {
                        p.getErrorStream().close();
                    }
                    catch (IOException ex) {
                        // Ignore it.
                    }
                    try {
                        p.getOutputStream().close();
                    }
                    catch (IOException ex) {
                        // Ignore it.
                    }
                    p.destroy();
                }
            }
        }
        
//        if(macAddress == null)
//        {
//        	try
//			{
//				macAddress = InetAddress.getLocalHost().getHostAddress() + "#" + 
//							InetAddress.getLocalHost().getHostName();
//			}
//			catch (UnknownHostException e)
//			{
//				macAddress = null;
//			}
//        }
    }
    
    /**
     * Returns the current macAddress value.  Beware, this method can return null.
     */
    public static String getMACAddress() {
        return macAddress;
    }

    /**
     * Returns the first line of the shell command.
     * 
     * @param commands the commands to run
     * @return the first line of the command
     * @throws IOException
     */
    static String getFirstLineOfCommand(String[] commands) throws IOException {

        Process p = null;
        BufferedReader reader = null;

        try {
            p = Runtime.getRuntime().exec(commands);
            reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()), 128);

            return reader.readLine();
        }
        finally {
            if (p != null) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException ex) {
                        // Ignore it.
                    }
                }
                try {
                    p.getErrorStream().close();
                }
                catch (IOException ex) {
                    // Ignore it.
                }
                try {
                    p.getOutputStream().close();
                }
                catch (IOException ex) {
                    // Ignore it.
                }
                p.destroy();
            }
        }

    }
    
    public static void main(String[] args) throws Exception
    {
    	System.err.println(MACAddress.getMACAddress());
    }
    
    /**
     * Scans MAC addresses for good ones. 
     */
    /*
    static class HardwareAddressLookup {

        public String toString() {
            String out = null;
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                if (ifs != null) {
                    while (ifs.hasMoreElements()) {
                        NetworkInterface iface = ifs.nextElement();
                        byte[] hardware = iface.getHardwareAddress();
                        if (hardware != null && hardware.length == 6
                                && hardware[1] != (byte) 0xff) {
                            out = Hex.append(new StringBuilder(36), hardware).toString();
                            break;
                        }
                    }
                }
            }
            catch (SocketException ex) {
                // Ignore it.
            }
            return out;
        }
     */

}
