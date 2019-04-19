package com.starplayer.cache;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PlayerCache
{
    @SuppressWarnings("serial")
    public static final Set<String> FILE_SUFFIX = new HashSet<String>()
    {
        {
            add("wma");
            add("wmv");
            add("avi");
            add("mpg");
            add("mlv");
            add("mpe");
            add("mpeg");
            add("mp4");
            add("mov");
            add("3gp");
        }
    };

    // public static String NEW_LINE = System.getProperty("line.separator");

    /** Logo logo.png */
    public static String PLAYER_LOGO = "/picture/logo.jpg";

    /** 首页 home.jpg */
    public static String PLAYER_HOME = "/picture/home.jpg";

    /** 首页 welcome.jpg */
    public static String PLAYER_WELCOME = "/picture/welcome.jpg";

    /** Title */
    public static String PLAYER_TITLE = "RATE TURNTABLE TEST SIMULATION PROGRAM";

    /** File */
    public static String MENU_FILE = "File";

    /** Open File */
    public static String MENU_FILE_OPEN_FILE = "Open File";

    /** Exit */
    public static String MENU_FILE_EXIT = "Exit";

    /** Full */
    public static String BTN_FULL = "Full";

    /** Small */
    public static String BTN_SMALL = "Small";

    /** Stop */
    public static String BTN_STOP = "Stop";

    /** 打开播放列表 List>> */
    public static String BTN_LIST_OPENED = "List>>";

    /** 关闭播放列表 <<List */
    public static String BTN_LIST_CLOSED = "<<List";

    /** Clear */
    public static String BTN_LIST_CLEAR = "Clear";

    /** Search */
    public static String BTN_LIST_SEARCH = "Search";

    /** PROGRESS */
    public static String PROGRESS = "00:00";

    /** 清除播放列表提示 */
    public static String DIRLOG_CLEAR_PLAYLIST = "Are you sure to clear the playlist?";

    private static String OUTPUT_PATH = "cache.tmp";

    private Map<String, String> viewMap = new HashMap<String, String>();

    private Map<String, String> searchMap = new HashMap<String, String>();

    /** 最近一次打开文件的目录 */
    private String lastPath = null;

    /** 最近一次打开的文件 */
    private String lastFile = null;

    /**
     * 写入缓存文件
     * 
     * @param map
     */
    public static void writeHistory(Map<String, String> map)
    {
        try
        {
            Map<String, String> tempMap = new HashMap<String, String>();
            if (map instanceof TreeMap)
            {
                tempMap.putAll(map);
                FileOutputStream fos = new FileOutputStream(OUTPUT_PATH);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(tempMap);
                oos.close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 读取缓存文件
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> readHistory()
    {
        Map<String, String> historyMap = new HashMap<String, String>();
        try
        {
            FileInputStream fis = new FileInputStream(OUTPUT_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            historyMap = (HashMap<String, String>) ois.readObject();
            ois.close();
            // Map<String, String> historyMap = new HashMap<String, String>();
            // List<String> paths = Files.readAllLines(Paths.get(OUTPUT_PATH),
            // Charset.forName("UTF-8"));
            // for (String path : paths)
            // {
            // historyMap.put(path, path);
            // }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return historyMap;
    }

    /**
     * 清空历史记录
     * 
     * @throws IOException
     */
    public static void clearHistory() throws IOException
    {
        RandomAccessFile rf = new RandomAccessFile(OUTPUT_PATH, "rw");
        FileChannel fc = rf.getChannel();
        fc.truncate(1);
    }

    public static Image getLogo()
    {
        Image logo = Toolkit.getDefaultToolkit().getImage(PlayerCache.class.getClass().getResource(PLAYER_LOGO));
        return logo;
    }

    public static URL getHome()
    {
        return PlayerCache.class.getClass().getResource(PLAYER_HOME);
    }

    public static URL getWelcome()
    {
        return PlayerCache.class.getClass().getResource(PLAYER_WELCOME);
    }

    public Map<String, String> getViewMap()
    {
        return viewMap;
    }

    public void setViewMap(Map<String, String> viewMap)
    {
        this.viewMap = new TreeMap<String, String>(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2) * -1;
            }
        });
        this.viewMap.putAll(viewMap);
    }

    public Map<String, String> getSearchMap()
    {
        return searchMap;
    }

    public void setSearchMap(Map<String, String> searchMap)
    {
        this.searchMap = new TreeMap<String, String>(new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2) * -1;
            }
        });
        this.searchMap.putAll(searchMap);
    }

    public String getLastPath()
    {
        return lastPath;
    }

    public void setLastPath(String lastPath)
    {
        this.lastPath = lastPath;
    }

    public String getLastFile()
    {
        return lastFile;
    }

    public void setLastFile(String lastFile)
    {
        this.lastFile = lastFile;
    }
}
