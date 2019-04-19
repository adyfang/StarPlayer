package com.starplayer.cache;

import java.io.File;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

public class VideoFileFilter extends FileFilter
{

    @Override
    public boolean accept(File file)
    {
        boolean isShow = false;
        if (file.isDirectory())
        {
            isShow = true;
        }
        else
        {
            for (String suffix : PlayerCache.FILE_SUFFIX)
            {
                if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(suffix))
                {
                    isShow = true;
                    break;
                }
            }
        }
        return isShow;
    }

    @Override
    public String getDescription()
    {
        StringBuffer desc = new StringBuffer("Video(");
        int i = 0;
        int total = PlayerCache.FILE_SUFFIX.size();
        for (String suffix : PlayerCache.FILE_SUFFIX)
        {
            desc.append("*." + suffix);
            if (i++ < total - 1)
            {
                desc.append(", ");
            }
        }
        desc.append(")");
        return desc.toString();
    }
}
