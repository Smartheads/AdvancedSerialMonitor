/*
 * Copyright (C) 2020 Robert Hutter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.net16.smartcrew;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Robert Hutter
 */
public final class AdvancedSerialMonitor extends JFrame implements SerialPortDataListener
{
    private SerialPort port;
    private final DefaultCaret caret;
    private java.io.File sendFile;
    private java.io.File exportFile;
    private java.io.File streamFile;
    private java.io.FileWriter streamFileWriter;
    private String decodeCharset;
    private String encodeCharset;
    private boolean startWithTimestamp;
    
    private final ImageIcon onIcon;
    private final ImageIcon offIcon;
    
    private GraphPlotter gp;
    
    private byte[] incommingBuffer;
    
    {
        startWithTimestamp = false;
        port = null;
        onIcon = new ImageIcon(getClass().getResource("/green_dot.png"));
        offIcon = new ImageIcon(getClass().getResource("/red_dot.png"));
        incommingBuffer = new byte[0];
    }

    /**
     * Creates new form AdvancedSerialMonitor
     */
    public AdvancedSerialMonitor() {
        initComponents();
        
        // Initialize variables
        caret = (DefaultCaret)console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        onOffLabel.setIcon(offIcon);
        encodeCharset = (String) charsetSelector.getSelectedItem();
        decodeCharset = (String) decodeCharsetSelector.getSelectedItem();
        numberLengthLabel.setVisible(false);
        numberLengthComboBox.setVisible(false);
        numberSignednessLabel.setVisible(false);
        numberSignednessComboBox.setVisible(false);
        
        Action clearConsoleAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                console.setText("");
            }
        };
        
        clearConsole.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK),
                "clear"
        );
        clearConsole.getActionMap().put("clear", clearConsoleAction);
        
        Action startStopAction = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AdvancedSerialMonitor.this.startStopActionPerformed(new ActionEvent(startStop, ActionEvent.ACTION_FIRST, ""));
            }
        };
        
        console.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK),
                "startStop"
        );
        console.getActionMap().put("startStop", startStopAction);
    }
    
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event)
    {  
        String data;
        
        // Update graphplotter
        if (gp != null)
        { 
            gp.SerialEvent(event);
        }
            
        // If decodeCharset equals null, don't decode numbers
        if (decodeCharset == null)
        {
            byte[] buff = event.getReceivedData();
            StringBuilder sb = new StringBuilder();
            
            switch(numberLengthComboBox.getSelectedIndex())
            {
                case 0: // 1 byte
                    for (byte b : buff)
                    {
                        if (numberSignednessComboBox.getSelectedIndex() == 0)
                        {
                            // Signed
                            sb.append((int)b);
                        }
                        else
                        {
                            // Unsigned
                            if ((int) b >= 0)
                            {
                                sb.append((int)b);
                            }
                            else
                            {
                                int x = b;
                                sb.append(256+x);
                            }
                        }
                        sb.append(" | ");
                    }
                break;
                
                case 1: // 2 byte
                    byte[] cbuff = new byte[buff.length + incommingBuffer.length];
                    
                    // Populate new buffer
                    int e = 0;
                    for (byte b : incommingBuffer)
                    {
                        cbuff[e] = b;
                        e++;
                    }
                    
                    for (byte b : buff)
                    {
                        cbuff[e] = b;
                        e++;
                    }
                    
                    // Check length
                    int count;
                    if (cbuff.length % 2 == 0)
                    {
                        count = cbuff.length / 2;
                        incommingBuffer = new byte[0];
                    }
                    else
                    {
                        count = (cbuff.length-1) / 2;
                        incommingBuffer = new byte[1];
                        incommingBuffer[0] = cbuff[cbuff.length-1];
                    }
                    
                    for (int i = 0; i < count; i += 2)
                    {
                        if (numberSignednessComboBox.getSelectedIndex() == 0)
                        {
                            // Signed
                            short x = (short) ((cbuff[i] << 8) | (cbuff[i+1]));
                            sb.append(x);
                        }
                        else
                        {
                            // Unsigned
                            int x = ((cbuff[0] << 8) & 0x0000ff00) | (cbuff[1] & 0x000000ff);
                            sb.append(x);
                        }
                        sb.append(" | ");
                    }
                break;
            }
            
            data = sb.toString();
        }
        else
        {
            try
            {
                data = new String(event.getReceivedData(), decodeCharset);
            }
            catch (UnsupportedEncodingException ex)
            {
                data = new String(event.getReceivedData()); // Loose bad encoding
            }
        }

        if(timestampCheckBox.isSelected())
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();  

            String date = "[" + dtf.format(now) + "] ";
            boolean toFile = (toggleStream.isSelected() && streamFile != null);

            if (startWithTimestamp)
            {
                console.append(date);
                startWithTimestamp = false;
            }

            for (int i = 0; i < data.length(); i++)
            {
                if (data.charAt(i) == '\n')
                {
                    console.append("\n");

                    if (toFile)
                    {
                        try
                        {
                            streamFileWriter.write("\n");
                            streamFileWriter.flush();
                        }
                        catch (IOException ex)
                        {
                            
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
                                streamFileWriter.write(date);
                                streamFileWriter.flush();
                            }
                            catch (IOException ex)
                            {
                                
                            }
                        }

                        console.append(date);
                    }
                }
                else
                {
                    console.append(Character.toString(data.charAt(i)));
                }
            }
        }
        else
        {
            if (toggleStream.isSelected() && streamFile != null)
            {
                try
                {
                    streamFileWriter.write(data);
                    streamFileWriter.flush();
                }
                catch (IOException ex)
                {
                    
                }
            }

            console.append(data);
        }
    }
    
    /**
     * Method to be called when GraphPlotter is closed.
     * Prepare GraphPlotter for garbage collection. 
     */
    public void graphPlotterClosed()
    {
        this.gp = null;
    }
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileChooser = new javax.swing.JFileChooser();
        about = new javax.swing.JFrame();
        jLabel10 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        consolePanel = new javax.swing.JPanel();
        consoleScrollPane = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();
        controlPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        preferancesPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        portComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        log = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        startStop = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        clearLog = new javax.swing.JButton();
        baudRateSelector = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        decodeCharsetSelector = new javax.swing.JComboBox<>();
        onOffLabel = new javax.swing.JLabel();
        sendDataPanel = new javax.swing.JPanel();
        sendDataControlPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        charsetSelector = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        chooseSendFile = new javax.swing.JButton();
        jlSendFile = new javax.swing.JLabel();
        sendFileButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        endlComboBox = new javax.swing.JComboBox<>();
        jlSendError = new javax.swing.JLabel();
        sendDataSendPanel = new javax.swing.JPanel();
        sendData = new javax.swing.JButton();
        prompt = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        exportConsolePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        chooseExportFile = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cpToClipboard = new javax.swing.JButton();
        exportFileJl = new javax.swing.JLabel();
        exportToFile = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        errorExportFileJl = new javax.swing.JLabel();
        append = new javax.swing.JCheckBox();
        StreamConsolePanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        chooseStreamFile = new javax.swing.JButton();
        toggleStream = new javax.swing.JToggleButton();
        streamOverwrite = new javax.swing.JCheckBox();
        streamFileJl = new javax.swing.JLabel();
        jlStreamError = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        selectFontSize = new javax.swing.JComboBox<>();
        clearConsole = new javax.swing.JButton();
        autoscroll = new javax.swing.JCheckBox();
        wordWrap = new javax.swing.JCheckBox();
        timestampCheckBox = new javax.swing.JCheckBox();
        numberLengthComboBox = new javax.swing.JComboBox<>();
        numberLengthLabel = new javax.swing.JLabel();
        numberSignednessLabel = new javax.swing.JLabel();
        numberSignednessComboBox = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        alwaysOnTopCheckBox = new javax.swing.JCheckBoxMenuItem();
        shrinkMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        about.setTitle("About Advanced Serial Monitor");
        about.setAlwaysOnTop(true);
        about.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/info.png")).getImage());
        about.setResizable(false);
        about.setSize(new java.awt.Dimension(439, 300));
        about.setType(java.awt.Window.Type.POPUP);

        jLabel10.setText("<html><h2>Advanced Serial Monitor</h2></html");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imac.png"))); // NOI18N

        jLabel12.setText("<html><p>Robert Hutter<br />Version 1302<br />No rights reserved.</p></html>");

        jLabel13.setText("<html><div>Icons made by <a href=\"https://www.flaticon.com/authors/roundicons\" title=\"Roundicons\">Roundicons</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a> is licensed by <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\" target=\"_blank\">CC 3.0 BY</a></div><br /><div>Icons made by <a href=\"https://www.flaticon.com/authors/smashicons\" title=\"Smashicons\">Smashicons</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a> is licensed by <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\" target=\"_blank\">CC 3.0 BY</a></div></html>");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout aboutLayout = new javax.swing.GroupLayout(about.getContentPane());
        about.getContentPane().setLayout(aboutLayout);
        aboutLayout.setHorizontalGroup(
            aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutLayout.createSequentialGroup()
                .addGroup(aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(aboutLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator4))
                    .addGroup(aboutLayout.createSequentialGroup()
                        .addGroup(aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(aboutLayout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(aboutLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(aboutLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 30, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aboutLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        aboutLayout.setVerticalGroup(
            aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Advanced Serial Monitor");
        setBounds(new java.awt.Rectangle(100, 100, 1000, 850));
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/imac.png")).getImage());
        setMinimumSize(new java.awt.Dimension(950, 500));
        setSize(new java.awt.Dimension(1105, 625));

        mainPanel.setLayout(new java.awt.GridBagLayout());

        consolePanel.setLayout(new java.awt.BorderLayout());

        consoleScrollPane.setPreferredSize(new java.awt.Dimension(300, 366));

        console.setEditable(false);
        console.setColumns(20);
        console.setForeground(new java.awt.Color(109, 109, 109));
        console.setRows(21);
        console.setMaximumSize(null);
        console.setMinimumSize(null);
        console.setPreferredSize(null);
        consoleScrollPane.setViewportView(console);

        consolePanel.add(consoleScrollPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mainPanel.add(consolePanel, gridBagConstraints);

        controlPanel.setMinimumSize(new java.awt.Dimension(850, 175));
        controlPanel.setPreferredSize(new java.awt.Dimension(625, 175));
        controlPanel.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(850, 128));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(850, 128));

        preferancesPanel.setMinimumSize(new java.awt.Dimension(831, 101));

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD, jLabel2.getFont().getSize()+2));
        jLabel2.setText("Port");

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD, jLabel3.getFont().getSize()+2));
        jLabel3.setText("Baud rate");

        portComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        portComboBox.setToolTipText("Select a serial port to communicate on.");
        portComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                portComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        portComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portComboBoxActionPerformed(evt);
            }
        });

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD, jLabel7.getFont().getSize()+2));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Start/stop");

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setHorizontalScrollBar(null);
        jScrollPane2.setMaximumSize(null);
        jScrollPane2.setName(""); // NOI18N

        log.setEditable(false);
        log.setColumns(20);
        log.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        log.setRows(5);
        jScrollPane2.setViewportView(log);

        jLabel8.setFont(jLabel8.getFont().deriveFont(jLabel8.getFont().getStyle() | java.awt.Font.BOLD, jLabel8.getFont().getSize()+2));
        jLabel8.setText("Log");
        jLabel8.setToolTipText("");

        startStop.setFont(startStop.getFont().deriveFont(startStop.getFont().getStyle() | java.awt.Font.BOLD));
        startStop.setText("Start");
        startStop.setToolTipText("Use start/stop if you want to free up the current port for other usage. (CTRL+P)");
        startStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        clearLog.setText("Clear");
        clearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLogActionPerformed(evt);
            }
        });

        baudRateSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "110", "300", "600", "1200", "2400", "4800", "9600", "14400", "19200", "38400", "57600", "115200", "128000", "256000" }));
        baudRateSelector.setSelectedIndex(6);
        baudRateSelector.setToolTipText("Choose the transmision speed to communicate on. [bit/s]");
        baudRateSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baudRateSelectorActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel9.setFont(jLabel9.getFont().deriveFont(jLabel9.getFont().getStyle() | java.awt.Font.BOLD, jLabel9.getFont().getSize()+2));
        jLabel9.setText("Decode charset");

        decodeCharsetSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "US-ASCII", "ISO-8859-1 ", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16", "Don't decode" }));
        decodeCharsetSelector.setToolTipText("Select the charset used to decode recived data.");
        decodeCharsetSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decodeCharsetSelectorActionPerformed(evt);
            }
        });

        onOffLabel.setText("X");
        onOffLabel.setMaximumSize(new java.awt.Dimension(16, 16));
        onOffLabel.setMinimumSize(new java.awt.Dimension(16, 16));
        onOffLabel.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout preferancesPanelLayout = new javax.swing.GroupLayout(preferancesPanel);
        preferancesPanel.setLayout(preferancesPanelLayout);
        preferancesPanelLayout.setHorizontalGroup(
            preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferancesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(portComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baudRateSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(onOffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(startStop)
                        .addGap(49, 49, 49))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, preferancesPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(clearLog)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel9))
                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(decodeCharsetSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        preferancesPanelLayout.setVerticalGroup(
            preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferancesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                        .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(preferancesPanelLayout.createSequentialGroup()
                                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(decodeCharsetSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(portComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(baudRateSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(preferancesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startStop, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(onOffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, preferancesPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(clearLog)
                    .addGroup(preferancesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19))
        );

        jTabbedPane1.addTab("Preferences", preferancesPanel);

        sendDataPanel.setLayout(new java.awt.GridLayout(2, 1));

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 18);
        flowLayout1.setAlignOnBaseline(true);
        sendDataControlPanel.setLayout(flowLayout1);

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getStyle() | java.awt.Font.BOLD, jLabel5.getFont().getSize()+2));
        jLabel5.setText("Charset");
        sendDataControlPanel.add(jLabel5);

        charsetSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "US-ASCII", "ISO-8859-1 ", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16", "Don't encode" }));
        charsetSelector.setToolTipText("Select the charset used to encoded the data that will be transmitted.");
        charsetSelector.setMinimumSize(new java.awt.Dimension(81, 21));
        charsetSelector.setPreferredSize(new java.awt.Dimension(90, 21));
        charsetSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charsetSelectorActionPerformed(evt);
            }
        });
        sendDataControlPanel.add(charsetSelector);

        jLabel18.setFont(jLabel18.getFont().deriveFont(jLabel18.getFont().getStyle() | java.awt.Font.BOLD, jLabel18.getFont().getSize()+2));
        jLabel18.setText("Send contents of file");
        sendDataControlPanel.add(jLabel18);

        chooseSendFile.setText("Choose file");
        chooseSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseSendFileActionPerformed(evt);
            }
        });
        sendDataControlPanel.add(chooseSendFile);
        sendDataControlPanel.add(jlSendFile);

        sendFileButton.setText("Send");
        sendFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileButtonActionPerformed(evt);
            }
        });
        sendDataControlPanel.add(sendFileButton);

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | java.awt.Font.BOLD, jLabel6.getFont().getSize()+2));
        jLabel6.setText("End message with");
        sendDataControlPanel.add(jLabel6);

        endlComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nothing", "Newline '\\n'", "Carrage return '\\r'", "'\\0'" }));
        endlComboBox.setMinimumSize(new java.awt.Dimension(81, 21));
        endlComboBox.setPreferredSize(new java.awt.Dimension(120, 21));
        sendDataControlPanel.add(endlComboBox);

        jlSendError.setForeground(new java.awt.Color(255, 0, 0));
        sendDataControlPanel.add(jlSendError);

        sendDataPanel.add(sendDataControlPanel);

        sendData.setFont(sendData.getFont().deriveFont(sendData.getFont().getStyle() | java.awt.Font.BOLD));
        sendData.setText("Send");
        sendData.setToolTipText("");
        sendData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendDataActionPerformed(evt);
            }
        });

        prompt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        prompt.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        prompt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptActionPerformed(evt);
            }
        });

        jLabel19.setFont(jLabel19.getFont().deriveFont(jLabel19.getFont().getStyle() | java.awt.Font.BOLD, jLabel19.getFont().getSize()+2));
        jLabel19.setText("Send");

        javax.swing.GroupLayout sendDataSendPanelLayout = new javax.swing.GroupLayout(sendDataSendPanel);
        sendDataSendPanel.setLayout(sendDataSendPanelLayout);
        sendDataSendPanelLayout.setHorizontalGroup(
            sendDataSendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sendDataSendPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prompt, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendData)
                .addContainerGap())
        );
        sendDataSendPanelLayout.setVerticalGroup(
            sendDataSendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sendDataSendPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sendDataSendPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prompt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendData, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sendDataPanel.add(sendDataSendPanel);

        jTabbedPane1.addTab("Send Data", sendDataPanel);

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getStyle() | java.awt.Font.BOLD, jLabel14.getFont().getSize()+2));
        jLabel14.setText("Export contents of console");

        chooseExportFile.setText("Choose file");
        chooseExportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseExportFileActionPerformed(evt);
            }
        });

        jLabel15.setFont(jLabel15.getFont().deriveFont(jLabel15.getFont().getStyle() | java.awt.Font.BOLD, jLabel15.getFont().getSize()+2));
        jLabel15.setText("To file");

        cpToClipboard.setText("Copy to clipboard");
        cpToClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpToClipboardActionPerformed(evt);
            }
        });

        exportToFile.setText("Export");
        exportToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToFileActionPerformed(evt);
            }
        });

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        errorExportFileJl.setForeground(new java.awt.Color(255, 0, 0));

        append.setText("Overwrite");
        append.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout exportConsolePanelLayout = new javax.swing.GroupLayout(exportConsolePanel);
        exportConsolePanel.setLayout(exportConsolePanelLayout);
        exportConsolePanelLayout.setHorizontalGroup(
            exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportConsolePanelLayout.createSequentialGroup()
                .addGroup(exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(exportConsolePanelLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(exportConsolePanelLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chooseExportFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exportToFile)
                                .addGap(6, 6, 6)
                                .addComponent(append))
                            .addComponent(errorExportFileJl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportFileJl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cpToClipboard))
                    .addGroup(exportConsolePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14)))
                .addContainerGap(511, Short.MAX_VALUE))
        );
        exportConsolePanelLayout.setVerticalGroup(
            exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exportConsolePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(exportConsolePanelLayout.createSequentialGroup()
                        .addGroup(exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(exportConsolePanelLayout.createSequentialGroup()
                                .addGroup(exportConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(chooseExportFile)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(exportFileJl)
                                    .addComponent(exportToFile)
                                    .addComponent(append, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(errorExportFileJl, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator5))
                        .addGap(30, 30, 30))
                    .addGroup(exportConsolePanelLayout.createSequentialGroup()
                        .addComponent(cpToClipboard)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Export Console", exportConsolePanel);

        jLabel16.setFont(jLabel16.getFont().deriveFont(jLabel16.getFont().getStyle() | java.awt.Font.BOLD, jLabel16.getFont().getSize()+2));
        jLabel16.setText("Stream console");

        jLabel17.setFont(jLabel17.getFont().deriveFont(jLabel17.getFont().getStyle() | java.awt.Font.BOLD, jLabel17.getFont().getSize()+2));
        jLabel17.setText("To file");

        chooseStreamFile.setText("Choose file");
        chooseStreamFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseStreamFileActionPerformed(evt);
            }
        });

        toggleStream.setText("Steam");
        toggleStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleStreamActionPerformed(evt);
            }
        });

        streamOverwrite.setSelected(true);
        streamOverwrite.setText("Overwrite");
        streamOverwrite.setToolTipText("Overwrite flie when stream opens.");
        streamOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                streamOverwriteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout StreamConsolePanelLayout = new javax.swing.GroupLayout(StreamConsolePanel);
        StreamConsolePanel.setLayout(StreamConsolePanelLayout);
        StreamConsolePanelLayout.setHorizontalGroup(
            StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StreamConsolePanelLayout.createSequentialGroup()
                .addGroup(StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jlStreamError, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(StreamConsolePanelLayout.createSequentialGroup()
                        .addGroup(StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(StreamConsolePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel16))
                            .addGroup(StreamConsolePanelLayout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chooseStreamFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toggleStream)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(streamOverwrite)))
                        .addGap(8, 8, 8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(streamFileJl, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap(515, Short.MAX_VALUE))
        );
        StreamConsolePanelLayout.setVerticalGroup(
            StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StreamConsolePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(streamFileJl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(StreamConsolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chooseStreamFile)
                        .addComponent(toggleStream)
                        .addComponent(streamOverwrite)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jlStreamError)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Stream Console", StreamConsolePanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        controlPanel.add(jTabbedPane1, gridBagConstraints);

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD, jLabel4.getFont().getSize()+8));
        jLabel4.setText("Advanced Serial Monitor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        controlPanel.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 10);
        mainPanel.add(controlPanel, gridBagConstraints);

        jLabel1.setText("Font size");

        selectFontSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32" }));
        selectFontSize.setSelectedItem("13");
        selectFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFontSizeActionPerformed(evt);
            }
        });

        clearConsole.setText("Clear");
        clearConsole.setToolTipText("Clears the console. (CTRL+L)");
        clearConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearConsoleActionPerformed(evt);
            }
        });

        autoscroll.setSelected(true);
        autoscroll.setText("Autoscroll");
        autoscroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoscrollActionPerformed(evt);
            }
        });

        wordWrap.setText("Linewrap");
        wordWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordWrapActionPerformed(evt);
            }
        });

        timestampCheckBox.setText("Timestamp");
        timestampCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timestampCheckBoxActionPerformed(evt);
            }
        });

        numberLengthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 byte", "2 byte" }));

        numberLengthLabel.setText("Number size");

        numberSignednessLabel.setText("Signedness");

        numberSignednessComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "signed", "unsigned" }));

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(selectFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(autoscroll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wordWrap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(timestampCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numberLengthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numberLengthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numberSignednessLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numberSignednessComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 323, Short.MAX_VALUE)
                .addComponent(clearConsole))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1))
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoscroll)
                    .addComponent(wordWrap)
                    .addComponent(timestampCheckBox)
                    .addComponent(numberLengthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberLengthLabel)
                    .addComponent(numberSignednessLabel)
                    .addComponent(numberSignednessComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(clearConsole))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 10, 10);
        mainPanel.add(optionsPanel, gridBagConstraints);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info.png"))); // NOI18N
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(aboutMenuItem);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exit.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Window");

        alwaysOnTopCheckBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        alwaysOnTopCheckBox.setText("Always on top");
        alwaysOnTopCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysOnTopCheckBoxActionPerformed(evt);
            }
        });
        jMenu2.add(alwaysOnTopCheckBox);

        shrinkMenuItem.setText("Shrink");
        shrinkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shrinkMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(shrinkMenuItem);

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/line_graph.png"))); // NOI18N
        jMenuItem1.setText("Open Graph Plotter");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFontSizeActionPerformed
        // TODO add your handling code here:
        console.setFont(new Font("Monospaced", Font.PLAIN,Integer.parseInt((String)selectFontSize.getSelectedItem())));
    }//GEN-LAST:event_selectFontSizeActionPerformed

    private void clearConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearConsoleActionPerformed
        // TODO add your handling code here:
        console.setText("");
    }//GEN-LAST:event_clearConsoleActionPerformed

    private void sendDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendDataActionPerformed
        // TODO add your handling code here:
        sendPrompt();
    }//GEN-LAST:event_sendDataActionPerformed

    private void clearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogActionPerformed
        // TODO add your handling code here:
        log.setText("");
    }//GEN-LAST:event_clearLogActionPerformed

    private void wordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordWrapActionPerformed
        // TODO add your handling code here:
        console.setLineWrap(wordWrap.isSelected());
    }//GEN-LAST:event_wordWrapActionPerformed

    private void autoscrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoscrollActionPerformed
        // TODO add your handling code here
        if (autoscroll.isSelected())
        {
            console.setCaretPosition(console.getDocument().getLength());
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        else
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }//GEN-LAST:event_autoscrollActionPerformed

    private void alwaysOnTopCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysOnTopCheckBoxActionPerformed
        // TODO add your handling code here:
        setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
    }//GEN-LAST:event_alwaysOnTopCheckBoxActionPerformed

    private void startStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopActionPerformed
        // TODO add your handling code here:
        if(port == null)
        {
            startComm();
        }
        else
        {
            if (port.isOpen())
            {
                // shut down serial comms
                stopComm();
                log.append ("Port closed.\n");
            }
            else
            {
                startComm();
            }
        }
    }//GEN-LAST:event_startStopActionPerformed

    private void portComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_portComboBoxPopupMenuWillBecomeVisible
        // TODO add your handling code here:
        log.append("Scanning ports...\n");
        refreshPortList();
        
    }//GEN-LAST:event_portComboBoxPopupMenuWillBecomeVisible

    private void portComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portComboBoxActionPerformed
        // TODO add your handling code here:
        updateComm();
    }//GEN-LAST:event_portComboBoxActionPerformed

    private void charsetSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charsetSelectorActionPerformed
        // TODO add your handling code here:
        encodeCharset = (String) charsetSelector.getSelectedItem();
    }//GEN-LAST:event_charsetSelectorActionPerformed

    private void baudRateSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baudRateSelectorActionPerformed
        // TODO add your handling code here:
        updateComm();
    }//GEN-LAST:event_baudRateSelectorActionPerformed

    private void decodeCharsetSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decodeCharsetSelectorActionPerformed
        // TODO add your handling code here:
        if (((String) decodeCharsetSelector.getSelectedItem()).equals("Don't decode"))
        {
            decodeCharset = null;
            numberLengthLabel.setVisible(true);
            numberLengthComboBox.setVisible(true);
            numberSignednessLabel.setVisible(true);
            numberSignednessComboBox.setVisible(true);
        }
        else
        {
            decodeCharset = (String) decodeCharsetSelector.getSelectedItem();
            numberLengthLabel.setVisible(false);
            numberLengthComboBox.setVisible(false);
            numberSignednessLabel.setVisible(false);
            numberSignednessComboBox.setVisible(false);
        }
    }//GEN-LAST:event_decodeCharsetSelectorActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        // TODO add your handling code here:
        port.closePort();
        dispose();
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void promptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptActionPerformed
        // TODO add your handling code here:
        sendPrompt();
    }//GEN-LAST:event_promptActionPerformed

    private void chooseSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseSendFileActionPerformed
        // TODO add your handling code here:
        int returnVal = fileChooser.showOpenDialog(AdvancedSerialMonitor.this);

        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            sendFile = fileChooser.getSelectedFile();
            jlSendFile.setText(sendFile.getName());
            jlSendError.setText("");
        }
        else
        {
            jlSendFile.setText("");
            sendFile = null;
        }
    }//GEN-LAST:event_chooseSendFileActionPerformed

    private void sendFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileButtonActionPerformed
        // TODO add your handling code here:
        if (sendFile != null)
        {
            if (port.isOpen())
            {
                try
                {

                    byte[] buff = IOUtils.toByteArray(new java.io.FileInputStream(sendFile));
                    port.writeBytes(buff, buff.length);
                    jlSendError.setForeground(Color.green);
                    jlSendError.setText("Contents of file sent.");
                } catch (java.io.IOException ioe)
                {
                    jlSendError.setForeground(Color.red);
                    jlSendError.setText("Error reading file. Please try again.");
                }
            }
            else
            {
                jlSendError.setForeground(Color.red);
                jlSendError.setText("Can't send data. Port not open.");
            }
        } else
        {
            jlSendError.setForeground(Color.red);
            jlSendError.setText("No file choosen. Please choose a file.");
        }
    }//GEN-LAST:event_sendFileButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        // TODO add your handling code here:
        about.setLocationRelativeTo(null);
        about.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
        about.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void chooseExportFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseExportFileActionPerformed
        // TODO add your handling code here:
        int returnVal = fileChooser.showOpenDialog(AdvancedSerialMonitor.this);

        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            exportFile = fileChooser.getSelectedFile();
            exportFileJl.setText(exportFile.getName());
            errorExportFileJl.setText("");
        } else {
            exportFileJl.setText("");
            exportFile = null;
        }
    }//GEN-LAST:event_chooseExportFileActionPerformed

    private void cpToClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpToClipboardActionPerformed
        // TODO add your handling code here:
        StringSelection stringSelection = new StringSelection(console.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }//GEN-LAST:event_cpToClipboardActionPerformed

    private void exportToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToFileActionPerformed
        // TODO add your handling code here:
        if (exportFile != null)
        {
            try {
                try (java.io.FileWriter fw = new java.io.FileWriter(exportFile, !append.isSelected())) {
                    fw.write(console.getText());
                    fw.flush();
                    errorExportFileJl.setForeground(Color.green);
                    errorExportFileJl.setText("Console exported.");
                }
            } catch (IOException ex) {
                Logger.getLogger(AdvancedSerialMonitor.class.getName()).log(Level.SEVERE, null, ex);
                errorExportFileJl.setForeground(Color.red);
                errorExportFileJl.setText("Can't open file. Please try again.");
            }
        }
        else
        {
            errorExportFileJl.setForeground(Color.red);
            errorExportFileJl.setText("No file selected. Please choose a file.");
        }
    }//GEN-LAST:event_exportToFileActionPerformed

    private void appendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_appendActionPerformed

    private void chooseStreamFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseStreamFileActionPerformed
        // TODO add your handling code here:
        int returnVal = fileChooser.showOpenDialog(AdvancedSerialMonitor.this);

        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try
            {
                streamFile = fileChooser.getSelectedFile();
                streamFileWriter = new java.io.FileWriter(streamFile, !streamOverwrite.isSelected());
                streamFileJl.setText(streamFile.getName());
                jlStreamError.setText("");
            }
            catch (IOException ex)
            {
                jlStreamError.setForeground(Color.red);
                jlStreamError.setText("Can't open file. Please try again.");
            }
        }
        else
        {
            streamFileJl.setText("");
            streamFile = null;
        }
    }//GEN-LAST:event_chooseStreamFileActionPerformed

    private void streamOverwriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_streamOverwriteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_streamOverwriteActionPerformed

    private void toggleStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleStreamActionPerformed
        // TODO add your handling code here:
        if (toggleStream.isSelected())
        {
            if (streamFile != null)
            {
                try
                {
                    streamFileWriter = new java.io.FileWriter(streamFile, !streamOverwrite.isSelected());
                    jlStreamError.setForeground (Color.green);
                    jlStreamError.setText("Stream opened.");
                } catch (IOException ex) {
                    Logger.getLogger(AdvancedSerialMonitor.class.getName()).log(Level.SEVERE, null, ex);
                    jlStreamError.setForeground(Color.red);
                    jlStreamError.setText("Can't open file. Please try again.");
                }
            }
            else
            {
                jlStreamError.setForeground(Color.red);
                jlStreamError.setText("No file selected. Please select a file.");
                toggleStream.setSelected(false);
            }
        }
        else
        {
            jlStreamError.setText("");
        }
    }//GEN-LAST:event_toggleStreamActionPerformed

    private void timestampCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timestampCheckBoxActionPerformed
        // TODO add your handling code here:
        timestampCheckBox.isSelected();
        
        if (timestampCheckBox.isSelected())
        {
            startWithTimestamp = true;
        }
    }//GEN-LAST:event_timestampCheckBoxActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        if (gp == null)
        {
            java.awt.EventQueue.invokeLater(() -> {
                gp = new GraphPlotter(this);
            });
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void shrinkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shrinkMenuItemActionPerformed
        // TODO add your handling code here:
        this.setSize(this.getMinimumSize());
    }//GEN-LAST:event_shrinkMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdvancedSerialMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new AdvancedSerialMonitor().setVisible(true);
        });
        
    }
    
    /**
     * 
     */
    private void stopComm ()
    {
        String prevPort = port.getSystemPortName();
        
        port.removeDataListener();
        port.closePort();
        startStop.setSelected(false);
        startStop.setText("Start");
        console.setForeground(new java.awt.Color(109,109,109));
        onOffLabel.setIcon(offIcon);
        
        refreshPortList();
        
        // Check to see if previous port is still available
        for (int i = 0; i < portComboBox.getItemCount(); i++)
        {
            if (((String) portComboBox.getItemAt(i)).equals(prevPort))
            {
                portComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * 
     */
    private void startComm()
    {
        if (!startStop.isSelected())
        {
            stopComm();
        }
        
        if (portComboBox.getSelectedItem().equals("Select"))
        {
            log.append ("No port selected. Please select a port.\n");
            startStop.setSelected (false);
            return;
        }

        // turn on serial comms
        startStop.setEnabled(false);
        port = SerialPort.getCommPort((String) portComboBox.getSelectedItem());
        port.setBaudRate(Integer.parseInt((String) baudRateSelector.getSelectedItem()));
        port.openPort();
        port.addDataListener(AdvancedSerialMonitor.this);
        log.append("Port "+(String)portComboBox.getSelectedItem() + " opened sucessfully.\n");
        startStop.setText("Stop");
        onOffLabel.setIcon(onIcon);
        console.setForeground(new java.awt.Color(0, 0, 0));
        startStop.setSelected(true);
        startStop.setEnabled(true);
    }
    
    private void updateComm ()
    {
        if (port != null)
        {
            if (port.isOpen())
            {
                if (portComboBox.getItemCount() > 0)
                {
                    if (!portComboBox.getSelectedItem().equals("Select"))
                        try
                        {
                            port.removeDataListener();
                            port.closePort();
                            port = SerialPort.getCommPort((String) portComboBox.getSelectedItem());
                            port.setBaudRate(Integer.parseInt((String) baudRateSelector.getSelectedItem()));
                            port.openPort();
                            port.addDataListener(AdvancedSerialMonitor.this);
                        }
                        catch (SerialPortInvalidPortException | NumberFormatException e)
                        {
                            log.append ("Port closed unexpectedly...\n");
                            onOffLabel.setIcon(offIcon);
                        }
                    else
                    {
                        log.append("Port closed unexpectedly...");
                        stopComm();
                    }
                }
            }
        }
    }
    
    private void sendPrompt ()
    {
        if (port != null)
        {
            if (port.isOpen())
            {
                if (prompt.getText().length() > 0 || endlComboBox.getSelectedIndex() != 0)
                {
                    String endl = "";

                    switch (endlComboBox.getSelectedIndex())
                    {
                        case 0: // Nothing
                        break;

                        case 1: // \n
                            endl = "\n";
                        break;

                        case 2: // \r
                            endl = "\r";
                        break;

                        case 3:
                            endl = "\0";
                        break;
                    }

                    if (encodeCharset.equals("Don't encode"))
                    {   
                        try
                        {
                            String text = prompt.getText().trim();
                            // Calculate length of buffer
                            int l = 1;
                            for (char c : text.toCharArray())
                            {
                                if (c == ',')
                                {
                                    l++;
                                }
                            }

                            byte[] buff = new byte[l];
                            l = 0;
                            StringBuilder sb = new StringBuilder("");

                            // Extract numbers
                            for (int i = 0; i < text.length(); i++)
                            {
                                if (text.charAt(i) == ',' || i == text.length() - 1)
                                {
                                    buff[l++] = (byte) Integer.parseInt(sb.toString());
                                }
                                else
                                {
                                    sb.append(text.charAt(i));
                                }
                            }

                            // Send numbers
                            port.writeBytes(buff, buff.length);

                            prompt.setText("");
                            jlSendError.setText("");
                        }
                        catch(NumberFormatException e)
                        {
                            jlSendError.setForeground(Color.red);
                            jlSendError.setText("No charset selected. Can't encode character.");
                        }
                    }
                    else
                    {
                        try
                        {
                            byte[] buff = (prompt.getText()+endl).getBytes(encodeCharset);
                            port.writeBytes(buff, buff.length);

                            prompt.setText("");
                            jlSendError.setText("");
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            jlSendError.setForeground(Color.red);
                            jlSendError.setText("Can't send data. Please select another charset.\n");
                        }
                    }
                }
                else
                {
                    jlSendError.setForeground(Color.red);
                    jlSendError.setText("You can't send nothing. Please enter something.");
                }
            }
            else
            {
                jlSendError.setForeground(Color.red);
                jlSendError.setText("Can't send data. Port not open.");
            }
        }
        else
        {
            jlSendError.setForeground(Color.red);
            jlSendError.setText("Can't send data. Port not open.");
        }
    }
    
    private void refreshPortList ()
    {
        SerialPort[] ports = SerialPort.getCommPorts();
        portComboBox.removeAllItems();
        for (SerialPort port1 : ports) {
            portComboBox.addItem(port1.getSystemPortName());
        }
        
        if (portComboBox.getItemCount() < 1)
            portComboBox.addItem("Select");
    }

    public String getDecodeCharset()
    {
        return decodeCharset;
    }
//<editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel StreamConsolePanel;
    private javax.swing.JFrame about;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JCheckBoxMenuItem alwaysOnTopCheckBox;
    private javax.swing.JCheckBox append;
    private javax.swing.JCheckBox autoscroll;
    private javax.swing.JComboBox<String> baudRateSelector;
    private javax.swing.JComboBox<String> charsetSelector;
    private javax.swing.JButton chooseExportFile;
    private javax.swing.JButton chooseSendFile;
    private javax.swing.JButton chooseStreamFile;
    private javax.swing.JButton clearConsole;
    private javax.swing.JButton clearLog;
    private javax.swing.JTextArea console;
    private javax.swing.JPanel consolePanel;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton cpToClipboard;
    private javax.swing.JComboBox<String> decodeCharsetSelector;
    private javax.swing.JComboBox<String> endlComboBox;
    private javax.swing.JLabel errorExportFileJl;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JPanel exportConsolePanel;
    private javax.swing.JLabel exportFileJl;
    private javax.swing.JButton exportToFile;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jlSendError;
    private javax.swing.JLabel jlSendFile;
    private javax.swing.JLabel jlStreamError;
    private javax.swing.JTextArea log;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> numberLengthComboBox;
    private javax.swing.JLabel numberLengthLabel;
    private javax.swing.JComboBox<String> numberSignednessComboBox;
    private javax.swing.JLabel numberSignednessLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel onOffLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JComboBox<String> portComboBox;
    private javax.swing.JPanel preferancesPanel;
    private javax.swing.JTextField prompt;
    private javax.swing.JComboBox<String> selectFontSize;
    private javax.swing.JButton sendData;
    private javax.swing.JPanel sendDataControlPanel;
    private javax.swing.JPanel sendDataPanel;
    private javax.swing.JPanel sendDataSendPanel;
    private javax.swing.JButton sendFileButton;
    private javax.swing.JMenuItem shrinkMenuItem;
    private javax.swing.JToggleButton startStop;
    private javax.swing.JLabel streamFileJl;
    private javax.swing.JCheckBox streamOverwrite;
    private javax.swing.JCheckBox timestampCheckBox;
    private javax.swing.JToggleButton toggleStream;
    private javax.swing.JCheckBox wordWrap;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
