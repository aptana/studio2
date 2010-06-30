/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 *
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 *
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.logging.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Kevin Lindsey
 */
public class ReaderWriterTest
{
    private final static String[] LINES = new String[] {
        "INFO line",
        "WARNING line",
        "ERROR line",
        "This line has INFO before WARNING",
        "This line has INFO before ERROR",
        "This line has WARNING before INFO",
        "This line has WARNING before ERROR",
        "This line has ERROR before INFO",
        "This line has ERROR before WARNING",
        "This line ends with INFO",
        "This line ends with WARNING",
        "This line ends with ERROR"
    };
    private static final int READ_DELAY = 1000;
    private static final int WRITE_DELAY = 125;
    private static final int RUN_TIME = 10 * 1000;
    private static final int MAX_LINES = 10;
    
    volatile boolean running = true;
    
    private String[] lines = new String[MAX_LINES];
    
    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        ReaderWriterTest rw = new ReaderWriterTest();
        
        rw.run();
    }

    /**
     * ReaderWriterTest
     */
    public ReaderWriterTest()
    {
    }
    
    /**
     * run
     */
    public void run()
    {
        try
        {
            // create file
            final File file = File.createTempFile("read-write-test", "txt");
            file.deleteOnExit();
            
            // create threads
            Thread readerThread = new Thread(createReader(file));
            Thread writerThread = new Thread(createWriter(file));
            
            // start threads
            writerThread.start();
            readerThread.start();
            
            // run for a while
            try
            {
                Thread.sleep(RUN_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            
            // tell threads they can stop
            running = false;
            
            // wait until writer thread stops
            try
            {
                writerThread.join(1*1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            
            // wait until reader thread stops
            try
            {
                readerThread.join(1*1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            
            // say bye
            System.out.println("Test complete");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * createReader
     * 
     * @param file
     * @return
     */
    private Runnable createReader(final File file)
    {
        return new Runnable() {
            public void run()
            {
                FileInputStream inputStream = null;
                
                try
                {
                    inputStream = new FileInputStream(file);
                    
                    FileChannel fileChannel = inputStream.getChannel();
                    
                    while (running)
                    {
                        long position = fileChannel.position();
                        long size = fileChannel.size();
                        
                        if (position < size)
                        {
                            long update = size - position;
                            
                            int length = (int) update;
                            int offset = 0;
                            int read;
                            byte[] data = new byte[length];
                            
                            while ((read = inputStream.read(data, offset, length)) > 0)
                            {
                                offset += read;
                                length -= read;
                            }
                            
                            String text = new String(data);
                            
                            System.out.print("< " + text);
                            
                            fileChannel.position(size);
                        }
                        else
                        {
                            System.out.println("< ...");
                        }
                        
                        Thread.sleep(READ_DELAY);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (inputStream != null)
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException e)
                        {
                        }
                    }
                }
            }
        };
    }
    
    /**
     * createWriter
     * 
     * @param file
     * @return
     */
    private Runnable createWriter(final File file)
    {
        return new Runnable() {
            public void run()
            {
                FileOutputStream outputStream = null;
                
                try
                {
                    outputStream = new FileOutputStream(file);
                    
                    while (running)
                    {
                        int index = (int) Math.floor(Math.random() * LINES.length);
                        String line = LINES[index] + "\n";
                        
                        outputStream.write(line.getBytes());
                        System.out.print("> " + line);
                        
                        Thread.sleep(WRITE_DELAY);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (outputStream != null)
                    {
                        try
                        {
                            outputStream.close();
                        }
                        catch (IOException e)
                        {
                        }
                    }
                }
            }
        };
    }
}
