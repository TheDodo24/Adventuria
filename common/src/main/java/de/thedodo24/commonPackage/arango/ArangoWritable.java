package de.thedodo24.commonPackage.arango;

import com.arangodb.entity.BaseDocument;

import java.util.Map;

public interface ArangoWritable<KeyType>
{
    KeyType getKey();

    void read(BaseDocument document);
    void save(BaseDocument document);

    interface SimpleArangoWritable<KeyType> extends ArangoWritable<KeyType>
    {
        void read(Map<String, Object> data);
        Map<String, Object> save();

        @Override
        default void read(BaseDocument document)
        {
            read(document.getProperties());
        }

        @Override
        default void save(BaseDocument document)
        {
            document.getProperties().putAll(save());
        }
    }
}
