/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect6;
import game.*;

public class HumanConnect6Player extends RandomConnect6Player {
	private GameFrame frame;
	private Connect6Move move = new Connect6Move(0,0,0,0);

	public HumanConnect6Player(String nname)
	{
		super(nname);
		frame = new GameFrame(nickname, new Connect6Canvas());
		frame.setVisible(true);
		gameState = new Connect6State();
	}
	public GameMove getMove(GameState game, String lastMove)
	{
		if (!lastMove.equals("--") && frame.canvas.move != null) {
			((Connect6Move)frame.canvas.move).parseMove(lastMove);
		}
		frame.canvas.setBoard(game);
		frame.canvas.repaint();
		frame.canvas.getMove(move, game, this);
		try {
			frame.canvas.ready.acquire();
		}
		catch (Exception e) { 		}
		game.makeMove(move);
		frame.canvas.repaint();
		return move;
	}
	public static void main(String[] args)
	{
		GamePlayer p = new HumanConnect6Player("HUMAN");	// must have this name
		p.compete(args);
	}

}
