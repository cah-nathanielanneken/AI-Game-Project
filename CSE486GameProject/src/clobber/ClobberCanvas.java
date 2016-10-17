/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package clobber;
import java.awt.*;
import java.awt.event.*;

import game.*;

public class ClobberCanvas extends GameCanvas {
	public static final long serialVersionUID = 0;
	public static final int SQR_SZ = 26;
	public static final int LEFT = 20;
	public static final int TOP = 20;
	private int cnt = 0;
	
	public int getH()
	{ return ClobberState.ROWS * SQR_SZ + 150; }
	public int getW()
	{ return ClobberState.COLS * SQR_SZ + 150; }
	public void getMove(GameMove move, GameState state, Object waiting)
	{
		this.move = move;
		this.waiting = waiting;
		this.state = state;
		this.gettingMove = true;
		cnt = 0;
	}
	public ClobberCanvas()
    { addMouseListener(this); }
    public void paint(Graphics g)
    {
    	ClobberState brd = (ClobberState)state;

    	for (int r=0; r<ClobberState.ROWS; r++) {
    		String s = "" + r;
    		char [] chArray = s.toCharArray();
    		g.setColor(Color.BLUE);
        	g.drawChars(chArray, 0, chArray.length, LEFT/3, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP/2);
        	g.drawChars(chArray, 0, chArray.length, LEFT + (ClobberState.COLS+2)*SQR_SZ, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP+(ClobberState.COLS+2)*SQR_SZ);
        	for (int c=0; c<ClobberState.COLS; c++) {
        		ClobberMove lastMove = (ClobberMove)move;
        		square(g, c, r);
        		if (brd.board[r][c] == 'R') {
        			Color color = Color.RED;
        			if (r == lastMove.row2 && c == lastMove.col2) {
        					color = new Color(125, 0, 0);
        			}
        			circle(g, c, r, color);
        		} else if (brd.board[r][c] == 'W') {
        			Color color = Color.WHITE;
        			if (r == lastMove.row2 && c == lastMove.col2) {
            					color = new Color(210, 210, 210);
            		}
        			circle(g, c, r, color);
        		}
        	}
    	}
    }
    public void mousePressed(MouseEvent mouseEvent) 
    { 
    	ClobberMove mv = (ClobberMove)move;
        int r = (mouseEvent.getY() - TOP) / (SQR_SZ+2);
        int c = (mouseEvent.getX() - LEFT) / (SQR_SZ+2);
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
