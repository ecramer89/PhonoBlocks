import java.util.LinkedList;


public abstract class Observable {
	
	private LinkedList<Observer> observers;
	
	public Observable(){
		observers=new LinkedList<Observer>();
	}
	
	public void addObserver(Observer o){
		observers.add(o);
	}
	
	public void notifyObservers(Object data){
		for(Observer o: observers)
			o.update(this, data);	
	}

}
