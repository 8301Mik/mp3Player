package music;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

public class Song {
	private String title;
	private String artist;
	private String length;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;
    private long lengthMilliseconds;
    private int frameCount;
    
    
    public Song(String filePath) {
    	this.filePath = filePath;
    	try {
            // mit einem Mp3File Objekt kann spezifische Information aus der Mp3-Datei geholt werden 
    		mp3File = new Mp3File(filePath);
    		length = getConvertedSongLength();
    		frameCount = mp3File.getFrameCount();
    		frameRatePerMilliseconds = (double) frameCount / mp3File.getLengthInMilliseconds();
    		
    		File file = new File(filePath);
    		
    		lengthMilliseconds = mp3File.getLengthInMilliseconds();
            //AudioFile audioFile = AudioFileIO.read(new File(filePath));
    		AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            //lengthMilliseconds = (long) (file.length()/(frameCount*frameRatePerMilliseconds));
            
            if(tag!=null) {
            	title = tag.getFirst(FieldKey.TITLE);
            	artist = tag.getFirst(FieldKey.ARTIST);
            } else {
            	title = "N/A";
            	artist = "N/A";
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public String getTitle() {
        return title;
        }
    
    public String getArtist() {
        return artist;
        }
    
    private String getConvertedSongLength() {
    	//long hours = mp3File.getLengthInSeconds()/3600;
        long minutes = (mp3File.getLengthInSeconds()%3600) / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        //String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        return formattedTime;
    }
    
    public String getSongLength() {
        return length;
        }
    
    public String getPath() {
    	return filePath;
    }
    
    public double getFrameRatePerMilliseconds() {
    	return frameRatePerMilliseconds;
    }
    
    public long getLengthInMilliseconds() {
    	return (long)(lengthMilliseconds*0.859090909);
    }
    
    public Mp3File getMp3File() {
    	return mp3File;
    }

}
