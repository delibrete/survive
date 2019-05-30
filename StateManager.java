class StateManager 
{ 
    // static variable single_instance of type Singleton 
    private static StateManager single_instance = null; 
  
    // variable of type String 
    public State state; 
  
    // private constructor restricted to this class itself 
    private StateManager() 
    { 
        
    } 
  
    // static method to create instance of Singleton class 
    public static StateManager getInstance() 
    { 
        if (single_instance == null) 
            single_instance = new StateManager(); 
  
        return single_instance; 
    }

	public void setState(State s) {
		state = s;
		state.enter();
	}

	public void changeState(State s) {
		if (s.getName() != state.getName()) {
			state.exit();
			state = s;
			state.enter();
		}
	}

	public State getState() {
		return state;
	}

	public void printState() {
		System.out.println(state.getName());
	}
} 