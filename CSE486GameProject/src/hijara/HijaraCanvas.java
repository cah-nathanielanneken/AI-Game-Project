/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hijara;
import java.awt.*;
import java.awt.event.*;

import game.*;

public class HijaraCanvas extends GameCanvas {
	public static final long serialVersionUID = 0;
	public static final int SQR_SZ = 100;
	public static final int THIRD_SQR_SZ = SQR_SZ / 3;
	public static final int DISK_SZ = SQR_SZ / 5;
	
	public static final int LEFT = 20;
	public static final int TOP = 20;
	
	public int getH()
	{ return HijaraState.ROWS * SQR_SZ + 150; }
	public int getW()
	{ return HijaraState.COLS * SQR_SZ + 150; }
	public void getMove(GameMove move, GameState state, Object waiting)
	{
		this.move = move;
		this.waiting = waiting;
		this.state = state;
		this.gettingMove = true;
	}
	public HijaraCanvas()
    { addMouseListener(this); }
	private void renderDigit(Graphics g, int x, int y, char digit,
							char color, boolean dim)
	{
    	int offset = (int)(THIRD_SQR_SZ * 0.2);
    	char [] ary = {digit};
    	g.setColor(Color.BLACK);
    	g.drawChars(ary, 0, 1, x, y);
    	if (color == 'B') {
    		Color b = Color.BLUE;
    		if (dim)
    			b = b.darker().darker();
    		g.setColor(b);
        	circle(g, x-offset, y, DISK_SZ);
    	} else if (color == 'Y') {
    		Color b = Color.YELLOW;
    		if (dim) b = b.darker().darker();
    		g.setColor(b);
    	    circle(g, x-offset, y, DISK_SZ);
    	}
	}
    private void renderSquare(Graphics g, int x, int y, StringBuffer s, boolean dim)
    {
    	int lx = x * (SQR_SZ + 2) + LEFT;
    	int ly = y * (SQR_SZ + 2) + TOP;
    	g.setColor(Color.LIGHT_GRAY);
    	char emptyChar = HijaraState.emptySym;
    	g.fillRect(lx+1, ly+1, SQR_SZ, SQR_SZ);
    	renderDigit(g, lx+2*THIRD_SQR_SZ, ly+2*THIRD_SQR_SZ, '4', s.charAt(3), dim);
    	if (s.charAt(3)!= emptyChar) dim = false;
    	renderDigit(g, lx+1*THIRD_SQR_SZ, ly+2*THIRD_SQR_SZ, '3', s.charAt(2), dim);
    	if (s.charAt(2)!= emptyChar) dim = false;
    	renderDigit(g, lx+2*THIRD_SQR_SZ, ly+1*THIRD_SQR_SZ, '2', s.charAt(1), dim);
    	if (s.charAt(1)!= emptyChar) dim = false;
    	renderDigit(g, lx+1*THIRD_SQR_SZ, ly+1*THIRD_SQR_SZ, '1', s.charAt(0), dim);
    	if (s.charAt(0)!= emptyChar) dim = false;
    }
    public void paint(Graphics g)
    {
    	HijaraState st = (HijaraState)state;

    	HijaraMove lastMove = (HijaraMove)move;

    	for (int r=0; r<HijaraState.ROWS; r++) {
    		String s = "" + r;
    		char [] chArray = s.toCharArray();
    		g.setColor(Color.BLUE);
        	g.drawChars(chArray, 0, chArray.length, LEFT/3, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP/2);
        	g.drawChars(chArray, 0, chArray.length, LEFT + (HijaraState.COLS+2)*SQR_SZ, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP+(HijaraState.COLS+2)*SQR_SZ);

        	g.drawString("BLUE: " + st.score[0], 30, 4*SQR_SZ+TOP+20);
        	g.drawString("YELLOW: " + st.score[1], 30, 4*SQR_SZ+TOP+40);
        	g.drawChars(chArray, 0, chArray.length, LEFT + (HijaraState.COLS+2)*SQR_SZ, TOP + SQR_SZ/2 + r*(SQR_SZ+2));
        	g.drawChars(chArray, 0, chArray.length, LEFT + SQR_SZ/2 + r*(SQR_SZ+2),TOP+(HijaraState.COLS+2)*SQR_SZ);
        	
        	for (int c=0; c<HijaraState.COLS; c++) {
        		renderSquare(g, c, r, st.board[r][c], r==lastMove.row && c==lastMove.col);
        	}
    	}
    }
    public void mousePressed(MouseEvent mouseEvent) 
    { 
    	HijaraMove mv = (HijaraMove)move;
        int r = (mouseEvent.getY() - TOP) / (SQR_SZ+2);
        int c = (mouseEvent.getX() - LEFT) / (SQR_SZ+2);
        mv.row = r;
        mv.col = c;
       	ready.release();
    }
    private void circle(Graphics g, int x, int y, int diam)
    {
    	g.fillOval(x, y, diam, diam);
    }
}
