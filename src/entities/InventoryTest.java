package entities;
//инвентарь
class InventoryTest { 
	int[][] inventory = new int[3][4];

	public void checkFreeSpace() {
		for (int i=0; i < inventory.length; i++) {
			for (int j=0; j < inventory[i].length; j++) {
				System.out.print(inventory[i][j]);
			}
			System.out.println("");
		}
	}

	public void putItem(SomeItem inv) {
		int itemSize = inv.getSize();
		if (itemSize > inventory[0].length) {
			System.out.println("Нет места для предмета " + inv.getName());
		}
		else {
			for (int i=0; i < inventory.length; i++) {
				int ticker = 0;
				for (int j=0; j < inventory[i].length; j++) {

					if (inventory[i][j] == 0) {
						ticker++;
					}
					else {ticker = 0;}
					if (ticker == itemSize) {break;}
				}
					if (ticker == itemSize) {
						System.out.println("Поместили");
						break;
					}
					
			}
		}
	}
}



//здесь тест
class TestInv {
	public static void main(String[] args) {
		InventoryTest inventory = new InventoryTest();
		SomeItem kekblya = new SomeItem();

		kekblya.setSize(3);
		kekblya.setName("Палка Ебалка");
		inventory.checkFreeSpace();
		inventory.putItem(kekblya);

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