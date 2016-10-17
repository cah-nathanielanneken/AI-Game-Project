/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import java.awt.*;
import java.awt.event.*;
import game.*;

public class Connect6Canvas extends GameCanvas {
	public static final long serialVersionUID = 0;
	public static final int SQR_SZ = 26;
	public static final int LEFT = 20;
	public static final int TOP = 20;
	private int cnt = 0;
	
	public int getH()
	{ return Connect6State.N * SQR_SZ + 150; }
	public int getW()
	{ return Connect6State.N * SQR_SZ + 150; }
	public void getMove(GameMove move, GameState state, Object waiting)
	{
		this.move = move;
		this.waiting = waiting;
		this.state = state;
		this.gettingMove = true;
		cnt = 0;
	}
	public Connect6Canvas()
    { addMouseListener(this); }
    public void paint(Graphics g)
    {
    	Connect6State brd = (Connect6State)state;

    	for (int r=0; r<Connect6State.N; r++) {
    		String s = "" + r;
    		char [] chArray = s.toCharArray();
    		g.setColor(Color.BLUE);
        	g.drawChars(chArray, 0, chArray.length, LEFT/3, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP/2);
        	g.drawChars(chArray, 0, chArray.length, LEFT + (Connect6State.N+2)*SQR_SZ, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP+(Connect6State.N+2)*SQR_SZ);
        	for (int c=0; c<Connect6State.N; c++) {
    			Connect6Move lastMove = (Connect6Move)move;
        		square(g, c, r);
        		if (brd.board[r][c] == 'B') {
        			Color color = Color.BLACK;
        			if (r == lastMove.row1 && c == lastMove.col1 ||
        				r == lastMove.row2 && c == lastMove.col2) {
        					color = new Color(125, 125, 125);
        			}
        			circle(g, c, r, color);
        		} else if (brd.board[r][c] == 'W') {
        			Color color = Color.WHITE;
        			if (r == lastMove.row1 && c == lastMove.col1 ||
            				r == lastMove.row2 && c == lastMove.col2) {
            					color = new Color(210, 210, 210);
            			}
        			circle(g, c, r, color);
        		}
        	}
    	}
    }
    public void mousePressed(MouseEvent mouseEvent) 
    { 
    	if (!gettingMove) {
    		return;
    	}
    	Connect6Move c6mv = (Connect6Move)move;
        int r = (mouseEvent.getY() - TOP) / (SQR_SZ+2);
        int c = (mouseEvent.getX() - LEFT) / (SQR_SZ+2);
        cnt++;
        if (state.numMoves == 0) {
        	c6mv.row1 = r;
        	c6mv.col1 = c;
        	c6mv.row2 = c6mv.col2 = 0;
        	cnt = 0;
        	gettingMove = false;
        	ready.release();
        } else if (cnt == 1) {
        	c6mv.row1 = r;
        	c6mv.col1 = c;
        } else if (cnt == 2) {
        	c6mv.row2 = r;
        	c6mv.col2 = c;
        	cnt = 0;
        	gettingMove = false;
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
    private void circle(Graphics g, int x, int y, Color color)
    {
    	int diam = (int)(SQR_SZ*0.8);
    	int ws = SQR_SZ - diam;
    	int lx = x * (SQR_SZ + 2) + LEFT;
    	int ly = y * (SQR_SZ + 2) + TOP;
    	g.setColor(color);
    	g.fillOval(lx+ws/2+1, ly+ws/2+1, diam, diam);
    }
}
