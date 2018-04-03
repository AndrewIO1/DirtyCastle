package entities;

import core.DwarfsGame;
import entities.creatures.TestCreature;

public class CharGen {
	/**
	 * @param args
	 * @return 
	 */
//	генератор СЭКСА
	private static void sexGen(TestCreature.Builder citizen) {
		//генерация пола
		int sex =  1 + (int)(Math.random() * 2);
		citizen.sex(sex);
	}
	
//	генератор имени
// заполняем массивы слогами
	private static String[] male1s = {"Vladi","Kosti","Brone","Kila", "Andre", "Meta", "Stan"};
	private static String[] male2s = {"slav","mir","var","tard"};
	private static String[] fmale1s = {"Stel", "Skal", "Flo", "Ka", "Si"};
	private static String[] fmale2s = {"mi", "la", "li", "den"};
	private static String[] fmale3s = {"cia", "na", "nia"};

	private static void nameGen(TestCreature.Builder citizen) {
		String name;
		if (citizen.sex() == 1) {
			String rand1 = male1s[DwarfsGame.rnd.nextInt(male1s.length)];
			String rand2 = male2s[DwarfsGame.rnd.nextInt(male2s.length)];
			name = rand1 + rand2;
		}
		else {
			String rand1 = fmale1s[DwarfsGame.rnd.nextInt(fmale1s.length)];
			String rand2 = fmale2s[DwarfsGame.rnd.nextInt(fmale2s.length)];
			String rand3 = fmale3s[DwarfsGame.rnd.nextInt(fmale3s.length)];
			name = rand1 + rand2 + rand3;
			}
		citizen.name(name) ;	
		
	}
	
// генератор основных статов
	private static void mainStatGen(TestCreature.Builder citizen) {
		citizen.maxHp(100);
		citizen.speed(0.1f);
		citizen.str(10); //сила
		citizen.intel(10); //ум
		citizen.dex(10); //ловкость
		citizen.mood(50); //настроение
	}
	
	
//	генератор типа тела
	private static void bodyGen(TestCreature.Builder citizen) {
		int[] bodyStats = new int[3]; //статы тела
		//определение телосложения по Шелдону
				bodyStats[0] = 1 + (int)(Math.random()*7); //жир - эндоморф
				bodyStats[1] = 1 + (int)(Math.random()*7); //сила - мезоморф
				bodyStats[2] = 1 + (int)(Math.random()*7); //худоба - эктоморф

		//заносим информацию о телосложении в переменную, чтобы потом на её основе выбиралась грамотная модель персонажа.

				if(bodyStats[0] < 5 && bodyStats[1] < 5 && bodyStats[2] < 5) {

					citizen.bodyType(0);//fucking normie

				} else if(bodyStats[0] < bodyStats[1] && //жир < щощ
						bodyStats[0] < bodyStats[2] && //жир < слим
						bodyStats[1] == bodyStats[2]) // щощ = слим
				{ 	
					citizen.bodyType(0);//reeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
				}else if (bodyStats[0] >= 5) { // жир >= 5
					if (bodyStats[0] <= bodyStats[2]) {//жир <= худоба
						citizen.bodyType(0);//NORMIES GET OUT
					}else {
						citizen.bodyType(2); //жиробасс
					}
				}
				else if (bodyStats[1] >= 5){
					citizen.bodyType(1); // говнатырь
				} 
				else{
					citizen.bodyType(3); //дрищ
				}
				
				float speed = 10;
				citizen.maxHp(citizen.maxHp() + (bodyStats[0]*10));
				citizen.dex(citizen.dex() - bodyStats[0] + bodyStats[2]);
				citizen.speed((speed - bodyStats[0] + (bodyStats[2]) + (bodyStats[1]/2))/10);
				citizen.str(citizen.str() + bodyStats[1] - (bodyStats[2]/2) + (bodyStats[0]/2));
				citizen.intel(citizen.intel() + bodyStats[2]);
	}
	


	//genirator 0.3.1
	public static TestCreature.Builder charGen(TestCreature.Builder citizen){
		sexGen(citizen);
		nameGen(citizen);
		mainStatGen(citizen);
		bodyGen(citizen);
		
		return citizen;
	}
	
	
	//ОНЛИ ТЕСТ
	public static void main(String[] args) {
		TestCreature.Builder citizen = new TestCreature.Builder("");
		charGen(citizen);
		
		//вывод всей инфы
		System.out.println("Имя - "+citizen.name());
		System.out.println("Пол - "+citizen.sex());
		String badi = (citizen.bodyType() == 0 ? "Normie" : (citizen.bodyType() == 1) ? "Мощ щощ" : (citizen.bodyType() == 2) ? "Jirna hura" : "дрищ"); 
		System.out.println("Тип тела - "+badi+citizen.bodyType());
		System.out.println("HP = "+citizen.maxHp());
		System.out.println("dex = "+citizen.dex());
		System.out.println("speed = "+citizen.speed());
		System.out.println("str = "+citizen.str());
		System.out.println("intel = "+citizen.intel());

	}
}
