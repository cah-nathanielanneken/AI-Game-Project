/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hex;
import java.awt.*;
import java.awt.event.*;

import game.*;

public class HexCanvas extends GameCanvas {
	public static final long serialVersionUID = 0;
	public static final int HEX_RAD = 25;
	public static final int HEX_EDGE = HEX_RAD;
	public static final double THIRTY_DEGREES = Math.toRadians(30);
	public static final int HEX_PERP = (int)(HEX_RAD*Math.cos(THIRTY_DEGREES));
	public static final int HEX_LONG_DIAM = 2 * HEX_RAD;
	public static final int HEX_SHORT_DIAM = 2 * HEX_PERP;
	public static final int LEFT = 20;
	public static final int TOP = 20;
	public final int [] ptX = new int [6];
	public final int [] ptY = new int [6];
	
	private double getX(int row, int col)
	{
		return LEFT + HEX_PERP * (row + 1) + HEX_SHORT_DIAM * col;
	}
	private double getY(int row, int col)
	{
		return TOP + HEX_RAD + (HEX_RAD + HEX_EDGE / 2.0) * row; 
	}
	public double dist(int row, int col, int mx, int my)
	{
		double dx = getX(row, col) - mx;
		double dy = getY(row, col) - my;
		return Math.sqrt(dx*dx + dy*dy);
	}
	public int getH()
	{
		int nEvens = HexState.N / 2;
		int nOdds = HexState.N - nEvens;
		return  nEvens * HEX_EDGE + nOdds * HEX_LONG_DIAM + 100;
	}
	public int getW()
	{ return (3 * HexState.N - 1) * HEX_PERP + 150; }
	public void getMove(GameMove move, GameState state, Object waiting)
	{
		this.move = move;
		this.waiting = waiting;
		this.state = state;
		this.gettingMove = true;
	}
	public HexCanvas()
    { addMouseListener(this); }
    private void renderHex(Graphics g, int r, int c, char who, boolean lastMove)
    {
    	double cx = getX(r, c);
    	double cy = getY(r, c);
    	for (int i=0; i<6; i++) {
    		double angle = Math.toRadians(i * 60 + 30);
    		ptX[i] = (int)(HEX_RAD * Math.cos(angle) + cx);
    		ptY[i] = (int)(HEX_RAD * Math.sin(angle) + cy);
    	}
    	if (who == HexState.homeSym) {
    		g.setColor(Color.WHITE);
    		g.fillPolygon(ptX, ptY, 6);
    	} else if (who == HexState.awaySym) {
    		g.setColor(Color.BLACK);
    		g.fillPolygon(ptX, ptY, 6);
    	} else {
    		g.setColor(Color.LIGHT_GRAY);
    		g.drawPolygon(ptX, ptY, 6);
    	}
    }
    public void paint(Graphics g)
    {
    	HexState st = (HexState)state;
    	HexMove lastMove = (HexMove)move;
    	g.setColor(Color.GRAY);
    	g.fillRect(0,0,1000,1000);
    	
    	g.setColor(Color.WHITE);
    	int H = HEX_RAD;
    	g.fillRect((int)getX(0,0), (int)getY(0,0)-2*H,
    				(int)getX(0,HexState.N-1), (int)getY(0,HexState.N-1)-H);
    	g.fillRect((int)getX(HexState.N-1,0), (int)getY(HexState.N-1,0)+H,
				(int)getX(2,HexState.N-1), (int)getY(0,HexState.N-1)+2*H/2);
    	for (int r=0; r<HexState.N; r++) {
        	for (int c=0; c<HexState.N; c++) {
        		renderHex(g, r, c, st.board[r][c], r==lastMove.row && c==lastMove.col);
        	}
    	}
    }
    public void mousePressed(MouseEvent mouseEvent) 
    { 
    	HexMove mv = (HexMove)move;
    	int mx = mouseEvent.getX();
    	int my = mouseEvent.getY();
    	mv.row = mv.col = -1;
    	double minDist = 10000;
    	for (int r=0; r<HexState.N; r++) {
    		for (int c=0; c<HexState.N; c++) {
    			double offset = dist(r, c, mx, my); 
    			if (offset < HEX_PERP && offset < minDist) {
    				mv.row = r;
    				mv.col = c;
    				minDist = offset;
    			}
    		}
    	}
       	ready.release();
    }
}
