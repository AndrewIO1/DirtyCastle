package entities;
//инвентарь
class InventoryTest { 
	int[][] inventory = new int[3][4];
	int item = 0;

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
		boolean isInside = false;
		//		Сравнение предмета и длины инвентаря
		if (itemSize > inventory[0].length) {
			System.out.println("Нет места для предмета " + inv.getName());
		}
		//		Если место есть, то ебашит цикл
		else {
			for (int i=0; i < inventory.length; i++) { //РЯД
				int ticker = 0;

				for (int j=0; j < inventory[i].length; j++) { //ЭЛЕМЕНТЫ РЯДА

					if (inventory[i][j] == 0) {
						ticker++;
					}
					else {ticker = 0;}
					if (ticker == itemSize) {break;}
				} //элемены ряда end
				
				if (ticker == itemSize) {
					item++;
					for (int k=ticker-1; k >= 0; k--) {
						inventory[i][k] = item;
					}
					isInside = true;
					System.out.println("Поместили");
					break;}
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
		kekblya.setSize(1);
		inventory.putItem(kekblya);
		kekblya.setSize(4);
		inventory.putItem(kekblya);
		kekblya.setSize(2);
		inventory.putItem(kekblya);
		kekblya.setSize(2);
		inventory.putItem(kekblya);
		inventory.checkFreeSpace();

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