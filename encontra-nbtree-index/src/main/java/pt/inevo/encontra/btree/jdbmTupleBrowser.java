package pt.inevo.encontra.btree;

import java.io.IOException;
import java.io.Serializable;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

public class jdbmTupleBrowser<K extends Serializable, V extends Serializable> implements ITupleBrowser<K,V> {

    private TupleBrowser browser;

    protected jdbmTupleBrowser(TupleBrowser b){
        this.browser = b;
    }

    @Override
    public ITuple<K, V> getNext() {
        try {
            Tuple tuple = new Tuple();
            if (browser.getNext(tuple)) {
                ITuple<K, V> t = new jdbmTuple<K, V>(tuple);
                return t;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITuple<K, V> getPrevious() {
        try {
            Tuple tuple = new Tuple();
            if (browser.getPrevious(tuple)) {
                ITuple<K, V> t = new jdbmTuple<K, V>(tuple);
                return t;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}