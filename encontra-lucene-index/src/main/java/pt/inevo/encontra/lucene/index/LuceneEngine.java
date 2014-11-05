package pt.inevo.encontra.lucene.index;

import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

/**
 * Created by jpvguerreiro on 10/30/2014.
 */
public class LuceneEngine<O extends IEntity> extends AbstractSearcher<O> {

    @Override
    protected Result<O> getResultObject(Result<IEntry> entryresult) {
        return new Result<O>((O) storage.get(
                Long.parseLong((String) entryresult.getResultObject().getId())));
    }
}