package de.thedodo24.commonPackage.economy;

import com.arangodb.entity.BaseDocument;
import com.google.common.collect.Lists;
import de.thedodo24.commonPackage.arango.ArangoWritable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class ArmorStandHandler implements ArangoWritable<String> {

    String key;
    List<UUID> armorStands;

    public ArmorStandHandler(String key) {
        this.key = key;
        this.armorStands = Lists.newArrayList();
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public void read(BaseDocument document) {
        armorStands = ((List<String>) document.getProperties().getOrDefault("armorStands", Lists.newArrayList())).stream().map(UUID::fromString).collect(Collectors.toList());
    }

    @Override
    public void save(BaseDocument document) {
        document.addAttribute("armorStands", armorStands);
    }
}
