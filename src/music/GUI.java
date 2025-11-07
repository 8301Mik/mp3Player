package music;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame {
	private JPanel buttons;
	private JSlider playbackSlider;
	private JButton loadSongButton;
	private JFileChooser fileChooser;
	private JLabel labelBeginning;
	private JLabel labelEnd;
	Hashtable<Integer,JLabel> labelTable;
    private Color songInformationColor = new Color(255, 110, 149);
	private Player player;
	private JPanel songInformationPanel;
	private JLabel songTitle;
	private JLabel songArtist;
    boolean isMuted = false;

	public GUI() {
		super("Musikspieler");
		setSize(600,800);
		setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        player = new Player(GUI.this);
        
        addComponents();
	}
	
	private void addComponents() {
		addButtons();
		addPlaybackSlider();
		addLoadSongButton();
		addFileChooser();
		addSongInformation();
	}
	
	private void addSongInformation() {
		songInformationPanel = new JPanel();
		songInformationPanel.setBounds(10,300,getWidth()-25,80);
		songInformationPanel.setBackground(Color.black);
		songInformationPanel.setBorder(new LineBorder(songInformationColor,5));
		
        //add title
        songTitle = new JLabel("Title");
        songTitle.setFont(new Font("Medodica", Font.BOLD,35));
        songTitle.setBounds(50,280,getWidth()-25,100);
        songTitle.setForeground(songInformationColor);
        add(songTitle);
        
        //add artist
        songArtist = new JLabel("Artist");
        songArtist.setFont(new Font("Medodica", Font.BOLD,20));
        songArtist.setBounds(50,310,getWidth()-25,100);
        songArtist.setForeground(songInformationColor);
        add(songArtist);

		add(songInformationPanel);
	}
	
	private void addLoadSongButton() {
		loadSongButton = new JButton("Load song");
		loadSongButton.setVisible(true);
		loadSongButton.setBackground(Color.PINK);
		loadSongButton.setBounds(0,0,200,100);
		loadSongButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(GUI.this);
                File selectedFile = fileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                	Song song = new Song(selectedFile.getPath());
                	/*if (player.getAdvancedPlayer()!= null) {
                		player.setIsFinished(true);
                		player.stopSong();
                	}*/
                	if (!player.isFinished()) {
                		player.setIsFinished(true);
                		player.stopSong();
                	}
                	updateSongTitleAndArtist(song); 
                    player.setCurrentFrame(0);
                    player.loadSong(song);
                    updatePlaybackSlider(song);
            		play();
                }
			}
			
		});
		add(loadSongButton);
	}
	
    private void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
    }
	
	private void addPlaybackSlider() {
		playbackSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
		playbackSlider.setVisible(true);
		playbackSlider.setBackground(Color.BLACK);
		playbackSlider.setBounds(10,400,getWidth()-25,80);
		playbackSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!player.isPaused()) {
					player.pauseSong();
				}
 			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				JSlider source = (JSlider) e.getSource();
				int frame = source.getValue();
				player.setCurrentFrame(frame);
                player.setCurrentTimeInMilliseconds((int)(frame/((double)player.getTimeCorrection()*player.getCurrentSong().getFrameRatePerMilliseconds())));
                player.pauseSong();
                player.playSong();
                play();
			}
		});
		
		playbackSlider.setMaximum(Integer.MAX_VALUE);
		labelTable = new Hashtable<>();
		
		labelBeginning = new JLabel("00:00");
		labelBeginning.setFont(new Font("Medodica", Font.BOLD,18));
		labelBeginning.setForeground(songInformationColor);
		
		labelEnd = new JLabel("00:00");
		labelEnd.setFont(new Font("Medodica", Font.BOLD,18));
		labelEnd.setForeground(songInformationColor);
		
		labelTable.put(0, labelBeginning);
		labelTable.put(Integer.MAX_VALUE, labelEnd);

		playbackSlider.setLabelTable(labelTable);
		playbackSlider.setPaintLabels(true);
		
		add(playbackSlider);
	}
	
	public void updatePlaybackSlider(Song song) {
		playbackSlider.setMaximum(song.getMp3File().getFrameCount());
		labelTable = new Hashtable<>();
		
		labelEnd = new JLabel(song.getSongLength());
		labelEnd.setFont(new Font("Medodica", Font.BOLD,18));
		labelEnd.setForeground(songInformationColor);
		
		labelTable.put(0, labelBeginning);
		labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

		playbackSlider.setLabelTable(labelTable);
		playbackSlider.setPaintLabels(true);
	}
	
    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }
    
    public void changePlaybackSliderTime(String time) {
        labelBeginning.setText(time);
    }
    
	private void addButtons() {
		buttons = new JPanel();
		buttons.setBounds(10,500,getWidth()-25,80);
		buttons.setBackground(Color.black);
		buttons.setOpaque(true);
		add(buttons);
		
		// 0
        JButton shuffle = new JButton(loadImage("src/assets/shuffle.png"));
        shuffle.setBorderPainted(false);
        shuffle.setBackground(null);
        shuffle.setContentAreaFilled(false); 
        buttons.add(shuffle);
        
        // 1
        JButton prev = new JButton(loadImage("src/assets/prev.png"));
        prev.setBorderPainted(false);
        prev.setBackground(null);
        prev.setContentAreaFilled(false); 
        buttons.add(prev);
        
        /*// 2
        JButton prev2 = new JButton(loadImage("src/assets/prev2.png"));
        prev2.setBorderPainted(false);
        prev2.setBackground(null);
        prev2.setContentAreaFilled(false); 
        buttons.add(prev2);*/
        
        // 2
        JButton play = new JButton(loadImage("src/assets/play.png"));
        play.setBorderPainted(false);
        play.setBackground(null);
        play.setContentAreaFilled(false); 
        play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			if (player.getCurrentSong() == null) return;
			player.playSong();	
			play();
			} 	
        });
        buttons.add(play);
        
        // 3
        JButton pause = new JButton(loadImage("src/assets/pause.png"));
        pause.setBorderPainted(false);
        pause.setBackground(null);
        pause.setContentAreaFilled(false); 
        pause.setVisible(false);
        pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				player.pauseSong();
				pause();
			}
        });
        buttons.add(pause);
        
        /*// 5
        JButton next2 = new JButton(loadImage("src/assets/next2.png"));
        next2.setBorderPainted(false);
        next2.setBackground(null);
        next2.setContentAreaFilled(false); 
        buttons.add(next2);*/
        
        // 4
        JButton next = new JButton(loadImage("src/assets/next.png"));
        next.setBorderPainted(false);
        next.setBackground(null);
        next.setContentAreaFilled(false); 
        buttons.add(next);
        
        // 5
        JButton loop = new JButton(loadImage("src/assets/loop.png"));
        loop.setBorderPainted(false);
        loop.setBackground(null);
        loop.setContentAreaFilled(false); 
        loop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Betätigt");
				if(!player.isLooped()) {
					player.setIsLooped(true);
				} else {
					player.setIsLooped(false);
				}
			}
        });
        buttons.add(loop);
    
        //6
        JButton audio = new JButton(loadImage("src/assets/audio.png"));
        audio.setBorderPainted(false);
        audio.setBackground(null);
        audio.setContentAreaFilled(false); 
        audio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isMuted) {
					audio.setIcon(loadImage("src/assets/noAudio.png"));
					isMuted = true;
				} else {
					audio.setIcon(loadImage("src/assets/audio.png"));
					isMuted = false;
				}
				
			if (player.getCurrentSong() == null) return;
			player.playSong();	
			play();
			} 	
        });
        buttons.add(audio);
	}
	
	private void addFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(""));
        // description: MP3     extension: mp3
        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));
    }
	
	private void play() {
		JButton play = (JButton) buttons.getComponent(2);
		JButton pause = (JButton) buttons.getComponent(3);
		
		play.setVisible(false);
		play.setEnabled(false);
		
		pause.setVisible(true);
		pause.setVisible(true);
	}
	
	private void pause() {
		JButton play = (JButton) buttons.getComponent(2);
		JButton pause = (JButton) buttons.getComponent(3);
		
		play.setVisible(true);
		play.setEnabled(true);
		
		pause.setVisible(false);
		pause.setVisible(false);
	}

	private ImageIcon loadImage(String imagePath) {
		try {
			BufferedImage image = ImageIO.read(new File(imagePath));
			return new ImageIcon(image);
		} catch (Exception e) {
			e.printStackTrace();
		} return null;
	}
}
