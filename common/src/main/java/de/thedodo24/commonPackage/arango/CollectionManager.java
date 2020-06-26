package de.thedodo24.commonPackage.arango;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectionManager<Writable extends ArangoWritable<KeyType>, KeyType>
{
    protected ArangoCollection                     collection;
    protected Map<KeyType, Writable>               cache;
    protected CacheCondition<Writable, KeyType>    cacheCondition;
    protected WritableGenerator<Writable, KeyType> generator;
    private List<String> charList = Lists.newArrayList("!", "\"", "§", "$", "%", "&", "/",
            "(", ")", "=", "?", "`", "´", "+", "*", "#", "'", ":", ".", ";",
            ",", "<", ">", "~", "\\", "}", "]", "[", "{", "³", "²", "^", "°", "ß", "ü", "ä", "ö", "Ä", "Ö", "Ü");

    public CollectionManager(String collection, ArangoDatabase database, WritableGenerator<Writable, KeyType> generator)
    {
        this(collection, generator, database);
    }

    public CollectionManager(String collection, ArangoDatabase database, WritableGenerator<Writable, KeyType> generator,
                             CacheCondition<Writable, KeyType> cacheCondition)
    {
        this(collection, generator, database, cacheCondition);
    }

    public CollectionManager(String collection, WritableGenerator<Writable, KeyType> generator, ArangoDatabase database)
    {
        this(collection, generator, database, null);
    }

    public CollectionManager(String collection, WritableGenerator<Writable, KeyType> generator, ArangoDatabase database,
                             CacheCondition<Writable, KeyType> cacheCondition)
    {
        if (cacheCondition != null)
        {
            this.cache = new HashMap<>();
            this.cacheCondition = cacheCondition;
        }

        if (!database.collection(collection).exists())
            database.createCollection(collection);

        this.collection = database.collection(collection);
        this.generator = generator;
    }

    public boolean isCacheEnabled( )
    {
        return this.cache != null;
    }

    public Writable getOrGenerate(KeyType key)
    {
        return this.getOrGenerate(key, this.generator);
    }

    public Writable getOrGenerate(KeyType key, WritableGenerator<Writable, KeyType> generator)
    {
        Writable value;
        if ((value = this.get(key)) != null)
            return value;

        return this.register(key, generator);
    }

    public Writable get(KeyType key)
    {
        if(key instanceof String)
             if(charList.stream().anyMatch(((String) key)::contains)) {
                 return null;
             }
        if (this.isCacheEnabled() && this.cache.containsKey(key))
            return this.cache.get(key);

        Writable value = this.getNoCached(key);
        if (value != null && this.isCacheEnabled())
            if (this.cacheCondition.checkCache(key, value))
                this.cache.put(value.getKey(), value);

        return value;
    }

    public Writable getNoCached(KeyType key)
    {
        BaseDocument document;
        if(key != null) {
            if ((document = this.collection.getDocument(key.toString(), BaseDocument.class)) == null)
                return null;
            Writable value = this.generator.generate(key);
            value.read(document);

            return value;
        }
        return null;
    }

    public boolean saveCached(KeyType key)
    {
        if (this.isCacheEnabled())
            return false;

        Writable value;
        if ((value = this.cache.get(key)) != null)
            return this.save(value);

        System.err.println("[AdviAPI] CollectionManager: Requested key was not found in cache");

        return false;
    }

    public boolean save(Writable value)
    {
        if (value == null)
        {
            System.err.println("[AdviAPI] CollectionManager: Save-Value cannot be null");
            return false;
        }

        BaseDocument document = new BaseDocument();
        document.setKey(value.getKey().toString());
        value.save(document);

        this.collection.replaceDocument(value.getKey().toString(), document);

        if (this.isCacheEnabled() && !this.cache.containsKey(value.getKey()))
            this.cache.put(value.getKey(), value);

        return true;
    }

    public boolean reload(Writable value)
    {
        BaseDocument document;
        if ((document = this.collection.getDocument(value.getKey().toString(), BaseDocument.class)) == null)
            return false;

        value.read(document);

        return true;
    }

    public boolean delete(KeyType key)
    {
        this.uncache(key);

        return this.collection.deleteDocument(key.toString()) != null;
    }

    public Writable register(KeyType key)
    {
        return this.register(key, this.generator);
    }

    protected Writable register(KeyType key, WritableGenerator<Writable, KeyType> generator)
    {
        Writable data = generator.generate(key);

        BaseDocument document = new BaseDocument();
        document.setKey(key.toString());
        data.save(document);

        this.collection.insertDocument(document);

        return data;
    }

    public Writable uncache(KeyType key)
    {
        if (this.isCacheEnabled())
            return this.cache.remove(key);

        return null;
    }

    public void clearCache( )
    {
        if (this.isCacheEnabled())
            this.cache.clear();
    }

    protected void closeCursor(ArangoCursor<?> cursor)
    {
        try
        {
            cursor.close();
        }
        catch (Exception ignored) {}
    }

    public interface CacheCondition<Writable extends ArangoWritable<KeyType>, KeyType>
    {
        boolean checkCache(KeyType key, Writable obj);
    }

}
