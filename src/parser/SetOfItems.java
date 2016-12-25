package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 项集类
 * Created by 李坤隆 on 2016/12/24.
 */
public class SetOfItems {

    //状态计数
    public static int count = 0;

    //当前项集状态标号
    public int mTag;

    //是否已加入状态集
	private boolean isInAllStatus;

    private List<Item> mItems;

    public SetOfItems(List<Item> mItems) {
        this.mItems = mItems;
        isInAllStatus = false;
    }


    //------------Getter And Setter Begin-------------
    public int getmTag() {
        return mTag;
    }

    //设置当前项集状态标号
    public void setmTag() {
        mTag = count++;
    }

	public boolean isInAllStatus() {
		return isInAllStatus;
	}

	public void setInAllStatus(boolean inAllStatus) {
		isInAllStatus = inAllStatus;
	}

	public List<Item> getmItems() {
        return mItems;
    }

    public void setmItems(List<Item> mItems) {
        this.mItems = mItems;
    }
    //------------Getter And Setter End-------------


    public void closure(){
    	Grammer grammer = Grammer.getInstance();
		for(int i = 0;i < mItems.size();i++){
			Item item = mItems.get(i);
			if(item.getmRight().length() == item.getmPointPosition()){
				continue;
			}
			if(grammer.isInList(grammer.getmNonTerminals(), item.getmRight().charAt(item.getmPointPosition()))){
				for(String s : grammer.getmProductionRules()){
					if(s.charAt(0) == item.getmRight().charAt(item.getmPointPosition())){
						String[] temp = s.split(":");
						List<Character> firstS = item.firstS();
						Item itemTemp = new Item(temp[0].charAt(0), temp[1], 0, firstS);
						if(!isInItems(itemTemp)){
							mItems.add(itemTemp);

						}
					}
				}
			}
		}
    }

    public int reduce(char x){
    	Grammer grammer = Grammer.getInstance();
    	//遍历当前项集中的所有项
    	for(Item item : mItems){
    		if(item.getmPointPosition() == item.getmRight().length()){
    			if(grammer.isInList(item.getA(), x)){
    				String itemProduction = item.getmLeft() + ":" + item.getmRight();
    				for(int i = 0;i < grammer.getmProductionRules().size();i++){
						String production = grammer.getmProductionRules().get(i);
    					if(itemProduction.equals(production)){
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

    public SetOfItems goTo(char x){
    	List<Item> items = new ArrayList<Item>();
    	SetOfItems setOfItems = new SetOfItems(items);
    	for(Item item : mItems){
    		if(item.getmRight().length() == item.getmPointPosition()){
    			continue;
			}
    		if(item.getmRight().charAt(item.getmPointPosition()) == x){
    			Item itemTemp = null;
				try {
					itemTemp = (Item) item.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				itemTemp.setmPointPosition(item.getmPointPosition() + 1);
    			items.add(itemTemp);
    			
    		}
    	}

    	int i = 0;
    	//如果项集不为空
    	if(items.size() != 0){
			for(; i < Chart.allStatus.size(); i++){
				if(setOfItems.equals(Chart.allStatus.get(i))){
					setOfItems = Chart.allStatus.get(i);
					break;
				}
			}
			//如果当前项集不在总状态集里面，则给当前项集设置状态编号
			if(i == Chart.allStatus.size()){
				setOfItems.setmTag();
			}
		}else {
    		setOfItems = null;
		}
    	return setOfItems;
    }
    
    public boolean isInItems(Item i){
    	
    	for(Item item : mItems){
    		if(item.equals(i)){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
	public boolean equals(SetOfItems setOfItems){

    	for(Item thisItem : this.getmItems()){
    		//是否在setOfItems中找到相同项的标记位
    		boolean tag = false;
    		for(Item item : setOfItems.getmItems()){
    			if(thisItem.equals(item)){
    				tag = true;
    				break;
    			}
			}
			if(!tag){
    			return false;
			}
		}

    	return true;
	}

}
