package com.starplayer.playlist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import com.starplayer.main.PlayerMain;

@SuppressWarnings("serial")
public class PlayListFrame extends JFrame
{

    private JPanel contentPane;

    private int flag = 0;

    @SuppressWarnings("rawtypes")
    private JList list = new JList();

    private JScrollPane scrollPane;

    private JPanel panel;

    private JButton historyClearButton;

    @SuppressWarnings("rawtypes")
    private DefaultListModel dlm = new DefaultListModel();

    private JButton searchButton;

    private JTextField searchtField;

    private JPanel panel_1;

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
            setList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
        }
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        scrollPane = new JScrollPane();
        scrollPane.setEnabled(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        list.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    String name = (String) list.getSelectedValue();
                    PlayerMain.openVedioFromList(name);
                    setList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                    getScrollPane().setViewportView(getList());
                }
            }
        });
        contentPane.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(getList());

        panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));

        panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        searchtField = new JTextField();
        searchtField.setText("");
        panel_1.add(searchtField);
        searchtField.setColumns(10);

        // History search, will realize it when i am free
        searchButton = new JButton("Search History");
        searchButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JTextField sField = (JTextField)panel_1.getComponent(0);
                String search = sField.getText();
                if (null != search && search.trim().length() > 0)
                {
                    PlayerMain.getPlayerCache().setSearchMap(PlayerMain.getPlayerCache().getViewMap());
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
                    PlayerMain.getPlayerCache().getSearchMap().clear();
                }
                
                setList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                getScrollPane().setViewportView(getList());
//                final PlayerDialog dialog = new PlayerDialog();
//                dialog.setVisible(true);
//                dialog.getCancelButton().setVisible(false);
//                dialog.setText("The Performance will come soon!!");
//                dialog.setBounds(PlayerMain.getFrame().getPlayListFrame().getX() + 15, PlayerMain.getFrame()
//                        .getPlayListFrame().getY() + 100, 350, 115);
//                dialog.getOkButton().addMouseListener(new MouseAdapter()
//                {
//                    @Override
//                    public void mouseClicked(MouseEvent e)
//                    {
//                        dialog.setVisible(false);
//                    }
//                });
            }
        });
        panel_1.add(searchButton);

        historyClearButton = new JButton("Clear History");
        panel_1.add(historyClearButton);

        // Clear history
        historyClearButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                final PlayerDialog dialog = new PlayerDialog();
                dialog.setVisible(true);
                dialog.setText("Are you sure to clear history?");
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
                            list.setModel(dlm);
                            scrollPane.setViewportView(getList());
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }

                    }
                });

            }

        });

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
    public JList getList()
    {

        return list;
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public void setList(ArrayList<String> arrayList)
    {

        dlm = new DefaultListModel();
        for (int i = arrayList.size() - 1; i >= 0; i--)
        {
            dlm.addElement(arrayList.get(i));
        }

        list.setModel(dlm);
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public JTextField getSearchtField()
    {
        return searchtField;
    }

    public void setSearchtField(JTextField searchtField)
    {
        this.searchtField = searchtField;
    }

}
