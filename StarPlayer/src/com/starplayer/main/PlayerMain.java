package com.starplayer.main;

import java.awt.EventQueue;
import java.awt.SplashScreen;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_logo_position_e;
import uk.co.caprica.vlcj.player.Logo;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.starplayer.cache.PlayerCache;
import com.starplayer.cache.VideoFileFilter;
import com.starplayer.views.ControlFrame;
import com.starplayer.views.DisplayFrame;
import com.starplayer.views.VideoDuration;
import com.sun.jna.NativeLibrary;

/**
 * 入口
 * 
 * @author adyfang
 * 
 */
public class PlayerMain
{

    private static DisplayFrame frame;

    private static String filePath;

    private static ControlFrame controlFrame;

    private static VideoDuration videoDuration;

    private static PlayerCache playerCache = new PlayerCache();

    /**
     * 打包后jar包中META-INF\MANIFEST.MF文件替换为工程中META-INF\MANIFEST.MF，增加启动闪屏
     * 
     * @param args
     * @throws IOException
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws IOException
    {
        new Thread()
        {
            public void run()
            {
                homeSplash();
            }
        }.start();
        try
        {
            Thread.sleep(2000);
        } catch (Exception e)
        {
        }

        playerCache.setViewMap(playerCache.readHistory());
        playerCache.setSearchMap(playerCache.getViewMap());
        String arch = System.getProperty("sun.arch.data.model");
        if (RuntimeUtil.isWindows())
        {
            filePath = arch.indexOf("64") != -1 ? "plugin\\64" : "plugin\\32";
        }
        File file = new File(filePath);
        System.out.println(file.getCanonicalPath());
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), file.getCanonicalPath());
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());

        EventQueue.invokeLater(new Runnable()
        {
            @SuppressWarnings("unused")
            public void run()
            {
                try
                {
                    frame = new DisplayFrame();
                    frame.setVisible(true);
                    controlFrame = new ControlFrame();

                    videoDuration = new VideoDuration();
                    String[] optionDecode =
                    { "--subsdec-encoding=GB18030", "--loop", "--repeat", "--video-title=" + PlayerCache.PLAYER_TITLE };
                    // Publish progress of movie and get the total time and
                    // current time
                    new SwingWorker<String, Integer>()
                    {

                        @Override
                        protected String doInBackground() throws Exception
                        {
                            while (true)
                            {
                                // current time and total time
                                long totalTime = frame.getMediaPlayer().getLength();
                                long currentTime = frame.getMediaPlayer().getTime();
                                videoDuration.timeCalculate(totalTime, currentTime);
                                frame.getCurrentLabel().setText(
                                        videoDuration.getMinitueCurrent() + ":" + videoDuration.getSecondCurrent());
                                frame.getTotalLabel().setText(
                                        videoDuration.getMinitueTotal() + ":" + videoDuration.getSecondTotal());
                                controlFrame.getCurrentLabel().setText(frame.getCurrentLabel().getText());
                                controlFrame.getTotalLabel().setText(frame.getTotalLabel().getText());

                                // Get the percent of the current movie progress
                                float percent = (float) currentTime / totalTime;
                                publish((int) (percent * 100));
                                Thread.sleep(200);
                            }
                        }

                        protected void process(java.util.List<Integer> chunks)
                        {
                            for (int v : chunks)
                            {
                                frame.getProgressBar().setValue(v);
                                controlFrame.getProgressBar().setValue(v);
                            }
                        };
                    }.execute();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void homeSplash()
    {
        try
        {
            SplashScreen splash = SplashScreen.getSplashScreen();
            System.out.println(PlayerCache.getWelcome());
            splash.setImageURL(PlayerCache.getWelcome());
            splash.update();
            Thread.sleep(2000);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Play the opened movie
    public static void play()
    {
        frame.getMediaPlayer().play();
        frame.getPlayButton().setText("||");

    }

    // Pause
    public static void pause()
    {
        frame.getMediaPlayer().pause();
        frame.getPlayButton().setText(">");
    }

    // Stop
    public static void stop()
    {
        frame.getMediaPlayer().stop();
        frame.getPlayButton().setText(">");
        frame.showPlayer(false);
    }

    // Forward
    public static void forword(float to)
    {
        frame.getMediaPlayer().setTime((long) (to * frame.getMediaPlayer().getLength()));
    }

    // Backword
    public static void backword()
    {
        frame.getPlayComponent().backward(frame.getMediaPlayer());
    }

    // Set current progress time
    public static void jumpTo(float to)
    {
        frame.getMediaPlayer().setTime((long) (to * frame.getMediaPlayer().getLength()));
    }

    // Set volume
    public static void setVolume(int v)
    {
        frame.getMediaPlayer().setVolume(v);
        frame.getVolumeLabel().setText("" + frame.getMediaPlayer().getVolume());
        controlFrame.getVolumLabel().setText("" + frame.getMediaPlayer().getVolume());
    }

    // Open view from your computer
    @SuppressWarnings("static-access")
    public static void openVideo()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new VideoFileFilter());
        if (null != playerCache.getLastPath())
        {
            chooser.setCurrentDirectory(new File(playerCache.getLastPath()));
        }
        else
        {
            chooser.changeToParentDirectory();
        }
        int v = chooser.showOpenDialog(null);
        if (v == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            // String name = file.getName();
            String filePath = file.getAbsolutePath();
            playerCache.setLastFile(filePath);
            playerCache.getViewMap().put(filePath, filePath);
            // Save the list history
            try
            {
                playerCache.setLastPath(file.getParent());
                File dir = new File(file.getParent());
                String suffix = null;
                String name = null;
                String path = null;
                for (File f : dir.listFiles())
                {
                    if (f.isFile())
                    {
                        name = f.getName();
                        suffix = name.substring(name.lastIndexOf(".") + 1);
                        if (PlayerCache.FILE_SUFFIX.contains(suffix))
                        {
                            path = f.getAbsolutePath();
                            playerCache.getViewMap().put(path, path);
                        }
                    }
                }
                playerCache.setSearchMap(playerCache.getViewMap());
                playerCache.writeHistory(playerCache.getViewMap());
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            frame.showPlayer(true);
            frame.getMediaPlayer().playMedia(file.getAbsolutePath());
            frame.getPlayButton().setText("||");
        }
    }

    // Open movie from the list view
    @SuppressWarnings("static-access")
    public static void openVideoFromList(String name)
    {
        String path = playerCache.getViewMap().get(name);
        playerCache.setLastFile(path);
        playerCache.getViewMap().put(path, path);
        playerCache.writeHistory(playerCache.getViewMap());
        frame.showPlayer(true);
        frame.getMediaPlayer().playMedia(path);
        frame.getPlayButton().setText("||");
    }

    // Exit to program
    public static void exit()
    {
        frame.getMediaPlayer().release();
        System.exit(0);
    }

    // Enter full screen
    public static void fullScreen()
    {
        frame.getMediaPlayer().setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame));
        frame.getProgressBar().setVisible(false);
        frame.getControlPanel().setVisible(false);
        frame.getProgressTimePanel().setVisible(false);
        frame.getJMenuBar().setVisible(false);
        frame.getMediaPlayer().setFullScreen(true);
        controlFrame.getVolumLabel().setText("" + frame.getMediaPlayer().getVolume());
        controlFrame.getListButton().setText("List>>");

        frame.setFlag(1);
        frame.getPlayComponent().getVideoSurface().addMouseMotionListener(new MouseMotionListener()
        {

            @Override
            public void mouseMoved(MouseEvent e)
            {
                if (frame.getFlag() == 1)
                {
                    controlFrame.setLocation((frame.getWidth() - controlFrame.getWidth()) / 2, frame.getHeight()
                            - controlFrame.getHeight());
                    controlFrame.setVisible(true);
                    controlFrame.getVolumControlerSlider().setValue(frame.getVolumControlerSlider().getValue());
                    if (frame.getMediaPlayer().isPlaying())
                    {
                        controlFrame.getPlayButton().setText("||");
                    }
                    else
                    {
                        controlFrame.getPlayButton().setText(">");
                    }
                }

            }

            @Override
            public void mouseDragged(MouseEvent e)
            {

            }
        });

    }

    // Exit from full screen
    @SuppressWarnings("static-access")
    public static void originalScreen()
    {
        frame.getProgressBar().setVisible(true);
        frame.getControlPanel().setVisible(true);
        frame.getProgressTimePanel().setVisible(true);
        frame.getJMenuBar().setVisible(true);
        frame.getMediaPlayer().setFullScreen(false);
        frame.setFlag(0);
        if (frame.getMediaPlayer().isPlaying())
            frame.getPlayButton().setText("||");
        else
            frame.getPlayButton().setText(">");

        if (frame.getPlayListFrame().getFlag() == 1)
        {
            frame.getListButton().setText("List>>");
        }
        else if (frame.getPlayListFrame().getFlag() == 0)
        {
            frame.getListButton().setText("<<List");
        }
        controlFrame.setVisible(false);
    }

    public static DisplayFrame getFrame()
    {
        return frame;
    }

    public static ControlFrame getControlFrame()
    {
        return controlFrame;
    }

    public void setLogo()
    {
        Logo logo = Logo.logo().file("picture/logo.png").position(libvlc_logo_position_e.top_left).opacity(0.2f)
                .enable();
        frame.getMediaPlayer().setLogo(logo);
    }

    public static PlayerCache getPlayerCache()
    {
        return playerCache;
    }

}
