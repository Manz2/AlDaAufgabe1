import java.util.*;

/**
 * Implementation of the Dictionary interface as AVL tree.
 * <p>
 * The entries are ordered using their natural ordering on the keys,
 * or by a Comparator provided at set creation time, depending on which constructor is used.
 * <p>
 * An iterator for this dictionary is implemented by using the parent node reference.
 *
 * @param <K> Key.
 * @param <V> Value.
 */
public class BinaryTreeDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {

    private V oldValue;
    private int modCount = 0;
    private Node<K, V> root = null;
    private int size = 0;

    /**
     * Creates a Node with left and right child and a parent
     *
     * @param <K> ist the key of the Node
     * @param <V> ist the Value of the Node
     */
    static private class Node<K, V> {
        K key;
        V value;
        int height;
        Node<K, V> left;
        Node<K, V> right;
        Node<K, V> parent;

        Node(K k, V v) {
            key = k;
            value = v;
            height = 0;
            left = null;
            right = null;
            parent = null;
        }
    }


    /**
     * The Minimum Entry of the Tree
     *
     * @param <K>
     * @param <V>
     */
    private static class MinEntry<K, V> {
        private K key;
        private V value;
    }

    /**
     * calls the recursive class insertR to insert a new Node
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the Value of the old Node
     */
    @Override
    public V insert(K key, V value) {
        root = insertR(key, value, root);
        if (root != null) {
            root.parent = null;
        }
        if (oldValue == null) {
            size++;
        }
        return oldValue;
    }

    /**
     * Inserts the Node in the right position
     *
     * @param key
     * @param value
     * @param p     is the root
     * @return the Value of the old Node
     */
    private Node<K, V> insertR(K key, V value, Node<K, V> p) {
        if (p == null) {
            p = new Node<>(key, value);
            oldValue = null;
        } else if (key.compareTo(p.key) < 0) {
            p.left = insertR(key, value, p.left);
            if (p.left != null) {
                p.left.parent = p;
            }
        } else if (key.compareTo(p.key) > 0) {
            p.right = insertR(key, value, p.right);
            if (p.right != null) {
                p.right.parent = p;
            }
        } else {
            oldValue = p.value;
            p.value = value;
        }
        modCount++;
        p = balance(p);
        return p;
    }


    /**
     * calls the recursive class searchR to search for a Node
     *
     * @param key the key whose associated value is to be returned.
     * @return the value of the searched node
     */
    @Override
    public V search(K key) {
        return searchR(key, root);
    }

    private V searchR(K key, Node<K, V> p) {
        if (p == null) {
            return null;
        } else if (key.compareTo(p.key) < 0) {
            return searchR(key, p.left);
        } else if (key.compareTo(p.key) > 0) {
            return searchR(key, p.right);
        } else {
            return p.value;
        }
    }

    /**
     * calls the recursive class removeR to remove a Node
     *
     * @param key key whose mapping is to be removed from the map.
     * @return the value of the removed Node
     */
    @Override
    public V remove(K key) {
        root = removeR(key, root);
        return oldValue;
    }

    /**
     * Searches an removees the Node
     *
     * @param key key of the to be removed node
     * @param p   root
     * @return the value of the removed Node or null if the node is not in the tree
     */
    private Node<K, V> removeR(K key, Node<K, V> p) {
        if (p == null) {
            oldValue = null;
        } else if (key.compareTo(p.key) < 0) {
            p.left = removeR(key, p.left);
        } else if (key.compareTo(p.key) > 0) {
            p.right = removeR(key, p.right);
        } else if (p.left == null || p.right == null) {
            oldValue = p.value;
            p = (p.left != null) ? p.left : p.right;
            modCount--;
            size--;
        } else {
            MinEntry<K, V> min = new MinEntry<>();
            p.right = getRemMinR(p.right, min);
            oldValue = p.value;
            p.key = min.key;
            p.value = min.value;
            modCount--;
            size--;
        }
        modCount++;
        p = balance(p);
        return p;
    }

    private Node<K, V> getRemMinR(Node<K, V> p, MinEntry<K, V> min) {
        assert p != null;
        if (p.left == null) {
            min.key = p.key;
            min.value = p.value;
            p = p.right;
        } else {
            p.left = getRemMinR(p.left, min);
        }
        p = balance(p);
        return p;
    }

    private Node<K, V> balance(Node<K, V> p) {
        if (p == null) {
            return null;
        }
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        if (getBalance(p.left) < 0) {
            if (getBalance(p.left) <= 0) {
                p = rotateRight(p);
            } else {
                p = rotateLeftRight(p);
            }
        } else if (getBalance(p) == +2) {
            if (getBalance(p.right) >= 0) {
                p = rotateLeft(p);
            } else {
                p = rotateRightLeft(p);
            }
        }
        return p;
    }

    private Node<K, V> rotateRight(Node<K, V> p) {
        assert p.left != null;
        Node<K, V> q = p.left;
        p.left = q.right;
        q.right = p;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    private Node<K, V> rotateLeft(Node<K, V> p) {
        assert p.right != null;
        Node<K, V> q = p.right;
        p.right = q.left;
        q.left = p;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    private Node<K, V> rotateLeftRight(Node<K, V> p) {
        assert p.left != null;
        p.left = rotateLeft(p.left);
        return rotateRight(p);
    }

    private Node<K, V> rotateRightLeft(Node<K, V> p) {
        assert p.right != null;
        p.right = rotateRight(p.right);
        return rotateLeft(p);
    }

    private int getHeight(Node<K, V> p) {
        if (p == null) {
            return -1;
        } else {
            return p.height;
        }
    }

    private int getBalance(Node<K, V> p) {
        if (p == null) {
            return 0;
        } else {
            return getHeight(p.right) - getHeight(p.left);
        }
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Iterator iterates over the whole tree and prints all the Nodes
     *
     * @return the current Node
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new BinaryTreeIterator();
    }

    private class BinaryTreeIterator implements Iterator<Entry<K, V>> {

        Node<K, V> currentNode = root;
        Stack<Node<K, V>> stack;

        public BinaryTreeIterator() {
            stack = new Stack<>();

            while (currentNode != null) {
                stack.push(currentNode);
                currentNode = currentNode.left;//left child
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public Entry<K, V> next() {
            Node<K, V> node = stack.pop();
            Entry<K, V> x = new Entry<>(node.key, node.value);
            if (node.right != null) {
                node = node.right;
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }
            return x;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Pretty prints the tree
     */
    public void prettyPrint() {
        printR(0, root);
    }

    private void printR(int level, Node<K, V> p) {
        printLevel(level);
        if (p == null) {
            System.out.println("#");
        } else {
            System.out.println(p.key + " " + p.value + "^" + ((p.parent == null) ? "null" : p.parent.key));
            if (p.left != null || p.right != null) {
                printR(level + 1, p.left);
                printR(level + 1, p.right);
            }
        }
    }

    private static void printLevel(int level) {
        if (level == 0) {
            return;
        }
        for (int i = 0; i < level - 1; i++) {
            System.out.print("   ");
        }
        System.out.print("|__");
    }
}
