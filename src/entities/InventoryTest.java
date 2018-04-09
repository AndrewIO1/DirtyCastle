package entities;
import java.util.ArrayList;

//инвентарь
class InventoryTest { 
	ArrayList<SomeItem> inventory = new ArrayList<SomeItem>();
	private int maxSpace = 0;
	private int space = 0;


	public void setSpace(int sp) {
		space = sp;
	}
	public int getSpace() {
		return space;
	}

	public void setMaxSpace(int max) {
		maxSpace = max;
	}

	public int checkFreeSpace() {
		int free = maxSpace - space;
		return free;

	}

	public void putItem(SomeItem item) {
		int size = item.getSize();
		if (size > maxSpace && size > checkFreeSpace()) {
			System.out.println("Не влезет");
		}
		else {
			int newSpace = getSpace() + size;
			System.out.println(newSpace);
			setSpace(newSpace);
			inventory.add(item);
		}
	}

	public void checkInventory() {
		System.out.println("Предметы в инвентаре:");
		int counter = 1;
		for (SomeItem item : inventory) {
			System.out.println(counter + ": " + item.getName());
			counter++;
			System.out.println("Осталось свободного места: " + checkFreeSpace());
			System.out.println();

		}
		System.out.println("Общий вес: " + getSpace());
	}
}


//здесь тест
class TestInv {
	public static void main(String[] args) {
		InventoryTest inv = new InventoryTest();
		SomeItem pr = new SomeItem();
		SomeItem pr2 = new SomeItem();
		SomeItem pr3 = new SomeItem();
		SomeItem pr4 = new SomeItem();
		SomeItem pr5 = new SomeItem();

		inv.setMaxSpace(10);
		System.out.println("Осталось свободного места: " + inv.checkFreeSpace());
		
		pr.setName("Палка Ебалка");
		pr.setSize(4);
		pr2.setName("КомпАсс");
		pr2.setSize(1);
		pr3.setName("Жопа");
		pr3.setSize(3);
		pr4.setName("Камера Обскура");
		pr4.setSize(2);
		pr5.setName("Твоя Мамка");
		pr5.setSize(7);


		inv.putItem(pr);
		inv.putItem(pr2);
		inv.putItem(pr3);
		inv.putItem(pr4);
		inv.putItem(pr5);

		inv.checkInventory();




	}
}


//предмет
class SomeItem {
	private int size;
	private int weight;
	private String name;

	int getSize() {
		return size;
	}

	public void setSize(int s) {
		size = s;
	}

	int getWeight() {
		return weight;
	}

	public void setWeight(int w) {
		weight = w;
	}

	String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}
}
