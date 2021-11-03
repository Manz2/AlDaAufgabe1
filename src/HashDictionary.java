import java.util.*;

public class HashDictionary<K,V> implements Dictionary<K,V>{
    private int size;
    private LinkedList<Entry<K,V>>[] hash;//'hash' is the LinkedHashDictionarry
    private LinkedList<Entry<K,V>>[] hashNew;//'hashNew' is a auxiliary Array to copy Entry's
    private int gro = 0;//total number of Entry's
    private int sizeNew;//auxiliary size to copy the array
    private int modCount = 0;

    public HashDictionary(int prim) {
        size = prim;
        clear();
    }

    public final void clear() {
        hash = new LinkedList[size]; //creates a new Linked HashDictionary
        gro = 0;
        modCount++;
    }


    /**
     * parsKey converts String 'key' to int
     *
     * @param key
     * @return the hash of the String
     */
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
        int index = ke % size; // calculates the Index in the 'hash' Array
        //Insert if hash address is empty
        Entry<K, V> entry = new Entry<>(key, value);
        if (hash[index] == null) {//entry wasn't already in the dictionary
            hash[index]  = new LinkedList<>();
            hash[index].add(entry);
            gro++;
        }else {
            //Insert if Hash adress is not empty
            for (Entry<K,V> q:hash[index]) {
                if (q.getKey().equals(entry.getKey())) {//Entry was already in the Linked List at 'hah[index]'
                    V old = q.getValue();
                    q.setValue(entry.getValue());
                    return old;//return the old value of the Entry
                }
            }
            hash[index].addLast(entry); //Ad's the Entry at the end of the LinkedList
            gro++;//increases total amount of Entry's in 'hash'
        }
        if(2<=(gro/size)) {
                //enlarges the size of 'hash' until it is a prime number which is at least twice as big as before
                sizeNew = size *2;//doubled the size of 'hash'
                while (!isPrime(sizeNew)){//enlarges the size as long as it is no prime number
                    sizeNew++;
                }
                hashNew = new LinkedList[sizeNew];
                //copies all Entrys to hashNew
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
    public static boolean isPrime(int n) { // Number to be tested
        for (int t=2; t<= Math.sqrt(n); t++) { // all dividers
            if(n%t == 0)
                return false;    // not a prime number
        }
        return true; // prime number
    }

    /**
     * copies 'entryC' in 'hashNew'
     *
     * @param entryC
     */
    public void copy(LinkedList<Entry<K,V>> entryC){
        int i = 0;
        while (i < entryC.size()) {
            insertNew(entryC.get(i).getKey(),entryC.get(i).getValue());//insertNew because the destination is not 'hash' but hashNew
            i++;
        }
    }

    private void insertNew(K key, V value) {
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


    @Override
    public V search(K key) {
        int ke = parsKey((String) key);;
        //int index = key.intValue() % size;
        int index = ke% size; //calculates the index of 'key'
        if(hash[index] == null){
            return null;
        }
        int i = 0;
        while (i < hash[index].size()) {
            if(hash[index].get(i).getKey().equals(key)){
                return hash[index].get(i).getValue();
            }
            i++;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int ke = parsKey((String) key);;
        int index = ke% size;//calculates the index of 'key'
        int i = 0;
        while (i < hash[index].size()) {//searches 'key' in the 'hash[index]'
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
    }//return total number of Entries in 'hash'

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new hashIterator();
    }
    private class hashIterator implements Iterator <Entry<K,V>> {
        private Entry<K,V> current = getFirst();
        private int expectedMod = modCount;
        private int curr = 0;//number of Entries returned by the iterator

        //getFirst returns the First Entry in 'hash'
        private Entry<K,V> getFirst() {
            //seaches the first index in 'hash' which is not null
            for(int i = 0;i<size;i++){
                if(hash[i]!=null){
                    return hash[i].get(0);
                }
            }
            return null;//'hash' do not have any entries
        }

        @Override
        public boolean hasNext() {
            return curr != size();
        } // returns if all entries where printed once


        /**
         * returns the following Entry
         *
         * @return
         */
        @Override
        public Entry<K,V> next() {
            if(curr==0){//checks if hasNext was called the first Time
                curr++;
                return current;//returns current as first value
            }
            if (expectedMod != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int ke = parsKey((String) current.getKey());
            int index = ke% size;//calculates index of 'current'
            for (Entry<K,V> x:hash[index]) {//searches for 'current'
                if(x.getKey().equals(current.getKey())){//current was found
                    if(hash[index].size()>((hash[index].indexOf(x))+1)) {//the following Entry after current is not null
                        curr++;
                        current = hash[index].get(hash[index].indexOf(x) + 1);
                        return hash[index].get(hash[index].indexOf(x) + 1); // returns the following Entry
                    } else { //the following Entry after current is null
                        index++;//enlarges 'index' to get next List in 'hash'
                        for (int j = index; j <= size; j++) {//searches for a List in 'hash'
                            if (hash[index]!=null&&hash[index].get(0) != null){
                                curr++;
                                current = hash[index].get(0);
                                return hash[index].get(0);//returns the first Entry of the List at 'hash'
                            }
                            index++;
                        }
                        //all following 'hash' indices do not contain a Linked List
                        return null;
                    }
                }
            }
            return null;
        }
    }
}
