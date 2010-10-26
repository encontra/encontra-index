package pt.inevo.encontra.btree;

import java.io.IOException;
import java.io.Serializable;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import pt.inevo.encontra.index.IndexEntry;

/**
 * Tuple Browser using the jdbm library.
 * @author Ricardo
 * @param <O>
 */
public class jdbmTupleBrowser<O extends IndexEntry<? extends Serializable, ? extends Serializable>> implements ITupleBrowser<O> {

    private TupleBrowser browser;

    protected jdbmTupleBrowser(TupleBrowser b){
        this.browser = b;
    }

    @Override
    public ITuple<O> getNext() {
        try {
            Tuple tuple = new Tuple();
            if (browser.getNext(tuple)) {
                ITuple<O> t = new jdbmTuple<O>(tuple);
                return t;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITuple<O> getPrevious() {
        try {
            Tuple tuple = new Tuple();
            if (browser.getPrevious(tuple)) {
                ITuple<O> t = new jdbmTuple<O>(tuple);
                return t;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}