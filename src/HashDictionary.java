import java.util.*;

public class HashDictionary<K,V> implements Dictionary<K,V>{
    private int size;
    private LinkedList<Entry<K,V>>[] hash;
    private LinkedList<Entry<K,V>>[] hashNew;
    private int gro = 0;
    private int sizeNew;
    private int modCount = 0;

    public HashDictionary(int prim) {
        size = prim;
        clear();
    }
    private static class Node<K,V> {
        private Node<K,V> next;
        private Entry<K,V> data;

        Node(Entry<K,V> x, Node<K,V>n) {
            data = x;
            next = n;
        }
    }
    public final void clear() {
        hash = new LinkedList[size];
        gro = 0;
        modCount++;
    }

    static int parsKey(String key){
        int adr = 0;
        for (int i = 0; i < key.length(); i++)
            adr = 31*adr + key.charAt(i);
        if (adr < 0)
            adr = -adr;
        return adr;
    }

    @Override
    public V insert(K key, V value) {
        int ke = parsKey((String) key);
        int index = ke % size;
        //Insert if hash adress is empty
        Entry<K, V> x = new Entry<>(key, value);
        if (hash[index] == null) {//entry wasn't already in the dictionary
            hash[index]  = new LinkedList<>();
            hash[index].add(x);
            gro++;
        }else {
            //Insert if Hash adress is not empty
            for (Entry<K,V> q:hash[index]) {
                if (q.getKey().equals(x.getKey())) {
                    V old = q.getValue();
                    q.setValue(x.getValue());
                    return old;
                }
            }
            hash[index].addLast(x);
            gro++;
        }
        if(2<=(gro/size)) {
                //vergrößern
                sizeNew = size *2;
                while (!isPrime(sizeNew)){
                    sizeNew++;
                }
                hashNew = new LinkedList[sizeNew];
                for (LinkedList d :hash) {
                    if(d!=null){
                        copy(d);
                    }
                }
                hash = hashNew;
                size = sizeNew;
            }

        modCount++;
        return null;
    }

    public void copy(LinkedList<Entry<K,V>> no){//Fehler beim copy
        int i = 0;

        while (i < no.size()) {
            insertnew(no.get(i).getKey(),no.get(i).getValue());
            i++;
        }
    }

    private void insertnew(K key, V value) {
        int ke = parsKey((String) key);
        int index = ke % sizeNew;
        //Insert if hash adress is empty
        Entry<K, V> x = new Entry<>(key, value);
        if (hashNew[index] == null) {//entry wasn't already in the dictionary
            hashNew[index]  = new LinkedList<>();
            hashNew[index].add(x);
        }else {
            //Insert if Hash adress is not empty
            for (Entry<K,V> q:hashNew[index]) {
                if (q.getKey().equals(x.getKey())) {
                    V old = q.getValue();
                    q.setValue(x.getValue());
                }
            }
            hashNew[index].addLast(x);
        }
    }

    public static boolean isPrime(int n) { // Zahl die getestet werden soll
        for (int t=2; t<= Math.sqrt(n); t++) { // alle Teiler
            if(n%t == 0)
                return false;    // keine Primzahl
        }
        return true; // Primzahl
    }
    @Override
    public V search(K key) {
        int ke = parsKey((String) key);;
        //int index = key.intValue() % size;
        int index = ke% size;
        int i = 0;
        while (i < hash[index].size()) {
            if(hash[index].get(i).getKey().equals(key)){
                return hash[index].get(i).getValue();
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int ke = parsKey((String) key);;
        //int index = key.intValue() % size;
        int index = ke% size;
        int i = 0;
        while (i < hash[index].size()) {
            if(hash[index].get(i).getKey().equals(key)){
                gro--;
                return hash[index].remove(i).getValue();
            }
            i++;
        }
        modCount++;
        return null;
    }

    @Override
    public int size() {
        return gro;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new hashIterator();
    }
    private class hashIterator implements Iterator <Entry<K,V>> {
        private Entry<K,V> current = getCurrent();
        private int expectedMod = modCount;
        private int curr = 0;
        private int ind = 0;

        private Entry<K,V> getCurrent() {
            for(int i = 0;i<size;i++){
                if(hash[i]!=null){
                    return hash[i].get(0);
                }
                ind++;
            }
            return null;
        }



        @Override
        public boolean hasNext() {
            return curr != size();
        }

        @Override
        public Entry<K,V> next() {
            if (expectedMod != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int i = 0;
            int index = 0;
            while (index < size) {
                if(hash[index] == null){
                    index++;
                    continue;
                }
                for (Entry<K,V> x:hash[index]) {
                    if(x.getKey().equals(current.getKey())){
                        if(hash[index].size()>hash[index].indexOf(x)+1) {
                            curr++;
                            current = hash[index].get(hash[index].indexOf(x) + 1);
                            return hash[index].get(hash[index].indexOf(x) + 1);
                        } else {
                            index++;
                            for (int j = index+1; j < size; j++) {
                                if (hash[index].get(0) != null){
                                    curr++;
                                    current = hash[index].get(0);
                                    return hash[index].get(0);
                                }
                                index++;
                            }
                            return null;
                        }
                    }
                }
                index++;
            }
            return null;
        }
    }
}
