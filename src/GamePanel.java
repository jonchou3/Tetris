import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.JPanel;

public class GamePanel extends JPanel{

	static final int SCREEN_WIDTH = 350;
	static final int SCREEN_HEIGHT = 735;
	static final int UNIT_SIZE = 35;
	static int DELAY = 400;
	static final int REFRESH_RATE = 5;
	static final int DIFFICULTY = 40000;
	int newPieceX[] = new int[4];
	int newPieceY[] = new int[4];
	int score = 0;
	boolean board[][] = new boolean[SCREEN_WIDTH/UNIT_SIZE][SCREEN_HEIGHT/UNIT_SIZE];
	int newPieceNum;
	int incomingPieceNum;
	int storedPiece;
	boolean running = false;
	Timer timer;
	Timer moveTimer;
	Timer difficultyTimer;
	Random random;
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH+SCREEN_WIDTH/2, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	public void startGame() {
		newPiece();
		running = true;
		timer = new Timer(REFRESH_RATE, new ActionListener() {
			@Override
	        public void actionPerformed(ActionEvent e) {
	            if(running) {
	            	checkTopCollision();
	            	checkPiece();
	            	checkBoard();
		        }
	            repaint();
	        }
	    });
		timer.start();
		moveTimer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    move();
                }
            }
        });
        moveTimer.start();
        difficultyTimer = new Timer(DIFFICULTY, new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                if (running && DELAY > 10) {
                    DELAY -= 30;
                }
            }
        });
        difficultyTimer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if(running) {
			for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			g.drawLine(SCREEN_WIDTH,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			g.fillRect(SCREEN_WIDTH,0,SCREEN_WIDTH/2,SCREEN_HEIGHT);
			//board state
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if(board[i][j]) {
						g.setColor(Color.gray);
						g.fillRect(i*UNIT_SIZE+1,j*UNIT_SIZE+1,UNIT_SIZE-1,UNIT_SIZE-1);
					}
				}
			}
			//current piece
			g.setColor(Color.blue);
			for(int i = 0; i < 4; i++) {
				//System.out.println(newPieceX[i] + " " + newPieceY[i]);
				g.fillRect(newPieceX[i]*UNIT_SIZE+1,newPieceY[i]*UNIT_SIZE+1,UNIT_SIZE-1,UNIT_SIZE-1);
			}
			//score
			g.setColor(Color.red);
			g.setFont( new Font("Garamond", Font.BOLD,20));
			FontMetrics metrics1 = getFontMetrics(g.getFont());
			g.drawString("Score: " + score, SCREEN_WIDTH+(SCREEN_WIDTH - metrics1.stringWidth("Score: " + score))/4, g.getFont().getSize());
			//incoming piece
			g.drawString("Incoming Piece", (int)(SCREEN_WIDTH*1.1),(int)(SCREEN_HEIGHT/8));
			g.setColor(Color.blue);
			drawPiece(g,incomingPieceNum,(int)(SCREEN_HEIGHT/8)+UNIT_SIZE);
			//stored piece
			g.setColor(Color.red);
			g.drawString("Stored Piece", (int)(SCREEN_WIDTH*1.1),(int)(SCREEN_HEIGHT/2));
			g.setColor(Color.blue);
			drawPiece(g,storedPiece,(int)(SCREEN_HEIGHT/2)+UNIT_SIZE);
			
		}
		else {
			gameOver(g);
		}
	}
	
	public void drawPiece(Graphics g, int pieceNum, int yOffset) {
		int pieceX[] = new int[4];
		int pieceY[] = new int[4];
		switch(pieceNum) {
		case 0: //I piece
			pieceX = new int[]{0,1,2,3};
			pieceY = new int[]{0,0,0,0};
			break;
		case 1: //L piece
			pieceX = new int[]{0,0,1,2};
			pieceY = new int[]{0,1,1,1};
			break;
		case 2: //J piece
			pieceX = new int[]{2,0,1,2};
			pieceY = new int[]{0,1,1,1};
			break;
		case 3: //O piece
			pieceX = new int[]{0,1,0,1};
			pieceY = new int[]{0,0,1,1};
			break;
		case 4: //S piece
			pieceX = new int[]{0,1,1,2};
			pieceY = new int[]{1,1,0,0};
			break;
		case 5: //Z piece
			pieceX = new int[]{0,1,1,2};
			pieceY = new int[]{0,0,1,1};
			break;
		case 6: //T piece
			pieceX = new int[]{1,0,1,2};
			pieceY = new int[]{0,1,1,1};
			break;
		}
		for(int i = 0; i < 4; i++) {
			//System.out.println(newPieceX[i] + " " + newPieceY[i]);
			g.fillRect(pieceX[i]*UNIT_SIZE+(int)(SCREEN_WIDTH*1.05),pieceY[i]*UNIT_SIZE+1+yOffset,UNIT_SIZE-1,UNIT_SIZE-1);
		}
	}
	public void newPiece() {
		newPieceNum = incomingPieceNum;
		incomingPieceNum = random.nextInt(7);
		switch(newPieceNum) {
		case 0: //I piece
			newPieceX = new int[]{0,1,2,3};
			newPieceY = new int[]{0,0,0,0};
			break;
		case 1: //L piece
			newPieceX = new int[]{0,0,1,2};
			newPieceY = new int[]{0,1,1,1};
			break;
		case 2: //J piece
			newPieceX = new int[]{2,0,1,2};
			newPieceY = new int[]{0,1,1,1};
			break;
		case 3: //O piece
			newPieceX = new int[]{0,1,0,1};
			newPieceY = new int[]{0,0,1,1};
			break;
		case 4: //S piece
			newPieceX = new int[]{0,1,1,2};
			newPieceY = new int[]{1,1,0,0};
			break;
		case 5: //Z piece
			newPieceX = new int[]{0,1,1,2};
			newPieceY = new int[]{0,0,1,1};
			break;
		case 6: //T piece
			newPieceX = new int[]{1,0,1,2};
			newPieceY = new int[]{0,1,1,1};
			break;
		}
		for(int i = 0; i < 4; i++) {
			newPieceX[i] += SCREEN_WIDTH/(UNIT_SIZE*2)-2;
		}
	}
	public void rotateClockwise() {
	    int[][] rotatedPiece = new int[4][2];
	    int pivotX = newPieceX[2];
	    int pivotY = newPieceY[2];
	    
	    for (int i = 0; i < 4; i++) {
	        int newX = pivotX - (newPieceY[i] - pivotY);
	        int newY = pivotY + (newPieceX[i] - pivotX);
	        
	        rotatedPiece[i][0] = newX;
	        rotatedPiece[i][1] = newY;
	    }
	    
	    boolean validRotation = true;
	    for (int i = 0; i < 4; i++) {
	        int newX = rotatedPiece[i][0];
	        int newY = rotatedPiece[i][1];

	        if (newX < 0 || newX >= SCREEN_WIDTH / UNIT_SIZE ||
	            newY < 0 || newY >= SCREEN_HEIGHT / UNIT_SIZE ||
	            board[newX][newY]) {
	            validRotation = false;
	            break;
	        }
	    }

	    if (validRotation) {
	        for (int i = 0; i < 4; i++) {
	            newPieceX[i] = rotatedPiece[i][0];
	            newPieceY[i] = rotatedPiece[i][1];
	        }
	    }

	}
	public void rotateCounterClockwise() {
	    int[][] rotatedPiece = new int[4][2];
	    int pivotX = newPieceX[2];
	    int pivotY = newPieceY[2];
	    
	    for (int i = 0; i < 4; i++) {
	        int newX = pivotX + (newPieceY[i] - pivotY);
	        int newY = pivotY - (newPieceX[i] - pivotX);
	        
	        rotatedPiece[i][0] = newX;
	        rotatedPiece[i][1] = newY;
	    }
	    
	    boolean validRotation = true;
	    for (int i = 0; i < 4; i++) {
	        int newX = rotatedPiece[i][0];
	        int newY = rotatedPiece[i][1];

	        if (newX < 0 || newX >= SCREEN_WIDTH / UNIT_SIZE ||
	            newY < 0 || newY >= SCREEN_HEIGHT / UNIT_SIZE ||
	            board[newX][newY]) {
	            validRotation = false;
	            break;
	        }
	    }

	    if (validRotation) {
	        for (int i = 0; i < 4; i++) {
	            newPieceX[i] = rotatedPiece[i][0];
	            newPieceY[i] = rotatedPiece[i][1];
	        }
	    }

	}
	public void move() {
		for(int i = 0; i < 4; i++) {
			newPieceY[i]++;
		}
	}
	public void checkPiece() {
		for(int i = 0; i < 4; i++) {
			//check bottom border and check board
			if(newPieceY[i] == SCREEN_HEIGHT/UNIT_SIZE || board[newPieceX[i]][newPieceY[i]]) {
				for(int j = 0; j < 4; j++) {
					if(newPieceY[j]-1 >= 0)
						board[newPieceX[j]][newPieceY[j]-1] = true;
				}
				newPiece();
				break;
			}
		}
	}
	public void checkTopCollision() {
		for(int i = 0; i < 4; i++) {
			if(newPieceY[i] <= 1 && board[newPieceX[i]][newPieceY[i]]) {
				running = false;
			}
		}
	}
	public void checkBoard() {
	    for (int i = board[0].length - 1; i >= 0; i--) {
	        boolean rowIsFull = true;
	        for (int j = 0; j < board.length; j++) {
	            if (!board[j][i]) {
	                rowIsFull = false;
	                break;
	            }
	        }
	        if (rowIsFull) {
	            // Remove the full row and shift down all rows above it
	            for (int k = i; k > 0; k--) {
	                for (int j = 0; j < board.length; j++) {
	                    board[j][k] = board[j][k - 1];
	                }
	            }
	            // Clear the top row
	            for (int j = 0; j < board.length; j++) {
	                board[j][0] = false;
	            }
	            // Adjust the index to recheck the current row after shifting down
	            i++;
	            score++;
	        }
	    }
	}
	public void storePiece() {
		int temp = newPieceNum;
		if(storedPiece >= 0)
			newPieceNum = storedPiece;
		storedPiece = temp;
		newPiece();
	}
	public void gameOver(Graphics g) {
		//Score text
		g.setColor(Color.red);
		g.setFont( new Font("Garamond", Font.BOLD,40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score))/2, g.getFont().getSize());
		//Game Over text
		g.setColor(Color.red);
		g.setFont( new Font("Garamond", Font.BOLD,75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
	}
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT://left
				boolean boundLeft = false;
				for(int i : newPieceX) {
					if(i <= 0) {
						boundLeft = true;
						break;
					}
				}
				if(!boundLeft) {
					for(int i = 0; i < 4; i++) {
						newPieceX[i]--;
					}
						
				}
				break;
			case KeyEvent.VK_RIGHT://right
				boolean boundRight = false;
				for(int i : newPieceX) {
					if(i >= (SCREEN_WIDTH/UNIT_SIZE)-1) {
						boundRight = true;
						break;
					}
				}
				if(!boundRight) {
					for(int i = 0; i < 4; i++) {
						newPieceX[i]++;
					}
						
				}
				break;
			case KeyEvent.VK_UP://rotate clockwise
				rotateClockwise();
				break;
			case KeyEvent.VK_DOWN://down
				for(int i = 0; i < 4; i++) {
					newPieceY[i]++;
				}
				break;
			case KeyEvent.VK_X://rotate clockwise
				rotateClockwise();
				//increment position +1
				break;
			case KeyEvent.VK_Z://rotate counterclockwise
				rotateCounterClockwise();
				//increment position -1
				break;
			case KeyEvent.VK_C://store piece
				storePiece();
				break;
			}
			
		}
	}
}
