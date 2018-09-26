package example.com.seereal;

class ReceivedSingleton {
    private volatile boolean isReceived = false;
    private volatile static ReceivedSingleton ourInstance = new ReceivedSingleton();

    public static synchronized ReceivedSingleton getInstance()
    {
        if(ourInstance==null) {
            ourInstance = new ReceivedSingleton();
        }
        return ourInstance;
    }

    private ReceivedSingleton() {
        isReceived = false;
    }

    public void onReceived(){
        isReceived=true;
    }

    public void reset()
    {
        isReceived=false;
    }

    public boolean instanceOf(boolean t){
        if(isReceived==t){
            return true;
        }
        else
            return false;
    }
}
