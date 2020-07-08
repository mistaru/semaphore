package Buffer;

public class CircularQueue implements ICharQ {
    private char[] q;
    private int putloc, getloc;

    protected CircularQueue(int size) {
        q = new char[size];
        putloc = getloc = 0;
    }

    public void put(char ch) {
        putloc++;
        if (putloc == q.length) putloc = 0;
        q[putloc] = ch;
    }

    public char get() {
        getloc++;
        if (getloc == q.length) getloc = 0;
        return q[getloc];
    }

    public char end() {
        return q[(getloc + 1) % q.length];
    }

}
