package musicalchairs;


import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class code {

	static class Player {
		private String name;
		private final Lock lock = new ReentrantLock();
		boolean gotChair = false;

		public Player(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public boolean checkLock(Player player) {
			Boolean myLock = false;
			Boolean yourLock = false;
			try {
				myLock = lock.tryLock();
				yourLock = player.lock.tryLock();
			} finally {
				if (!(myLock && yourLock)) {
					if (myLock) {
						lock.unlock();
					}
					if (yourLock) {
						player.lock.unlock();
					}
				}
			}

			return myLock && yourLock;
		}

		public void dance(Player player, Vetor vec, ArrayList<String> register, int nRounds) {

			int num = (int) (Math.random() * vec.getVec().size());
			if (checkLock(player) && checkChair(vec.getVec(), num)) {
				try {
					vec.getVec().set(num, player.name);
					System.out.println("The player " + this.name + " got the chair number " + num);
					register.add(0, player.name);

				} finally {
					lock.unlock();
					player.lock.unlock();
				}

			} else if (checkChair(vec.getVec(), num)) {
				System.out.println("The player " +this.name + " tried to pick the chair " + num + ", but it was occupied already");

			}

			else {

				if (!allOccupied(vec.getVec())) {
					System.out.println(
							"The player " + this.name + " tried to pick the chair " + num + ", but it was occupied already");
					final Player novo = new Player(player.name);
					new Thread(new DanceLoop(novo, vec, register, nRounds)).start();
				} else {
					System.out.println("The player " + player.name + " is eliminated");
					if (nRounds != 1) {
						System.out.println("Next Round\n");
					}
					nextRound(nRounds - 1, register);
				}
			}
		}

		public void nextRound(int nRounds, ArrayList<String> register) { //Inicio dos proximos rounds
			if (nRounds == 0) {
				System.out.println("\nPlayer " + register.get(0) + " is the winner!");
			} else {
				Vetor vec = new Vetor();

				for (int j = 0; j < nRounds; j++) {
					vec.getVec().add("0");
				}
				for (int j = 0; j < nRounds + 1; j++) {
					final Player novo = new Player(register.get(j));
					new Thread(new DanceLoop(novo, vec, register, nRounds)).start();
				}
			}
		}

		public boolean checkChair(Vector vec, int num) {
			if (vec.get(num).equals("0")) {
				return true;
			}
			return false;
		}

		public boolean allOccupied(Vector vec) {
			int counter = 0;
			for (int i = 0; i < vec.size(); i++) {
				if (!vec.get(i).equals("0")) {
					counter++;
				}
			}
			if (counter == vec.size()) {
				return true;
			} else {
				return false;
			}
		}

	}

	static class DanceLoop implements Runnable {
		private Player player;
		private Vetor vec;
		ArrayList<String> register;
		public int nRounds;

		public DanceLoop(Player bower, Vetor vec, ArrayList<String> register, int nRounds) {
			this.player = bower;
			this.vec = vec;
			this.register = register;
			this.nRounds = nRounds;

		}

		public void run() {
			Random random = new Random();
			try {
				Thread.sleep(random.nextInt(10));
			} catch (InterruptedException e) {
			}
			player.dance(player, vec, register, nRounds);
		}

	}

	static class Vetor {
		Vector<String> vec = new Vector<String>();

		public Vetor() {

		}

		public Vector getVec() {
			return vec;
		}

		public void setVec(Vector vec) {
			this.vec = vec;
		}

	}

	public static void main(String[] args) throws InterruptedException { 
		//Main e inicio do primeiro round
		ArrayList<String> register = new ArrayList();
		Vetor vec = new Vetor();

		vec.getVec().add("0");
		vec.getVec().add("0");
		vec.getVec().add("0");
		vec.getVec().add("0");

		final Player one = new Player("1");
		final Player two = new Player("2");
		final Player three = new Player("3");
		final Player four = new Player("4");
		final Player five = new Player("5");

		int n = 4;

		new Thread(new DanceLoop(one, vec, register, n)).start();
		new Thread(new DanceLoop(two, vec, register, n)).start();
		new Thread(new DanceLoop(three, vec, register, n)).start();
		new Thread(new DanceLoop(four, vec, register, n)).start();
		new Thread(new DanceLoop(five, vec, register, n)).start();
	}

}
