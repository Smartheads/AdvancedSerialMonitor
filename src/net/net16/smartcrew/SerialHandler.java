package net.net16.smartcrew;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Robert Hutter
 */
public class SerialHandler {
    private final JTextArea out;
    private SerialPort port;
    private File streamFile;
    private boolean streamOn;
    private FileWriter fw;
    
    /**
     *
     * @param output
     */
    public SerialHandler (JTextArea output)
    {
        this.out = output;
        this.port = null;
    }
    
    /**
     *
     */
    public void close ()
    {
        if (port != null)
        {
            port.closePort();
        }
    }
    
    /**
     *
     * @param port
     * @param rate
     * @param charset
     * @throws SerialException
     */
    public void open (String port, int rate, String charset) throws SerialException
    {
        this.port = SerialPort.getCommPort(port);
        this.port.setBaudRate(rate);
        if (!this.port.openPort())
        {
            throw new SerialException();
        }
        
        this.port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                
                String data = "";
                try {
                    data = getIncommingData (charset);
                } catch (SerialException ex) {
                    Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                out.append(data);
                
                if (streamOn && streamFile != null)
                {
                    try {
                        fw.write (data);
                        fw.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    /**
     *
     * @param data
     * @throws SerialException
     */
    public void send (byte[] data) throws SerialException
    {
        if (this.port != null)
        {
            if (this.port.isOpen())
            {
                this.port.writeBytes(data, data.length);
            }
            else
            {
               throw new SerialException(); 
            }
        } else
        {
            throw new SerialException();
        }
    }
    
    /**
     *
     * @param data
     * @param charset
     * @throws SerialException
     */
    public void send (String data, String charset) throws SerialException
    {
        try {
            send (data.getBytes(charset));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
            send (data.getBytes());
        }
    }
    
    private String getIncommingData (String charset) throws SerialException
    {
        byte[] newData = new byte[port.bytesAvailable()];
         if (port.readBytes(newData, newData.length) == -1)
             throw new SerialException ();
        
        try
        {
            System.out.print(new String (newData, charset));
            return (new String (newData, charset));
        } catch (UnsupportedEncodingException e)
        {
            System.out.print(new String (newData));
            return (new String (newData));
        }
    }
    
    /**
     *
     * @param port
     * @param rate
     * @param charset
     * @throws Exception
     */
    public void update (String port, int rate, String charset) throws Exception
    {
        this.close();
        this.open (port, rate, charset);
    }
    
    /**
     *
     * @return
     */
    public boolean isOpen ()
    {
        if (this.port != null)
            return this.port.isOpen();
        else
            return false;
    }
    
    /**
     *
     * @return
     */
    public String portName ()
    {
        return this.port.getSystemPortName();
    }
    
    /**
     *
     * @return
     */
    public static SerialPort[] getCommPorts()
    {
        return SerialPort.getCommPorts();
    }
    
    /**
     *
     * @param file
     * @param overwrite
     * @throws IOException
     */
    public void setStreamFile (java.io.File file, boolean overwrite) throws IOException
    {
        this.streamFile = file;
        this.fw = new java.io.FileWriter(this.streamFile, !overwrite);
    }
    
    /**
     *
     * @return
     */
    public java.io.File getStreamFile ()
    {
            return this.streamFile;
    }

    /**
     *
     * @return
     */
    public boolean isStreamOn() {
        return streamOn;
    }

    /**
     *
     * @param streamOn
     */
    public void setStreamOn(boolean streamOn) {
        this.streamOn = streamOn;
    }
}
