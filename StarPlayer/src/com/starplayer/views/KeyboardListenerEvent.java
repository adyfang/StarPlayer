package com.starplayer.views;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import com.starplayer.main.PlayerMain;

/**
 * Key board listener
 * 
 * @author adyfang
 * 
 */
public class KeyboardListenerEvent
{

    public void keyBordListner()
    {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
        {
            @Override
            public void eventDispatched(AWTEvent event)
            {
                if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED)
                {
                    switch (((KeyEvent) event).getKeyCode())
                    {
                        case KeyEvent.VK_RIGHT:
                        {
                            int a = PlayerMain.getFrame().getVolumControlerSlider().getValue();
                            PlayerMain.getFrame().getVolumControlerSlider().setValue(a);
                            PlayerMain.forword((float) (((PlayerMain.getFrame().getProgressBar().getPercentComplete()
                                    * PlayerMain.getFrame().getProgressBar().getWidth() + 10)) / PlayerMain.getFrame()
                                    .getProgressBar().getWidth()));
                        }
                            break;
                        case KeyEvent.VK_LEFT:
                        {
                            PlayerMain.jumpTo((float) ((PlayerMain.getFrame().getProgressBar().getPercentComplete()
                                    * PlayerMain.getFrame().getProgressBar().getWidth() - 5) / PlayerMain.getFrame()
                                    .getProgressBar().getWidth()));
                        }
                            break;
                        case KeyEvent.VK_ESCAPE:
                        {
                            if (!PlayerMain.getFrame().getMediaPlayer().isFullScreen())
                                PlayerMain.fullScreen();
                            else
                                PlayerMain.originalScreen();

                        }
                            break;
                        case KeyEvent.VK_UP:
                        {
                            PlayerMain.getFrame().getVolumControlerSlider()
                                    .setValue(PlayerMain.getFrame().getVolumControlerSlider().getValue() + 1);
                            PlayerMain.getControlFrame().getVolumControlerSlider()
                                    .setValue(PlayerMain.getFrame().getVolumControlerSlider().getValue());
                            // MyMain.getFrame().getVolumLabel().setText("" +
                            // MyMain.getFrame().getVolumControlerSlider().getValue());
                        }
                            break;
                        case KeyEvent.VK_DOWN:
                            PlayerMain.getFrame().getVolumControlerSlider()
                                    .setValue(PlayerMain.getFrame().getVolumControlerSlider().getValue() - 1);
                            PlayerMain.getControlFrame().getVolumControlerSlider()
                                    .setValue(PlayerMain.getFrame().getVolumControlerSlider().getValue());
                            break;
                        case KeyEvent.VK_SPACE:
                        {
                            if (PlayerMain.getFrame().getMediaPlayer().isPlaying())
                            {
                                PlayerMain.pause();
                                PlayerMain.getControlFrame().getPlayButton()
                                        .setText(PlayerMain.getFrame().getPlayButton().getText());
                            }
                            else
                            {
                                PlayerMain.play();
                                PlayerMain.getControlFrame().getPlayButton()
                                        .setText(PlayerMain.getFrame().getPlayButton().getText());
                            }
                        }
                            break;
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }
}
