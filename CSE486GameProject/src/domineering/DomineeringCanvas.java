/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package domineering;
import java.awt.*;
import java.awt.event.*;

import game.*;

public class DomineeringCanvas extends GameCanvas {
	public static final long serialVersionUID = 0;
	public static final int SQR_SZ = 26;
	public static final int LEFT = 20;
	public static final int TOP = 20;
	private int cnt = 0;
	
	public int getH()
	{ return DomineeringState.ROWS * SQR_SZ + 150; }
	public int getW()
	{ return DomineeringState.COLS * SQR_SZ + 150; }
	public void getMove(GameMove move, GameState state, Object waiting)
	{
		this.move = move;
		this.waiting = waiting;
		this.state = state;
		this.gettingMove = true;
		cnt = 0;
	}
	public DomineeringCanvas()
    { addMouseListener(this); }
    public void paint(Graphics g)
    {
    	DomineeringState brd = (DomineeringState)state;
    	if (brd == null) return;
    	for (int r=0; r<DomineeringState.ROWS; r++) {
    		String sRow = "" + (DomineeringState.ROWS-r-1);
    		String sCol = "" + r;
    		char [] chRowArray = sRow.toCharArray();
    		char [] chColArray = sCol.toCharArray();
    		g.setColor(Color.BLUE);
    		// LEFT, RIGHT, TOP, BOTTOM
        	g.drawChars(chRowArray, 0, chRowArray.length, LEFT/3, 									TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chRowArray, 0, chRowArray.length, LEFT + (DomineeringState.COLS+1)*SQR_SZ,	TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chColArray, 0, chColArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),			TOP/2);
        	g.drawChars(chColArray, 0, chColArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),			TOP+(DomineeringState.COLS+1)*SQR_SZ+4);
        	for (int c=0; c<DomineeringState.COLS; c++) {
        		square(g, c, DomineeringState.ROWS - 1 - r);
        		DomineeringMove lastMove = (DomineeringMove)move;
    			Color color = getColor(brd.board[r][c]);
    			boolean prevMove = r == lastMove.row2 && c == lastMove.col2 || r == lastMove.row1 && c == lastMove.col1; 
    			if (prevMove && brd.board[r][c] != DomineeringState.emptySym) {
					if (color == Color.WHITE)
						color = new Color(220, 220, 200);
					else if (color == Color.BLACK)
						color = new Color(100,100,100);
					else
						color = new Color((int)(color.getRed()*1.3), (int)(color.getGreen()*1.3), (int)(color.getBlue()*1.3));
    			}
    			drawSquare(g, c, DomineeringState.ROWS - r - 1, color);
        	}
    	}
    }
    public void mousePressed(MouseEvent mouseEvent) 
    { 
    	DomineeringMove mv = (DomineeringMove)move;
        int r = (mouseEvent.getY() - TOP) / (SQR_SZ+2);
        int c = (mouseEvent.getX() - LEFT) / (SQR_SZ+2);
        
        r = DomineeringState.ROWS - r - 1;
        cnt++;
        if (cnt == 1) {
        	mv.row1 = r;
        	mv.col1 = c;
        } else if (cnt == 2) {
        	mv.row2 = r;
        	mv.col2 = c;
        	cnt = 0;
        	ready.release();
        }
    }
    private void square(Graphics g, int x, int y)
    {
    	int lx = x * (SQR_SZ + 2) + LEFT;
    	int ly = y * (SQR_SZ + 2) + TOP;
    	g.setColor(Color.DARK_GRAY);
    	g.drawRect(lx, ly, SQR_SZ+2, SQR_SZ+2);
    	g.setColor(Color.LIGHT_GRAY);
    	g.fillRect(lx+1, ly+1, SQR_SZ, SQR_SZ);
    }
    private void drawSquare(Graphics g, int x, int y, Color color)
    {
    	int lx = x * (SQR_SZ + 2) + LEFT;
    	int ly = y * (SQR_SZ + 2) + TOP;
    	g.setColor(color);
    	g.fillRect(lx+1, ly+1, SQR_SZ, SQR_SZ);
    }
}
