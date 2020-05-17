package de.thedodo24.commonPackage.arango;

public interface WritableGenerator<Writable extends ArangoWritable<KeyType>, KeyType>
{
    Writable generate(KeyType key);
}
