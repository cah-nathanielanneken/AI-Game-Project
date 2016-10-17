/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package domineering;
import game.*;

import javax.swing.*;


public class HumanDomineeringPlayer extends RandomDomineeringPlayer {
	private GameFrame frame;
	private DomineeringMove move = new DomineeringMove(0,0,0,0);

	public String messageForOpponent(String opponent)
	{ return "I'm a humanist"; }
	public HumanDomineeringPlayer(String nname)
	{
		super(nname);
		frame = new GameFrame(nickname, new DomineeringCanvas());
		frame.setVisible(true);
		gameState = new DomineeringState();
	}
	public GameMove getMove(GameState game, String lastMove)
	{
		char ch = side == GameState.Who.HOME ? DomineeringState.homeSym : DomineeringState.awaySym;
		frame.setTitle("My move (" + ch + ")");
		if (!lastMove.equals("--") && frame.canvas.move != null) {
			((DomineeringMove)frame.canvas.move).parseMove(lastMove);
		}

		boolean OK;
		do {
			frame.canvas.setBoard(game);
			frame.canvas.repaint();
			frame.canvas.getMove(move, game, this);
			
			try {
				frame.canvas.ready.acquire();
			}
			catch (Exception e) { 		}
			OK = game.moveOK(move);
			if (!OK) {
				JOptionPane.showMessageDialog(null, "Bad move" + move.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} while (!OK);

		game.makeMove(move);
		frame.canvas.repaint();
		frame.setTitle("Waiting");
		return move;
	}
	public void endGame(int result)
	{
		char ch = side == GameState.Who.HOME ? DomineeringState.homeSym : DomineeringState.awaySym;
		if (result == 1) {
			frame.setTitle("Won (" + ch + ")");
		} else if (result == -1) {
			frame.setTitle("Loss (" + ch + ")");
		} else {
			frame.setTitle("Draw (" + ch + ")");
		}
	}
	public static void main(String[] args)
	{
		GamePlayer p = new HumanDomineeringPlayer("HUMAN");	// must have this name
		p.compete(args);
	}

}
