package world;

import java.util.Random;

import util.EntityFactory;
import util.SimplexNoise;
import world.Tile.TILE_TYPE;

public class MapGenerator {
	private static int x = 0;//координаты центра карты
	private static int y = 0;
	//private static int regionX = 0;
	//private static int regionY = 0;
	private static double scale;//приближение (4 стандартно)
	private static float elevationOctaves[] = new float[6]; 
	//октавы подъёма, нужны для генератора, можешь менять их как хочешь и глянуть, чё будет, только напиши в комментах начальные значения
	private static float moistureOctaves[] = new float[6];//пока юзелес, для деревьев и другой растительности
	private static double a;//общий подъём карты над уровнем моря
	private static double b;//Вроде опускание карты по краям
	private static double c;//Вроде скорость опускания местности, 
	//чем больше, тем быстрее опускается в зависимости от расстояния до точек возвышенности
	private static int islandNum = 0;//острова, ща их нет, да и воды пока тоже нет
	private static double MountainX[] = new double[5];//Точки с горами, рядом с ними наибольшая высота, плавно уменьшается с расстоянием
	private static double MountainY[] = new double[5];
	private static double IslandX[] = new double[islandNum];//Острова, дают меньший подъём местности, чем горы, сейчас их нет
	private static double IslandY[] = new double[islandNum];
	private static double HotPointX[] = new double[10];//Точки высокой температуры, для биомов, пока не нужно
	private static double HotPointY[] = new double[10];
	private static double[][] riverX;
	private static double[][] riverY;
	private static int riverAmount = 3;
	private static int[] riverLength;
	private static double riverSectionLength = 1.5;
	private static double maxDegree = 12;
	private static double currentDegree;
	private static int mapSize = 256;//Размер карты, правда он пока не юзается отсюда

	private static int seed = 5;//Сид, можешь менять, только закомменти оригинал 5
	private static SimplexNoise simplex = new SimplexNoise(seed);//Шумовая функция для генерации карты
	private static Random rand = new Random(seed);//Обычный рандом для гор и прочего говна

	

	public static Random getRand() {
		return rand;
	}

	public static SimplexNoise getSimplex() {
		return simplex;
	}

