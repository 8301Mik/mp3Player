package music;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Player extends PlaybackListener {
	private Player player;
	private static final Object playSignal = new Object();
	private GUI gui;
	FloatControl fc;
	private double timeCorrection = 1.166077082;
	public double getTimeCorrection() {
		return timeCorrection;
	}
	
    long startTime = System.currentTimeMillis();
    long currentRecordedTime = startTime;

	private int currentFrame;
	public void setCurrentFrame(int frame) {
		currentFrame = frame;
	}
	
	private long currentTimeInMilliseconds;
	public void setCurrentTimeInMilliseconds(long time) {
		currentTimeInMilliseconds = time;
	}
	
    private Song currentSong;
    public Song getCurrentSong() {
    	return currentSong;
    }
    
    private float currentVolume;
    public void setCurrentVolume(float volume) {
    	currentVolume = volume;
    };

    private float previousVolume;
    public void previousVolume(float volume) {
    	currentVolume = volume;
    };
    
    private AdvancedPlayer advancedPlayer;
	
    private boolean isFinished = false;
    public boolean isFinished() {
    	return isFinished;
    }
    public void setIsFinished(boolean bool) {
    	isFinished = bool;
    }
    
    private boolean isLooped;
    public void setIsLooped(boolean bool) {
    	isLooped = bool;
    }
    public boolean isLooped() {
    	return isLooped;
    }
    
    private boolean isPaused;
    public boolean isPaused() {
        return isPaused;
    }

    public AdvancedPlayer getAdvancedPlayer() {
    	return advancedPlayer;
    }
    
	public Player(GUI gui) {
		this.gui = gui;
	}
	
	public void loadSong(Song song) {
		isFinished = true;
		currentSong = song;
		stopSong();
	    startTime = System.currentTimeMillis();
	    currentRecordedTime = startTime;
		currentTimeInMilliseconds = 0;
		
		if (currentSong != null) {
			playSong();
		}
	}
	
	public void playSong() {
		if (currentSong == null)return;
		try {
			isFinished = false;
			FileInputStream fileInputStream = new FileInputStream(currentSong.getPath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			advancedPlayer = new AdvancedPlayer(bufferedInputStream);
			advancedPlayer.setPlayBackListener(this);
			
			startMusicThread();
			startFrameThread();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pauseSong() {
		if (advancedPlayer != null) {
			isPaused = true;
			stopSong();
		}
	}
	
	public void stopSong() {
		if (advancedPlayer != null && !isFinished) {
			advancedPlayer.stop();
			advancedPlayer.close();
			advancedPlayer = null;
		}
		if (advancedPlayer != null && isFinished) {
			advancedPlayer.close();
			advancedPlayer = null;
		}
	}
	
	
	
	public void startMusicThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					if(isPaused) {
						synchronized(playSignal) {
							isPaused = false;
							playSignal.notify();
						}
						advancedPlayer.play(currentFrame, Integer.MAX_VALUE);

						
					} else {
						advancedPlayer.play();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void startFrameThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(!isFinished && !isPaused) {
					try {
						
                        int calculatedFrame = (int) ((double) currentTimeInMilliseconds*timeCorrection*currentSong.getFrameRatePerMilliseconds());
						int minutes = (int) ((double)(currentTimeInMilliseconds*timeCorrection/1000))/60;
						int seconds = (int) ((double) currentTimeInMilliseconds*timeCorrection/1000) % 60;
						String formattedTime = String.format("%02d:%02d", minutes, seconds);
                        gui.changePlaybackSliderTime(formattedTime);
                        
                        gui.setPlaybackSliderValue(calculatedFrame);
                        System.out.println("Song: " + currentSong.getLengthInMilliseconds());
                        System.out.println("Actual: " + currentTimeInMilliseconds);
                        
                        // Was tun, wenn pausiert
						if (isPaused) {
							try {
								synchronized(playSignal) {
									
									playSignal.wait();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
						// Lied beenden
						if (currentTimeInMilliseconds == currentSong.getLengthInMilliseconds()) {
							if (isLooped) {
								currentFrame = 0;
								currentTimeInMilliseconds = 0;
								loadSong(currentSong);
								break;
							}
							isFinished = true;
							currentFrame = 0;
							currentTimeInMilliseconds = 0;
							break;
						}
						if (isLooped) {
							System.out.println("loop");
						}
						currentTimeInMilliseconds++;
						// Test, um eigentliche Länge des Liedes und wie es aufzählt zu testen
						/*System.out.println(currentTimeInMilliseconds);
						System.out.println(currentSong.getLengthInMilliseconds());*/
						Thread.sleep(1);	
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
    public void playbackStarted(PlaybackEvent evt) {
    	System.out.println("Song gestartet.");
    }
    
    public void playbackFinished(PlaybackEvent evt) {
    	// Es wird frame benötigt. evt.getFrame() gibt die aktuelle Position in Millisekunden an.
    	// Um also das derzeitige Frame zu erhalten, wird eine Rechnung benötigt.
        currentFrame += (int) ((double) evt.getFrame()*currentSong.getFrameRatePerMilliseconds());
    	System.out.println("Song beendet: " + evt.getFrame());
    	
    }
}
