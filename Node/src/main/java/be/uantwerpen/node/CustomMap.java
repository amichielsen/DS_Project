package be.uantwerpen.node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CustomMap<K, V> {

    private Map<K, ArrayList<V>> customMap;

    //Constructor that initiates the LinkedHashMap of type K and V
    public CustomMap() {
        customMap = new LinkedHashMap<K, ArrayList<V>>();
    }


    /*
     * Checks if the input key is already present in the Map
     * If not then create a ArrayList of type V
     * Adds the value to the ArrayList and updates the Map
     * Returns the last updated value
     */

    public V put(K key, V value) {
        ArrayList<V> l = null;
        if (customMap.get(key) == null) {
            l = new ArrayList<V>();
        } else {
            l = customMap.get(key);
        }
        l.add(value);
        customMap.put(key, l);
        return value;
    }


    //Removes a key-value mapping
    public void remove(K key) {
        customMap.remove(key);
    }

    /*
     * Removes a first occurrence of the given value
     * from the ArrayList
     * And updates the Map with new ArrayList
     *
     */
    public boolean removeValue(K key, V value) {
        boolean result = false;
        if (null != customMap.get(key)) {
            ArrayList<V> l = customMap.get(key);
            result = l.remove(value);
            customMap.put(key, l);
            return result;
        }
        return result;
    }

    /*
     * Removes all the values from the ArrayList
     * The key will be mapped to an empty ArrayList
     */
    public boolean removeAllValues(K key) {
        boolean result = false;

        if (null != customMap.get(key)) {
            customMap.put(key, new ArrayList<V>());
            result = true;
        }
        return result;
    }

    /*
     *
     * Returns the ArrayList mapped against the given key
     *
     */
    public ArrayList<V> get(K key) {
        return customMap.get(key);
    }

    /*
     *
     * Returns the key view of the Map
     *
     */
    public Set<K> keySet() {
        return customMap.keySet();
    }
}