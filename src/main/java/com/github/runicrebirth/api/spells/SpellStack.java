package com.github.runicrebirth.api.spells;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellStack {

    private final List<SpellComponent> components = new ArrayList<>();
    private Element element;

    public List<SpellComponent> components() {
        return Collections.unmodifiableList(components);
    }

    public int size() {
        return components.size();
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public void clear() {
        components.clear();
    }

    public boolean append(SpellComponent component) {
        if (component instanceof SpellModifier mod) {
            if (!mod.canAppendTo(components)) return false;
            String group = mod.exclusivityGroup();
            if (group != null) {
                for (SpellComponent c : components) {
                    if (c instanceof SpellModifier existing && group.equals(existing.exclusivityGroup())) {
                        return false;
                    }
                }
            }
        }
        components.add(component);
        return true;
    }

    public boolean validSpell() {
        for (SpellComponent c : components) {
            if (c instanceof SpellType) return true;
        }
        return false;
    }

    public SpellType resolveType() {
        for (SpellComponent c : components) {
            if (c instanceof SpellType t) return t;
        }
        return null;
    }

    public Element resolveElement() { return element; }

    public void setElement(Element element) { this.element = element; }

    public void compose(SpellParams params) {
        for (SpellComponent c : components) {
            if (c instanceof SpellModifier m) m.apply(params);
        }
    }
}
