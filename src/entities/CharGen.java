package entities;

import core.DwarfsGame;
import entities.creatures.TestCreature;

public class CharGen {
	/**
	 * @param args
	 * @return 
	 */
//	..������
	private static void sexGen(TestCreature.Builder citizen) {
		//��������� ����
		int sex =  1 + (int)(Math.random() * 2);
		citizen.sex(sex);
	}
	
	private static String[] maleFirstSyll = {"Vladi","Kosti","Brone","Kila", "Andre", "Meta", "Stan"};
	private static String[] maleSecondSyll = {"slav","mir","don","var","gon"};
	private static String[] fmaleFirstSyll = {"Stel", "Skal", "Flo", "Ka", "Si"};
	private static String[] fmaleSecondSyll = {"mi", "la", "li", "den"};
	private static String[] fmaleThirdSyll = {"cia", "na", "nia"};

	private static void nameGen(TestCreature.Builder citizen) {
		String name;
		if (citizen.sex() == 1) {
			name = maleFirstSyll[DwarfsGame.rnd.nextInt(maleFirstSyll.length)] + maleSecondSyll[DwarfsGame.rnd.nextInt(maleSecondSyll.length)];
		}
		else {
			name = fmaleFirstSyll[DwarfsGame.rnd.nextInt(fmaleFirstSyll.length)] + fmaleSecondSyll[DwarfsGame.rnd.nextInt(fmaleSecondSyll.length)] + fmaleThirdSyll[DwarfsGame.rnd.nextInt(fmaleThirdSyll.length)];
			}
		citizen.name(name) ;
		
		
	}
	

	private static void mainStatGen(TestCreature.Builder citizen) {
		citizen.maxHp(100);
		citizen.speed(0.1f);
		citizen.str(10); //����
		citizen.intel(10); //��
		citizen.dex(10); //��������
		citizen.mood(50); //����������
	}
	
	private static void bodyGen(TestCreature.Builder citizen) {
		int[] bodyStats = new int[3]; //����� ����
		//����������� ������������ �� �������
				bodyStats[0] = 1 + (int)(Math.random()*7); //��� - ��������
				bodyStats[1] = 1 + (int)(Math.random()*7); //���� - ��������
				bodyStats[2] = 1 + (int)(Math.random()*7); //������ - ��������

		//������� ���������� � ������������ � ����������, ����� ����� �� � ������ ���������� ��������� ������ ���������.

				if(bodyStats[0] < 5 && bodyStats[1] < 5 && bodyStats[2] < 5) {

					citizen.bodyType(0);//fucking normie

				} else if(bodyStats[0] < bodyStats[1] && //��� < ���
						bodyStats[0] < bodyStats[2] && //��� < ����
						bodyStats[1] == bodyStats[2]) // ��� = ����
				{ 	
					citizen.bodyType(0);//reeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
				}else if (bodyStats[0] >= 5) { // ��� >= 5
					if (bodyStats[0] <= bodyStats[2]) {//��� <= ������
						citizen.bodyType(0);//NORMIES GET OUT
					}else {
						citizen.bodyType(2); //��������
					}
				}
				else if (bodyStats[1] >= 5){
					citizen.bodyType(1); // ���������
				} 
				else{
					citizen.bodyType(3); //����
				}
				
				float speed = 10;
				citizen.maxHp(citizen.maxHp() + (bodyStats[0]*10));
				citizen.dex(citizen.dex() - bodyStats[0] + bodyStats[2]);
				citizen.speed((speed - bodyStats[0] + (bodyStats[2]) + (bodyStats[1]/2))/10);
				citizen.str(citizen.str() + bodyStats[1] - (bodyStats[2]/2) + (bodyStats[0]/2));
				citizen.intel(citizen.intel() + bodyStats[2]);
	}
	


	//����� ��������� ����, ���� ����
	//genirator 0.2
	public static TestCreature.Builder charGen(TestCreature.Builder citizen){
		sexGen(citizen);
		nameGen(citizen);
		mainStatGen(citizen);
		bodyGen(citizen);
		
		return citizen;
	}
	
	
	//���� ����
	public static void main(String[] args) {
		TestCreature.Builder citizen = new TestCreature.Builder("");
		charGen(citizen);
		
		//����� ���� ����
		System.out.println("��� - "+citizen.name());
		System.out.println("��� - "+citizen.sex());
		String badi = (citizen.bodyType() == 0 ? "Normie" : (citizen.bodyType() == 1) ? "��� ���" : (citizen.bodyType() == 2) ? "Jirna hura" : "����"); 
		System.out.println("��� ���� - "+badi+citizen.bodyType());
		System.out.println("HP = "+citizen.maxHp());
		System.out.println("dex = "+citizen.dex());
		System.out.println("speed = "+citizen.speed());
		System.out.println("str = "+citizen.str());
		System.out.println("intel = "+citizen.intel());

	}
}
