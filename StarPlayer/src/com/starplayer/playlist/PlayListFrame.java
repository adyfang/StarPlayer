package com.starplayer.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.starplayer.cache.PlayerCache;
import com.starplayer.main.PlayerMain;

@SuppressWarnings("serial")
public class PlayListFrame extends JFrame
{

    private JPanel contentPane;

    private int flag = 0;

    @SuppressWarnings("rawtypes")
    private JList playerList = new JList();

    private JScrollPane scrollPane;

    private JScrollPane attrPane;

    private JLabel attrText;

    private JPanel searchPanel;

    private JButton historyClearButton;

    @SuppressWarnings("rawtypes")
    private DefaultListModel dlm = new DefaultListModel();

    // private JButton searchButton;

    private JButton openFileButton;

    private JTextField searchField;

    private JPanel historyPanel;

    /**
     * Create the frame,to display the watched history .
     */
    @SuppressWarnings("static-access")
    public PlayListFrame()
    {
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                flag = 1;
                PlayerMain.getFrame().getListButton().setText("List>>");
                PlayerMain.getControlFrame().getListButton().setText(PlayerMain.getFrame().getListButton().getText());
            }
        });
        setType(Type.UTILITY);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMaximizedBounds(new Rectangle((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 400, 0, 400,
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        // setBounds(100, 100, 229, 394);
        Map<String, String> historyMap = PlayerMain.getPlayerCache().readHistory();
        if (!historyMap.isEmpty())
        {
            PlayerMain.getPlayerCache().setViewMap(historyMap);
            PlayerMain.getPlayerCache().setSearchMap(historyMap);
            setPlayerList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
        }
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        scrollPane = new JScrollPane()
        {
            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(contentPane.getWidth(), 200);
            }
        };
        scrollPane.setEnabled(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        playerList.setPreferredSize(new Dimension(scrollPane.getWidth(), 200));
        playerList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    String name = (String) playerList.getSelectedValue();
                    playerList.setSelectedValue(name, true);
                    // 选中文件在播放列表中的背景色
                    playerList.setSelectionBackground(Color.GRAY);
                    // 选中文件在播放列表中的字体颜色
                    // list.setSelectionForeground(Color.BLACK);

                    PlayerMain.openVideoFromList(name);
                    displayAttr();
                    // setList(new
                    // ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                    // getScrollPane().setViewportView(getList());
                }
            }
        });
        contentPane.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(playerList);

        attrText = new JLabel();
        attrText.setAutoscrolls(true);
        attrText.setSize(contentPane.getWidth(), 200);
        attrPane = new JScrollPane(attrText)
        {
            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(contentPane.getWidth(), 200);
            }
        };
        attrPane.setEnabled(false);
        attrPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        attrPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentPane.add(attrPane, BorderLayout.SOUTH);

        searchPanel = new JPanel();
        contentPane.add(searchPanel, BorderLayout.NORTH);
        searchPanel.setLayout(new BorderLayout(0, 0));

        historyPanel = new JPanel();
        searchPanel.add(historyPanel, BorderLayout.CENTER);
        historyPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        searchField = new JTextField();
        searchField.setText(PlayerCache.BTN_LIST_SEARCH);
        searchField.setPreferredSize(new Dimension(180, 25));
        historyPanel.add(searchField);
        // searchField.setColumns(10);

        searchField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                // 获得焦点的时候,清空提示文字
                JTextField textField = (JTextField) e.getSource();
                String search = textField.getText();
                if (null != search && search.trim().length() > 0 && PlayerCache.BTN_LIST_SEARCH.equals(search))
                {
                    searchField.setText("");
                }

                setPlayerList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                getScrollPane().setViewportView(getPlayerList());

                getPlayerList().setSelectedValue(PlayerMain.getPlayerCache().getLastFile(), true);
                // 选中文件在播放列表中的背景色
                getPlayerList().setSelectionBackground(Color.GRAY);
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                // 失去焦点的时候,判断如果为空,就显示提示文字
                JTextField textField = (JTextField) e.getSource();
                String search = textField.getText();
                if (null == search || search.trim().length() == 0)
                {
                    searchField.setText(PlayerCache.BTN_LIST_SEARCH);
                }
            }
        });

        searchField.addKeyListener(new KeyAdapter()
        {
            public void keyReleased(KeyEvent e)
            {
                JTextField textField = (JTextField) e.getSource();
                String search = textField.getText();
                startSearch(search);
            }

            public void keyTyped(KeyEvent e)
            {
            }

            public void keyPressed(KeyEvent e)
            {
            }
        });

        openFileButton = new JButton(PlayerCache.MENU_FILE_OPEN_FILE);
        historyPanel.add(openFileButton);

        // 打开文件
        openFileButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                PlayerMain.openVideo();
                setPlayerList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                getScrollPane().setViewportView(getPlayerList());

                getPlayerList().setSelectedValue(PlayerMain.getPlayerCache().getLastFile(), true);
                // 选中文件在播放列表中的背景色
                getPlayerList().setSelectionBackground(Color.GRAY);
                
                displayAttr();
            }
        });

        // 搜索播放列表
        // searchButton = new JButton(PlayerCache.BTN_LIST_SEARCH);
        // searchButton.addMouseListener(new MouseAdapter()
        // {
        // @Override
        // public void mouseClicked(MouseEvent e)
        // {
        // JTextField sField = (JTextField) historyPanel.getComponent(0);
        // String search = sField.getText();
        // startSearch(search);
        // }
        // });
        // historyPanel.add(searchButton);

        historyClearButton = new JButton(PlayerCache.BTN_LIST_CLEAR);
        historyPanel.add(historyClearButton);

        // Clear history
        historyClearButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                final PlayerDialog dialog = new PlayerDialog();
                dialog.setVisible(true);
                dialog.setText(PlayerCache.DIRLOG_CLEAR_PLAYLIST);
                dialog.setBounds(PlayerMain.getFrame().getPlayListFrame().getX() + 15, PlayerMain.getFrame()
                        .getPlayListFrame().getY() + 100, 350, 115);
                dialog.getCancelButton().addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        dialog.setVisible(false);
                    }
                });

                dialog.getOkButton().addMouseListener(new MouseAdapter()
                {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        try
                        {
                            dialog.setVisible(false);
                            PlayerMain.getPlayerCache().clearHistory();
                            PlayerMain.getPlayerCache().getViewMap().clear();
                            dlm.clear();
                            playerList.setModel(dlm);
                            scrollPane.setViewportView(getPlayerList());
                            PlayerMain.getPlayerCache().setLastFile(null);
                            displayAttr();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }

                    }
                });

            }

        });

    }

    private void startSearch(String search)
    {
        if (null != search && search.trim().length() > 0)
        {
            PlayerMain.getPlayerCache().getViewMap().clear();
            for (Entry<String, String> entry : PlayerMain.getPlayerCache().getSearchMap().entrySet())
            {
                if (entry.getValue().contains(search))
                {
                    PlayerMain.getPlayerCache().getViewMap().put(entry.getKey(), entry.getValue());
                }
            }
        }
        else
        {
            PlayerMain.getPlayerCache().setViewMap(PlayerMain.getPlayerCache().getSearchMap());
        }

        setPlayerList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
        getScrollPane().setViewportView(getPlayerList());

        getPlayerList().setSelectedValue(PlayerMain.getPlayerCache().getLastFile(), true);
        // 选中文件在播放列表中的背景色
        getPlayerList().setSelectionBackground(Color.GRAY);
    }

    public void displayAttr()
    {
        if (PlayerMain.getPlayerCache().getLastFile() != null)
        {
            try
            {
                File mvFile = new File(PlayerMain.getPlayerCache().getLastFile());
                String fileName = mvFile.getName();
                if (fileName.lastIndexOf(".") != -1)
                {
                    fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
                }
                File attrFile = new File(mvFile.getParent() + File.separator + fileName);
                if (attrFile.exists())
                {
                    List<String> lineList = Files.readAllLines(Paths.get(attrFile.getAbsolutePath()),
                            Charset.forName("GBK"));
                    String content = "";
                    int i = 0;
                    int total = lineList.size();
                    for (String line : lineList)
                    {
                        content += line;
                        if (i++ < total - 1)
                        {
                            content += "<br/>";
                        }
                    }
                    if (content.length() == 0)
                    {
                        attrText.setText("<html>Nothing</html>");
                        attrText.setHorizontalAlignment(SwingConstants.CENTER);
                        attrText.setVerticalAlignment(SwingConstants.CENTER);
                    }
                    else
                    {
                        attrText.setText("<html>" + content + "</html>");
                        attrText.setHorizontalAlignment(SwingConstants.LEFT);
                        attrText.setVerticalAlignment(SwingConstants.TOP);
                    }
                }
                else
                {
                    attrText.setText("<html>" + fileName + " is not exits.</html>");
                    attrText.setHorizontalAlignment(SwingConstants.CENTER);
                    attrText.setVerticalAlignment(SwingConstants.CENTER);
                }
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            attrText.setText("<html>Nothing</html>");
        }
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    @SuppressWarnings("rawtypes")
    public JList getPlayerList()
    {
        return playerList;
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public void setPlayerList(ArrayList<String> arrayList)
    {
        dlm = new DefaultListModel();
        for (int i = arrayList.size() - 1; i >= 0; i--)
        {
            dlm.addElement(arrayList.get(i));
        }

        playerList.setModel(dlm);
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public JTextField getSearchtField()
    {
        return searchField;
    }

    public void setSearchField(JTextField searchField)
    {
        this.searchField = searchField;
    }

    public JLabel getAttrText()
    {
        return attrText;
    }
}
