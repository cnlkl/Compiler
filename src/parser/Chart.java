package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chart {
	private HashMap<String, String> mChart;
	private Grammer mGrammer;
	private List<Character> mNTAndTList;
	public static List<SetOfItems> allStatus;
	
	public Chart(String grammer){
		mChart = new HashMap<String, String>();
		mGrammer = Grammer.getInstance(grammer);
		mNTAndTList = new ArrayList<Character>();
		allStatus = new ArrayList<SetOfItems>();
		mNTAndTList.addAll(mGrammer.getmTerminals());
		mNTAndTList.addAll(mGrammer.getmNonTerminals());
		init();
	}

	//------------------------------Getter And Setter-------------------------
	public HashMap<String, String> getmChart() {
		return mChart;
	}

	public void setmChart(HashMap<String, String> mChart) {
		this.mChart = mChart;
	}

	public Grammer getmGrammer() {
		return mGrammer;
	}

	public void setmGrammer(Grammer mGrammer) {
		this.mGrammer = mGrammer;
	}

	public List<Character> getmNTAndTList() {
		return mNTAndTList;
	}

	public void setmNTAndTList(List<Character> mNTAndTList) {
		this.mNTAndTList = mNTAndTList;
	}
	//------------------------------Getter And Setter-------------------------

	private void init(){
		//初始化项集
		SetOfItems initSet = getInitSet();
		allStatus.add(initSet);
		initSet.setInAllStatus(true);
		initParseChart(initSet);
	}

	private void initParseChart(SetOfItems initSet){
		//用于存放尚未跳转的状态
		List<SetOfItems> setOfItemss = new ArrayList<SetOfItems>();
		setOfItemss.add(initSet);

		for(int i = 0;i < setOfItemss.size();i++){
			SetOfItems currentSet = setOfItemss.get(i);
			currentSet.closure();

			//遍历所有终结符与非终结符
			for(Character c : mNTAndTList) {
				//遇到移进归约冲突优先归约
				int reduce = currentSet.reduce(c);
				if (reduce == -1) {
					SetOfItems next = currentSet.goTo(c);
					if (next != null) {
						if (!next.isInAllStatus()) {
							setOfItemss.add(next);
							allStatus.add(next);
							next.setInAllStatus(true);
						}
						if (mGrammer.isInList(mGrammer.getmTerminals(), c)) {
							//状态3的前看符号集合
							String firstR3 = "#fwdk{i}b";
							//状态8的前看符号集合
							String firstR8 = "fwdk{i}";
							//如果下一状态为3或者5则说明本项集合里面有推出空的产生式，直接归约。
							if(next.getmTag() == 3){
								for(int k = 0;k < firstR3.length();k++){
									Character character = firstR3.charAt(k);
									mChart.put(currentSet.getmTag() + character.toString(), "r" + 3);
								}
							}else if(next.getmTag() == 5){
								for(int k = 0;k < firstR8.length();k++){
									Character character = firstR8.charAt(k);
									mChart.put(currentSet.getmTag() + character.toString(), "r" + 8);
								}
							}else{
								mChart.put(currentSet.getmTag() + c.toString(), "s" + next.getmTag());
							}
						} else {
							mChart.put(currentSet.getmTag() + c.toString(), "g" + next.getmTag());
						}
					}
				} else {
					mChart.put(currentSet.getmTag() + c.toString(), "r" + reduce);
				}
			}
		}
	}

	private SetOfItems getInitSet(){

		List<Item> itemList = new ArrayList<Item>();
		String initProduction = mGrammer.getmProductionRules().get(0);
		String[] temp = initProduction.split(":");
		List<Character> betaAFirstList = new ArrayList<Character>();
		betaAFirstList.add('$');
		itemList.add(new Item(temp[0].charAt(0), temp[1], 0, betaAFirstList));

		SetOfItems initSet = new SetOfItems(itemList);
		initSet.setmTag();
		return initSet;
	}

	public void printAllStatus(){
		for (SetOfItems setOfItems : allStatus){
			System.out.println("-------状态 " + setOfItems.getmTag() +" Begin--------");
			for (Item item : setOfItems.getmItems()){
				System.out.println(item.getmLeft() + ":" + item.getmRight() + " , "+ item.getmPointPosition() + item.getA());
			}
			System.out.println("-------状态 " + setOfItems.getmTag() + " End-------");
		}
	}

	public void printParseChart(){
		for(Character c : mNTAndTList){
			System.out.print("\t\t" + c.toString());
		}
		System.out.println();

		for (int i = 0;i < SetOfItems.count;i++){
			System.out.print(i);
			for (int j = 0;j < mNTAndTList.size();j++){
				String temp = mChart.get(i + (mNTAndTList.get(j).toString()));
				if(temp != null){
					System.out.print("\t\t" + temp);
				}else{
					System.out.print("\t\t");
				}
			}
			System.out.println();
		}
	}
}
