package net.net16.smartcrew;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private boolean printTimestamp;
    private boolean startWithTimestamp;
    
    /**
     *
     * @param output
     */
    public SerialHandler (JTextArea output)
    {
        this.out = output;
        this.port = null;
        this.printTimestamp = false;
        this.startWithTimestamp = false;
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
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {  
                String data = "";
                
                try
                {
                    data = new String(event.getReceivedData(), charset);
                }
                catch (UnsupportedEncodingException ex)
                {
                    Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                    data = new String(event.getReceivedData()); // Loose bad encoding
                }
                
                if(printTimestamp)
                {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");  
                    LocalDateTime now = LocalDateTime.now();  
                    
                    String date = "[" + dtf.format(now) + "] ";
                    boolean toFile = (streamOn && streamFile != null);
                    
                    if (startWithTimestamp)
                    {
                        out.append(date);
                        startWithTimestamp = false;
                    }
                    
                    for (int i = 0; i < data.length(); i++)
                    {
                        if (data.charAt(i) == '\n')
                        {
                            out.append("\n");
                            
                            if (toFile)
                            {
                                try
                                {
                                    fw.write("\n");
                                    fw.flush();
                                }
                                catch (IOException ex)
                                {
                                    Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            if (i == data.length() - 1)
                            {
                                startWithTimestamp = true;
                            }
                            else
                            {
                                if (toFile)
                                {
                                    try
                                    {
                                        fw.write(date);
                                        fw.flush();
                                    }
                                    catch (IOException ex)
                                    {
                                        Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                
                                out.append(date);
                            }
                        }
                        else
                        {
                            out.append(Character.toString(data.charAt(i)));
                        }
                    }
                }
                else
                {
                    try
                    {
                        fw.write(data);
                        fw.flush();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(SerialHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    out.append(data);
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
    
    /**
     * 
     * @param b 
     */
    public void useTimestamp(boolean b)
    {
       this.printTimestamp = b;
       if (b)
           startWithTimestamp = true;
    }
}
