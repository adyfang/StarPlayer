package com.starplayer.views;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import com.starplayer.cache.PlayerCache;
import com.starplayer.main.PlayerMain;
import com.starplayer.playlist.PlayListFrame;

@SuppressWarnings("serial")
public class DisplayFrame extends JFrame
{
    private JPanel contentPane;

    private JLayeredPane videoPanel;

    EmbeddedMediaPlayerComponent playerComponent;

    private JPanel panel;

    private JPanel homePanel;

    private JButton stopButton;

    private JButton playButton;

    private JPanel controlPanel;

    private JProgressBar progressBar;

    private JSlider volumControlerSlider;

    private JMenuBar menuBar;

    private JMenu mnFile;

    private JMenuItem mntmOpenVideo;

    private JMenuItem mntmExit;

    private JButton forwardButton;

    private JButton backwordButton;

    private JButton fullScreenButton;

    private int flag = 0;

    private KeyboardListenerEvent kble;

    private JLabel volumeLabel;

    private JPanel progressTimePanel;

    private JLabel currentLabel;

    private JLabel totalLabel;

    private static PlayListFrame playListFrame;

    private JButton listButton;

    /**
     * Create the frame.
     */
    public DisplayFrame()
    {
        playListFrame = new PlayListFrame();
        setIconImage(PlayerCache.getLogo());
        setTitle(PlayerCache.PLAYER_TITLE);
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentMoved(ComponentEvent e)
            {
                if (playListFrame.getFlag() == 0)
                {
                    playListFrame.setVisible(true);
                    playListFrame.setBounds(PlayerMain.getFrame().getX() + PlayerMain.getFrame().getWidth() - 15,
                            PlayerMain.getFrame().getY(), 400, PlayerMain.getFrame().getHeight());
                }
            }

            @Override
            public void componentResized(ComponentEvent e)
            {

                if (playListFrame.getFlag() == 0 && !PlayerMain.getFrame().getMediaPlayer().isFullScreen())
                {
                    playListFrame.setVisible(true);
                    if (Math.abs(PlayerMain.getFrame().getWidth() - Toolkit.getDefaultToolkit().getScreenSize().width) <= 20)
                    {
                        playListFrame.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 400),
                                0, 400, PlayerMain.getFrame().getHeight());
                        playListFrame.setAlwaysOnTop(true);
                    }
                    else
                        playListFrame.setBounds(PlayerMain.getFrame().getX() + PlayerMain.getFrame().getWidth() - 15,
                                PlayerMain.getFrame().getY(), 400, PlayerMain.getFrame().getHeight());
                }

            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 500);
        kble = new KeyboardListenerEvent();
        kble.keyBordListner();
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu(PlayerCache.MENU_FILE);
        menuBar.add(mnFile);

        mntmOpenVideo = new JMenuItem(PlayerCache.MENU_FILE_OPEN_FILE);
        mntmOpenVideo.setSelected(true);
        mnFile.add(mntmOpenVideo);

        mntmExit = new JMenuItem(PlayerCache.MENU_FILE_EXIT);
        mnFile.add(mntmExit);

        mntmOpenVideo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPlayer(true);
                PlayerMain.openVideo();
                playListFrame.setPlayerList(new ArrayList<String>(PlayerMain.getPlayerCache().getViewMap().values()));
                playListFrame.getScrollPane().setViewportView(playListFrame.getPlayerList());

                playListFrame.getPlayerList().setSelectedValue(PlayerMain.getPlayerCache().getLastFile(), true);
                // 选中文件在播放列表中的背景色
                playListFrame.getPlayerList().setSelectionBackground(Color.GRAY);
                
                playListFrame.displayAttr();
            }
        });

        mntmExit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PlayerMain.exit();
            }
        });

        contentPane = new JPanel();
        contentPane.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentMoved(ComponentEvent e)
            {
                System.out.println();
            }
        });
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        videoPanel = new JLayeredPane();
        contentPane.add(videoPanel, BorderLayout.CENTER);
        videoPanel.setLayout(new BorderLayout(0, 0));

        playerComponent = new EmbeddedMediaPlayerComponent();
        final Canvas videoSurface = playerComponent.getVideoSurface();
        videoSurface.addMouseListener(new MouseAdapter()
        {
            String btnText = ">";

            @SuppressWarnings("unused")
            String btnText1 = PlayerCache.BTN_FULL;

            Timer mouseTime;

            @Override
            public void mouseClicked(MouseEvent e)
            {
                int i = e.getButton();
                if (i == MouseEvent.BUTTON1)
                {
                    if (e.getClickCount() == 1)
                    {
                        mouseTime = new Timer(350, new ActionListener()
                        {

                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                if (playButton.getText() == ">")
                                {
                                    PlayerMain.play();
                                    btnText = "||";
                                    playButton.setText(btnText);
                                }
                                else
                                {
                                    PlayerMain.pause();
                                    btnText = ">";
                                    playButton.setText(btnText);
                                }
                                mouseTime.stop();
                            }
                        });
                        mouseTime.restart();
                    }
                    else if (e.getClickCount() == 2 && mouseTime.isRunning())
                    {
                        mouseTime.stop();
                        if (flag == 0)
                        {
                            PlayerMain.fullScreen();
                        }
                        else if (flag == 1)
                        {
                            PlayerMain.originalScreen();
                        }
                    }
                }
            }

        });

        homePanel = new JPanel()
        {
            @Override
            public void paint(Graphics g)
            {
                super.paint(g);
                ImageIcon home = new ImageIcon(PlayerCache.getHome());
                g.drawImage(home.getImage(), 95, 0, this.getWidth() - 190, this.getHeight(), this);
            }
        };
        playerComponent.setName("player");
        homePanel.setName("home");
        videoPanel.add(homePanel, BorderLayout.CENTER);
        panel = new JPanel();
        videoPanel.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        controlPanel = new JPanel();
        @SuppressWarnings("unused")
        FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
        panel.add(controlPanel);

        playButton = new JButton(">");
        playButton.addMouseListener(new MouseAdapter()
        {
            String btnText = ">";

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (playButton.getText() == ">")
                {
                    PlayerMain.play();
                    btnText = "||";
                    playButton.setText(btnText);
                }
                else
                {
                    PlayerMain.pause();
                    btnText = ">";
                    playButton.setText(btnText);
                }

            }
        });
        controlPanel.add(playButton);

        backwordButton = new JButton("<<");
        backwordButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                PlayerMain.jumpTo((float) ((progressBar.getPercentComplete() * progressBar.getWidth() - 5) / progressBar
                        .getWidth()));
            }
        });
        controlPanel.add(backwordButton);

        volumControlerSlider = new JSlider();
        volumControlerSlider.setPaintLabels(true);
        volumControlerSlider.setSnapToTicks(true);
        volumControlerSlider.setPaintTicks(true);
        volumControlerSlider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                volumControlerSlider.setValue((int) (e.getX() * ((float) volumControlerSlider.getMaximum() / volumControlerSlider
                        .getWidth())));
                // volumLabel.setText("" + volumControlerSlider.getValue());
            }

        });
        volumControlerSlider.setValue(100);
        volumControlerSlider.setMaximum(120);
        volumControlerSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                PlayerMain.setVolume(volumControlerSlider.getValue());
            }
        });

        forwardButton = new JButton(">>");
        forwardButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {

                PlayerMain.jumpTo((float) (((progressBar.getPercentComplete() * progressBar.getWidth() + 10)) / progressBar
                        .getWidth()));
            }
        });

        stopButton = new JButton(PlayerCache.BTN_STOP);

        stopButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                PlayerMain.stop();
                playButton.setText(">");
            }
        });
        controlPanel.add(stopButton);
        controlPanel.add(forwardButton);

        fullScreenButton = new JButton(PlayerCache.BTN_FULL);
        fullScreenButton.addMouseListener(new MouseAdapter()
        {
            @SuppressWarnings("unused")
            String btnText = PlayerCache.BTN_FULL;

            @SuppressWarnings("unused")
            int flag = 0;

            @Override
            public void mouseClicked(MouseEvent e)
            {

                PlayerMain.fullScreen();
            }
        });
        controlPanel.add(fullScreenButton);

        controlPanel.add(volumControlerSlider);

        volumeLabel = new JLabel("" + volumControlerSlider.getValue());
        controlPanel.add(volumeLabel);

        listButton = new JButton();
        if (playListFrame.getFlag() == 1)
        {
            listButton.setText(PlayerCache.BTN_LIST_OPENED);
        }
        else if (playListFrame.getFlag() == 0)
        {
            listButton.setText(PlayerCache.BTN_LIST_CLOSED);
        }
        listButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {

                if (PlayerCache.BTN_LIST_OPENED.equals(listButton.getText()))
                {
                    playListFrame.setVisible(true);
                    if (Math.abs(PlayerMain.getFrame().getWidth() - Toolkit.getDefaultToolkit().getScreenSize().width) <= 20)
                    {
                        playListFrame.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 400, 0, 400,
                                PlayerMain.getFrame().getHeight());
                    }
                    else
                    {
                        playListFrame.setBounds(PlayerMain.getFrame().getX() + PlayerMain.getFrame().getWidth() - 15,
                                PlayerMain.getFrame().getY(), 400, PlayerMain.getFrame().getHeight());
                    }
                    playListFrame.setFlag(0);
                    listButton.setText(PlayerCache.BTN_LIST_CLOSED);
                }
                else if (PlayerCache.BTN_LIST_CLOSED.equals(listButton.getText()))
                {
                    playListFrame.setVisible(false);
                    listButton.setText(PlayerCache.BTN_LIST_OPENED);
                }

            }
        });
        controlPanel.add(listButton);

        progressTimePanel = new JPanel();
        panel.add(progressTimePanel, BorderLayout.NORTH);
        progressTimePanel.setLayout(new BorderLayout(0, 0));

        progressBar = new JProgressBar();
        progressTimePanel.add(progressBar, BorderLayout.CENTER);
        progressBar.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int x = e.getX();
                PlayerMain.jumpTo(((float) x / progressBar.getWidth()));

            }
        });

        currentLabel = new JLabel("00：00");
        progressTimePanel.add(currentLabel, BorderLayout.WEST);

        totalLabel = new JLabel("02：13");
        progressTimePanel.add(totalLabel, BorderLayout.EAST);
    }
    
    public void showPlayer(boolean isShowPlayer)
    {
        boolean isHaveHome = false;
        for (Component cp : videoPanel.getComponents())
        {
            if ("home".equals(cp.getName()))
            {
                isHaveHome = true;
                break;
            }
        }
        if (isShowPlayer)
        {
            if (isHaveHome)
            {
                videoPanel.remove(homePanel);
                playerComponent.setVisible(true);
                videoPanel.add(playerComponent);
            }
        }
        else
        {
            if (!isHaveHome)
            {
                videoPanel.add(homePanel);
                playerComponent.setVisible(false);
                videoPanel.remove(playerComponent);
            }
        }
    }

    // Get the video
    public EmbeddedMediaPlayer getMediaPlayer()
    {
        return playerComponent.getMediaPlayer();
    }

    public JProgressBar getProgressBar()
    {
        return progressBar;
    }

    public EmbeddedMediaPlayerComponent getPlayComponent()
    {
        return playerComponent;
    }

    public JButton getPlayButton()
    {
        return playButton;
    }

    public JPanel getControlPanel()
    {
        return controlPanel;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public int getFlag()
    {
        return flag;
    }

    public JSlider getVolumControlerSlider()
    {
        return volumControlerSlider;
    }

    public JLabel getVolumeLabel()
    {
        return volumeLabel;
    }

    public JLabel getCurrentLabel()
    {
        return currentLabel;
    }

    public JLabel getTotalLabel()
    {
        return totalLabel;
    }

    public JPanel getProgressTimePanel()
    {
        return progressTimePanel;
    }

    public JButton getListButton()
    {
        return listButton;
    }

    public static PlayListFrame getPlayListFrame()
    {
        return playListFrame;
    }

    public JLayeredPane getVideoPanel()
    {
        return videoPanel;
    }

    public JPanel getHomePanel()
    {
        return homePanel;
    }
}
