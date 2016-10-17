/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package hex;
import game.*;
import javax.swing.*;

public class HumanHexPlayer extends RandomHexPlayer {
	private GameFrame frame;
	private HexMove move = new HexMove(0,0);

	public String messageForOpponent(String opponent)
	{ return "I'm a humanist"; }
	public HumanHexPlayer(String nname)
	{
		super(nname);
		frame = new GameFrame(nickname, new HexCanvas());
		frame.setVisible(true);
		gameState = new HexState();
	}
	public GameMove getMove(GameState game, String lastMove)
	{
		System.out.println("ENTERING" + game);
		char ch = side == GameState.Who.HOME ? HexState.homeSym : HexState.awaySym;
		frame.setTitle("My move (" + ch + ")");
		if (!lastMove.equals("--") && frame.canvas.move != null) {
			((HexMove)frame.canvas.move).parseMove(lastMove);
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
		System.out.println("LEAVING" + game);
		frame.canvas.repaint();
		frame.setTitle("Waiting");
		return move;
	}
	public void endGame(int result)
	{
		char ch = side == GameState.Who.HOME ? HexState.homeSym : HexState.awaySym;
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
		GamePlayer p = new HumanHexPlayer("HUMAN");	// must have this name
		p.compete(args);
	}

}
