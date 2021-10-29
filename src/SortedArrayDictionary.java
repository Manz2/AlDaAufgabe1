import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.NoSuchElementException;


public class SortedArrayDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {
    private int size;
    private Entry<K, V>[] sortedArray;
    private final int DEFAULT_SIZE = 32;
    private int modCount = 0;

    public SortedArrayDictionary() {
        clear();
    }

    public final void clear() {
        size = 0;                                           //Gespeicherte größe wird auf 0 gesetzt
        sortedArray = new Entry[DEFAULT_SIZE];              //Neues Array vom Typ Entry mit der Größe Default_Size wird erstellt
        modCount++;
    }

    @Override
    public V insert(K key, V value) {
        int li = 0;
        int re = size - 1;

        if (size != 0) {
            while (li <= re) {
                int m = (li + re) / 2;
                if (key.compareTo(sortedArray[m].getKey()) < 0) {
                    re = m - 1;
                } else if (key.compareTo(sortedArray[m].getKey()) > 0) {
                    li = m + 1;
                } else {
                    V old = sortedArray[m].getValue();
                    sortedArray[m].setValue(value);
                    return old; //KEY GEFUNDEN
                }
            }
            if (size() + 10 == sortedArray.length) {
                sortedArray = Arrays.copyOf(sortedArray, sortedArray.length * 2);
            }

            Entry x = new Entry<>(key, value);
            sortedArray[size] = x;
            moveToLeft(size);

        } else {

            Entry x = new Entry<>(key, value);
            sortedArray[0] = x;
        }
        size++;
        modCount++;
        return null;
    }

    @Override
    public V search(K key) {
        int li = 0;
        int re = size - 1;

        while (li <= re) {
            int m = (li + re) / 2;
            if (key.compareTo(sortedArray[m].getKey()) < 0) {
                re = m - 1;
            } else if (key.compareTo(sortedArray[m].getKey()) > 0) {
                li = m + 1;
            } else {
                return sortedArray[m].getValue(); //KEY GEFUNDEN
            }
        }
        return null;//key nicht gefunden
    }

    private void moveToLeft(int pos) {
        Entry o = sortedArray[pos];
        K w = sortedArray[pos].getKey();
        int i = pos - 1;
        if (pos != 0) {
            while (sortedArray[i].getKey().compareTo(w) > 0) {
                sortedArray[i + 1] = sortedArray[i];
                sortedArray[i] = sortedArray[pos];
                i--;
                if (i < 0) {
                    break;
                }
            }
            sortedArray[i + 1] = o;
        }
    }

    @Override
    public V remove(K key) {
        int li = 0;

        if (size != 0) {
            int re = size - 1;
            while (li <= re) {
                int m = (li + re) / 2;
                if (key.compareTo(sortedArray[m].getKey()) < 0) {
                    re = m - 1;
                } else if (key.compareTo(sortedArray[m].getKey()) > 0) {
                    li = m + 1;
                } else {
                    V o = sortedArray[m].getValue();
                    moveToRight(m);//move to right
                    size--;
                    return o;

                }
            }
            modCount++;
            return null;
        } else {
            return null;
        }
    }

    private void moveToRight(int pos) {
        int i = pos;
        while (sortedArray[i + 1] != null) {
            sortedArray[i] = sortedArray[i + 1];
            i++;
        }
        sortedArray[size - 1] = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new SortedArrayIterator();
    }

    private class SortedArrayIterator implements Iterator<Entry<K, V>> {

        private int current = 0;
        private int expectedMod = modCount;

        @Override
        public boolean hasNext() {
            return current != size();
        }

        @Override
        public Entry next() {
            if (expectedMod != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return sortedArray[current++];
        }
    }
}