	public static void generate(int centerX, int centerY){
		//ПАЕХАЛИ
		elevationOctaves[0] = 1f;
		/*elevationOctaves[1] = 0.5f;
		elevationOctaves[2] = 0.25f;
		elevationOctaves[3] = 0.13f;
		elevationOctaves[4] = 0.06f;
		elevationOctaves[5] = 0.03f;*/

		moistureOctaves[0] = 1f;//1
		elevationOctaves[1] = 0.75f;//0.75
		elevationOctaves[2] = 0.43f;//0.33
		elevationOctaves[3] = 1.1f;//0.33 1.5
		elevationOctaves[4] = 0.5f;//0.33 0.5
		elevationOctaves[5] = 0.5f;//0.5 1.5

		a = 0.165;//0.12
		b = 1.5;
		c = 2.0;
		//расставляются параметры генератора
		placePoints();
		//ставятся точки гор и высокой температуры
		placeRivers();
		//Реки
		Tile[][][] tiles = new Tile[256][256][1];
		WorldMap mapSingleton = WorldMap.createMap(tiles);

		EntityFactory.init();

		scale = 4;//4
		x -= 512 - centerX;
		y -= 512 - centerY;
		//Вычисляется точка центра карты
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[0].length; j++){
				double nx = (i/256d - 0.5 + x/1024d*(scale/0.5))/scale;
				double ny = (j/256d - 0.5 + y/1024d*(scale/0.5))/scale;
				double e = getElevation(nx,ny);
				double m = getMoisture(nx, ny);
				mapSingleton.placeTile(i, j, 0, getWall(e), getFloor(e), m);
			}
		}
		//генерация карты

		mapSingleton.groupManager().setMapGroups();
		
		mapSingleton.createMinimap();
	}

	private static void placeRivers() {
		riverLength = new int[riverAmount];
		int riverLengthMax = 0;
		for(int i = 0; i < riverAmount; i++) {
			riverLength[i] = rand.nextInt(120) + 200;
			if(riverLength[i] > riverLengthMax) {
				riverLengthMax = riverLength[i];
			}
		}
		riverX = new double[riverAmount][riverLengthMax];
		riverY = new double[riverAmount][riverLengthMax];

		for(int j = 0; j < riverAmount; j++) {
			for(int i = 1; i < riverLength[j]; i++) {
				riverX[j][i] = -9999;
				riverY[j][i] = -9999;
			}
		}

		for(int i = 0; i < riverAmount; i++) {
			boolean success;
			do {
				success = true;
				int side = rand.nextInt(4);
				switch(side) {
				case 0:
					riverX[i][0] = -mapSize;
					riverY[i][0] = rand.nextInt(mapSize*2)-mapSize;
					break;
				case 1:
					riverX[i][0] = rand.nextInt(mapSize*2)-mapSize;
					riverY[i][0] = -mapSize;
					break;
				case 2:
					riverX[i][0] = mapSize;
					riverY[i][0] = rand.nextInt(mapSize*2)-mapSize;
					break;
				case 3:
					riverX[i][0] = rand.nextInt(mapSize*2)-mapSize;
					riverY[i][0] = mapSize;
					break;
				}
				//riverX[i][0] = rand.nextInt(mapSize)-mapSize/2;
				//riverY[i][0] = rand.nextInt(mapSize)-mapSize/2;
				for(int j = 0; j < riverAmount; j++) {
					if(j==i) continue;
					if(Math.sqrt(Math.pow(riverX[i][0]-riverX[j][0], 2) + Math.pow(riverY[i][0]-riverY[j][0], 2)) < 80) {
						success = false;
						break;
					}
				}
			} while(!success);
		}

		//currentDegree = rand.nextInt(360);
		for(int j = 0; j < riverAmount; j++) {
			currentDegree = Math.toDegrees(Math.atan(riverY[j][0]/riverX[j][0]));
			for(int i = 1; i < riverLength[j]; i++) {
				riverX[j][i] = riverX[j][i-1] + Math.cos(Math.toRadians(currentDegree))*riverSectionLength;
				riverY[j][i] = riverY[j][i-1] + Math.sin(Math.toRadians(currentDegree))*riverSectionLength;
				boolean success;
				int iter = 0;
				int preIter = 0;
				double tempDegree;
				do {
					success = true;
					double degreeLimit = maxDegree + iter*1.5;
					if(i <= 100) {
						tempDegree = Math.toDegrees(Math.atan(riverY[j][0]/riverX[j][0])) + rand.nextDouble()*degreeLimit*2 - degreeLimit;
					}else {
						tempDegree = currentDegree + rand.nextDouble()*degreeLimit*2 - degreeLimit;
					}
					if(iter > 15) break;
					double nextX = riverX[j][i] + Math.cos(Math.toRadians(tempDegree))*riverSectionLength;
					double nextY = riverY[j][i] + Math.sin(Math.toRadians(tempDegree))*riverSectionLength;
					double nextX2 = nextX + Math.cos(Math.toRadians(tempDegree))*riverSectionLength;
					double nextY2 = nextY + Math.sin(Math.toRadians(tempDegree))*riverSectionLength;
					outer: for(int k = 0; k < riverAmount; k++) {
						for(int h = 1; h < riverLength[k]; h++) {
							if(k == j && h == i) continue;
							double limit = 10;
							if(k == j) {
								limit = 1;
							}
							if(Math.sqrt(Math.pow(nextX-riverX[k][h], 2) + Math.pow(nextY-riverY[k][h], 2)) < limit) {
								success = false;
								preIter++;
								if(preIter > 5) {
									iter++;
									preIter = 0;
								}

								break outer;
							}
						}
					}

					outer: for(int k = 0; k < riverAmount; k++) {
						for(int h = 1; h < riverLength[k]; h++) {
							if(k == j && h == i) continue;
							double limit = 5;
							if(k == j) {
								limit = 1.5;
							}
							if(Math.sqrt(Math.pow(nextX2-riverX[k][h], 2) + Math.pow(nextY2-riverY[k][h], 2)) < limit) {
								success = false;
								preIter++;
								if(preIter > 10) {
									iter++;
									preIter = 0;
								}
								break outer;
							}
						}
					}

				} while(!success);

				currentDegree = tempDegree;
			}
		}

		for(int j = 0; j < riverAmount; j++) {
			for(int i = 0; i < riverLength[j]; i++) {
				riverX[j][i] /= mapSize;
				riverY[j][i] /= mapSize;
			}
		}
	}

	private static void placePoints(){
		for(int i = 0; i < MountainX.length; i++){
			MountainX[i] = (double)(rand.nextInt(mapSize/1) - mapSize/2)/mapSize;
			MountainY[i] = (double)(rand.nextInt(mapSize/1) - mapSize/2)/mapSize;
		}
		//Ставит рандомно точки с горами, чтолбы поднять вокруг них местность

		IslandX = new double[islandNum];
		for(int i = 0; i < IslandX.length; i++){
			int iter = 0;
			double dist;
			do{
				dist = 99;
				IslandX[i] = (double)(rand.nextInt((int) (mapSize*1.6)) - mapSize*1.6/2)/mapSize;
				IslandY[i] = (double)(rand.nextInt((int) (mapSize*1.6)) - mapSize*1.6/2)/mapSize;
				for(int j = 0; j < MountainX.length; j++){
					double nDist = 2*Math.sqrt((IslandX[i]-MountainX[j])*(IslandX[i]-MountainX[j]) + (IslandY[i]-MountainY[j])*(IslandY[i]-MountainY[j]));
					if(nDist < dist) dist = nDist;
				}
				iter++;
			}while(dist < 1.3 && iter < 100);
			if(iter == 100){
				IslandX = new double[0];
			}
		}

		//Точки с островами, сейчас не нужны, раскидывает их по краям мира, подальше от континента

		for(int i = 0; i < HotPointX.length; i++){
			HotPointX[i] = (double)(rand.nextInt((int) (mapSize*1.6)) - mapSize*1.6/2)/mapSize;
			HotPointY[i] = (double)(rand.nextInt((int) (mapSize*1.6)) - mapSize*1.6/2)/mapSize;
		}

		//Точки высокой температуры для биомов
	}

	private static final double getElevation(double x, double y){
		double e = 0;
		for(int k = 0; k < 5; k++){
			e += elevationOctaves[k]*simplex.generateSimplexNoise(x*Math.pow(2, k), y*Math.pow(2, k));
		}
		//Генерит функцией значение высоты, использует октавы (по сути это просто разные числа, на которые умножаются значения функции)
		e /= (elevationOctaves[0] + elevationOctaves[1] + elevationOctaves[2] + elevationOctaves[3] + elevationOctaves[4]);
		//нормализует значение, чтобы оно было в пределах от 0 до 1
		double d = 99;
		double iD = 99;
		for(int k = 0; k < MountainX.length; k++){
			double nDist = 2*Math.sqrt((x-MountainX[k])*(x-MountainX[k]) + (y-MountainY[k])*(y-MountainY[k]));
			if(nDist < d) d = nDist;
		}
		//Считает расстояние до ближайшей горы
		for(int k = 0; k < IslandX.length; k++){
			double nDist = 2*Math.sqrt((x-IslandX[k])*(x-IslandX[k]) + (y-IslandY[k])*(y-IslandY[k]));
			if(nDist < iD){
				iD = nDist;
			}
		}
		//Расстояние до ближайшего острова
		double t1 = Math.max(0, 0.95-b*Math.pow(d, c));
		//Изменяет высоту в зависимости от близости к горе
		double t2 = Math.max(0, 0.55-b*Math.pow(iD, c));
		//Изменяет высоту в зависимости от близости к острову
		e = Math.max((e+a)*(t1),(e+a*1.2)*(t2));
		//Берёт максимальную высоту (ближе к горе или острову)

		double rD = 99;
		for(int j = 0; j < riverAmount; j++) {
			for(int i = 0; i < riverLength[j]; i++) {
				double rDist = Math.sqrt((x-riverX[j][i])*(x-riverX[j][i]) + (y-riverY[j][i])*(y-riverY[j][i]));
				if(rDist < rD) {
					rD = rDist;
				}
			}
		}

		if(rD < (rand.nextDouble()/4.5+1.5)/mapSize) {
			double t3 = Math.pow(rD, 1/7d);
			e -= t3;
		}

		return e;
	}

	private static final double getMoisture(double x, double y){
		double m = 0;
		for(int k = 0; k < 6; k++){
			m += moistureOctaves[k]*simplex.generateSimplexNoise(x*Math.pow(2, k+4), y*Math.pow(2, k+4));
		}
		m += Math.abs(0.3*simplex.generateSimplexNoise(x*60, y*60));
		m += Math.abs(1*simplex.generateSimplexNoise(x*1400, y*1400));
		m /= (moistureOctaves[0] + moistureOctaves[1] + moistureOctaves[2] + moistureOctaves[3] + moistureOctaves[4] + moistureOctaves[5]+0.3+1);
		return m;
		//Растительность, пока не нужно
	}

	private static final TILE_TYPE getFloor(double e){
		if(e > 0.25) {
			return TILE_TYPE.ROCK;
		}
		if(e < 0.04) {
			return TILE_TYPE.WATER;
		}

		return TILE_TYPE.GRASS;
		//Достаёт пол в зависимости от высоты (e)
	}

	private static final TILE_TYPE getWall(double e){

		if(e > 0.27){
			return TILE_TYPE.ROCK;
		}

		if(e > 0.25) {
			return TILE_TYPE.DIRT;
		}

		return TILE_TYPE.NONE;
		//Достаёт стенку в зависимости от высоты (e)
	}
}
