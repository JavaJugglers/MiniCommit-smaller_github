package gitlet;

import java.io.Serializable;

public class ArrayDeque <T> implements Serializable {
    private T[] items;
    private int size;
    private int first_next;
    private int last_next;


    public ArrayDeque() {
        items = (T[]) new Object[8];
        first_next = 0;
        last_next = 0;
    }

    private int subbing(int pos, int length) {
        if (pos == 0) {
            return length - 1;
        }
        else {
            return pos - 1;
        }

    }
    private int adding(int pos, int length) {
        if (pos == length - 1) {
            return 0;
        }
        else {
            return pos + 1;
        }
    }
    private void up_sizing() {
        int first_position = adding(first_next,this.items.length);
        int last_position = subbing(last_next,this.items.length);
        int count = 0;
        T[] now = this.items;
        int then = this.items.length;
        if (size + 1 > this.items.length) {
            this.items = (T[]) new Object[this.items.length * 2];
            while (first_position != last_position) {
                this.items[count] = now[first_position];
                first_position = adding(first_position,then);
                count = count + 1;
            }
            this.items[count] = now[first_position];
            first_next = subbing(0,this.items.length);
            last_next = adding(count,this.items.length);
        }
    }
    private void down_sizing() {
        int x = size - 1;
        int first_position = adding(first_next,this.items.length);
        int last_position = subbing(last_next,this.items.length);
        int count = 0;
        T[] now = this.items;
        int then = this.items.length;
        double y = (double) x / this.items.length;
        if (this.items.length == 8) {
            return;
        }
        if (y < 0.25) {
            this.items = (T[]) new Object[this.items.length / 2]; //this.items.length / 2
            while (first_position != last_position) {
                this.items[count] = now[first_position];
                first_position = adding(first_position,then);
                count = count + 1;
            }
            this.items[count] = now[first_position];
            first_next = subbing(0,this.items.length);
            last_next = adding(count,this.items.length);
        }
    }

    public void addFirst(T item) {
        size = size + 1;
        up_sizing();
        if (first_next == last_next) {
            this.items[first_next] = item;
            first_next = subbing(first_next,this.items.length);
            last_next = adding(last_next,this.items.length);
        }
        else {
            this.items[first_next] = item;
            first_next = subbing(first_next,this.items.length);
        }
    }

    public T removeFirst() {

        if (size == 0) {
            return null;
        }
        else if (size == 1) {
            down_sizing();
            T a = this.items[adding(first_next,this.items.length)];
            first_next = adding(first_next,this.items.length);
            last_next = subbing(last_next,this.items.length);
            size = size - 1;
            return a;
        }
        else {
            down_sizing();
            T a = this.items[adding(first_next,this.items.length)];
            first_next = adding(first_next,this.items.length);
            size = size - 1;
            return a;
        }
    }

    public void addLast(T item) {
        size = size + 1;
        up_sizing();
        if (first_next == last_next) {
            this.items[last_next] = item;
            first_next = subbing(first_next,this.items.length);
            last_next = adding(last_next,this.items.length);
        }
        else {
            this.items[last_next] = item;
            last_next = adding(last_next,this.items.length);
        }
    }

    public T removeLast() {

        if (size == 0) {
            return null;
        }
        else if (size == 1) {
            down_sizing();
            T a = this.items[subbing(last_next,this.items.length)];
            first_next = adding(first_next,this.items.length);
            last_next = subbing(last_next,this.items.length);
            size = size - 1;
            return a;
        }
        else {
            down_sizing();
            T a = this.items[subbing(last_next,this.items.length)];
            last_next = subbing(last_next,this.items.length);
            size = size - 1;
            return a;
        }
    }

    public T get(int index) {
        if (index == 0) {
            return this.items[adding(first_next,this.items.length)];
        }
        else {
            int a = index + adding(first_next,this.items.length);
            if (a > this.items.length - 1) {
                a = a - (this.items.length - 1) - 1;
                return this.items[a];
            }
            else {
                return this.items[a];
            }

        }

    }


    public void printDeque() {
        int first_position = adding(first_next,this.items.length);
        int last_position = subbing(last_next,this.items.length);
        while (first_position <= last_position) {
            System.out.print(this.items[first_position]);
            System.out.print(' ');
            first_position = adding(first_position,this.items.length);
        }
    }

    public int size() {
        return size;
    }



}
